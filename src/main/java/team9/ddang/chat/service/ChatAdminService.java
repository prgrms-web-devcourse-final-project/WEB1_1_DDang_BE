package team9.ddang.chat.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ChatAdminService {
    List<Map<String, String>> extractMessagesFromCsv(LocalDate startDate, LocalDate endDate, Long chatRoomId);
}
