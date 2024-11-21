package team9.ddang.global.config.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

//    private final ChatRoomRepository chatRoomRepository;
//    private final ChatMemberRepository chatMemberRepository;

//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//
//        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//            String destination = accessor.getDestination(); // 예: /sub/chat/{chatRoomId}
//            Long chatRoomId = extractChatRoomId(destination);
//
//            Principal principal = accessor.getUser();
//            if (principal == null) {
//                throw new AccessDeniedException("인증되지 않은 사용자입니다.");
//            }
//
//            Long memberId = Long.valueOf(principal.getName());
//
//            boolean isParticipant = chatMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, memberId);
//            if (!isParticipant) {
//                throw new AccessDeniedException("해당 채팅방에 접근할 권한이 없습니다.");
//            }
//        }
//
//        return message;
//    }
//
//    private Long extractChatRoomId(String destination) {
//        return Long.parseLong(destination.split("/chat/")[1]);
//    }

}

// TODO : jwt 인증 절차 필요
// TODO : 시큐리티 완료시 구독 유효성 검증 활성화시키기
