package team9.ddang.global.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import team9.ddang.chat.repository.ChatMemberRepository;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.global.api.WebSocketResponse;
import team9.ddang.global.event.WebSocketMessageEvent;
import team9.ddang.member.jwt.service.JwtService;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        try {
            if (accessor.getCommand() == null) {
                return message;
            }

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                handleConnect(accessor);
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscribe(accessor);
            }

            if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                handleDisconnect(accessor);
            }
        } catch (Exception e) {
            log.error("Error in StompHandler: {}", e.getMessage(), e);
            accessor.getSessionAttributes().put("error", e.getMessage());
            return null;
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = extractToken(accessor);

        if (token == null || !jwtService.isTokenValid(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtService.extractEmail(token)
                .orElseThrow(() -> new IllegalArgumentException("토큰에서 이메일을 추출할 수 없습니다."));

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, Collections.emptyList());
        accessor.setUser(authentication);

        accessor.getSessionAttributes().put("user", authentication);
        accessor.getSessionAttributes().put("token", token);

        log.info("User connected: {}", email);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();

        if (principal == null) {
            principal = (Principal) accessor.getSessionAttributes().get("user");
        }

        if (principal == null) {
            throw new IllegalArgumentException("구독 요청 시 인증되지 않은 사용자입니다.");
        }

        String email = principal.getName();
        String destination = accessor.getDestination();
        if (destination == null) {
            throw new IllegalArgumentException("구독 요청에 destination 정보가 없습니다.");
        }
        log.info("User subscribing: {} to destination: {}", email, destination);

        if (destination.startsWith("/sub/chat/")) {
            Long chatRoomId = extractChatRoomId(destination);

            boolean isParticipant = chatMemberRepository.existsByChatRoomIdAndEmail(chatRoomId, email);
            if (!isParticipant) {
                throw new IllegalArgumentException("해당 채팅방에 접근할 권한이 없습니다.");
            }
        } else if (destination.startsWith("/sub/message/")) {
            handleMessageSubscription(destination, email);
        }
    }

    private void handleMessageSubscription(String destination, String authenticatedEmail) {
        String targetEmail = extractEmailFromDestination(destination);

        if (!authenticatedEmail.equals(targetEmail)) {
            throw new IllegalArgumentException("해당 메시지 경로를 구독할 권한이 없습니다.");
        }

        scheduler.schedule(() -> {
            List<Object[]> unreadCounts = chatRepository.countUnreadMessagesByMemberEmail(targetEmail);

            List<Map<String, Object>> responseData = unreadCounts.stream()
                    .map(result -> Map.of(
                            "chatRoomId", result[0],
                            "unreadCount", result[1]
                    ))
                    .toList();

            eventPublisher.publishEvent(
                    new WebSocketMessageEvent(
                            this,
                            destination,
                            WebSocketResponse.ok(responseData) // 데이터를 WebSocket 응답 형태로 래핑
                    )
            );
        }, 100, TimeUnit.MILLISECONDS); // 100ms 대기
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        Principal principal = accessor.getUser();
        if (principal == null) {
            principal = (Principal) accessor.getSessionAttributes().get("user");
        }

        if (principal != null) {
            String email = principal.getName();
            log.info("User disconnected: {}", email);
        } else {
            log.info("Unknown user disconnected.");
        }
    }

    private Long extractChatRoomId(String destination) {
        if (destination == null || !destination.contains("/chat/")) {
            throw new IllegalArgumentException("잘못된 구독 경로입니다: " + destination);
        }
        try {
            return Long.parseLong(destination.split("/chat/")[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("채팅방 ID를 파싱할 수 없습니다: " + destination, e);
        }
    }

    private String extractEmailFromDestination(String destination) {
        if (destination == null || !destination.startsWith("/sub/message/")) {
            throw new IllegalArgumentException("잘못된 메시지 구독 경로입니다: " + destination);
        }
        return destination.substring("/sub/message/".length());
    }

    private String extractToken(SimpMessageHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        String token = (String) accessor.getSessionAttributes().get("token");
        if (token != null) {
            return token;
        }

        throw new IllegalArgumentException("Authorization 헤더 또는 세션에 토큰이 없습니다.");
    }
}
