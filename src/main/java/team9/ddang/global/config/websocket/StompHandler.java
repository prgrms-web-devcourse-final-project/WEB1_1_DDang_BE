package team9.ddang.global.config.websocket;

import lombok.RequiredArgsConstructor;
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
import team9.ddang.chat.repository.ChatRoomRepository;
import team9.ddang.member.jwt.service.JwtService;

import java.security.Principal;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        try {
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                handleConnect(accessor);
            }

            if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                handleSubscribe(accessor);
            }
        } catch (Exception e) {
            System.err.println("Error in StompHandler: " + e.getMessage());
            e.printStackTrace();
            throw e;
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

        System.out.println("User connected: " + email);
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
        System.out.println("User subscribing: " + email);

        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/sub/chat/")) {
            Long chatRoomId = extractChatRoomId(destination);

            boolean isParticipant = chatMemberRepository.existsByChatRoomIdAndEmail(chatRoomId, email);
            if (!isParticipant) {
                throw new IllegalArgumentException("해당 채팅방에 접근할 권한이 없습니다.");
            }
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

    private String extractToken(SimpMessageHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        System.out.println("Authorization header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Authorization 헤더가 없거나 잘못되었습니다.");
    }
}
