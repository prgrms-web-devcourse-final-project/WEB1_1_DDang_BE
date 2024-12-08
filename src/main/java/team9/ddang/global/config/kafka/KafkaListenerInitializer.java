//package team9.ddang.global.config.kafka;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import team9.ddang.chat.repository.ChatRoomRepository;
//import team9.ddang.chat.service.KafkaDynamicListenerService;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class KafkaListenerInitializer {
//
//    private final KafkaDynamicListenerService kafkaDynamicListenerService;
//    private final ChatRoomRepository chatRoomRepository;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void initializeKafkaListeners() {
//        List<Long> chatRoomIds = chatRoomRepository.findAllActiveChatRoomIds();
//
//        for (Long chatRoomId : chatRoomIds) {
//            kafkaDynamicListenerService.addListenerForChatRoom(chatRoomId);
//        }
//
//        log.info("Initialized Kafka listeners for {} chat rooms.", chatRoomIds.size());
//    }
//}