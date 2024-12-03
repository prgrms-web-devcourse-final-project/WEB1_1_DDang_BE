package team9.ddang.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import team9.ddang.global.config.websocket.StompHandler;
import team9.ddang.global.event.WebSocketMessageEvent;

@Service
@RequiredArgsConstructor
public class WebSocketMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final StompHandler stompHandler;

    public void broadcastMessage(String destination, Object message) {
        messagingTemplate.convertAndSend(destination, message);
    }

    @EventListener
    public void handleWebSocketMessageEvent(WebSocketMessageEvent event) {
        System.out.println(event.getDestination());
        messagingTemplate.convertAndSend(event.getDestination(), event.getPayload());
    }
}
