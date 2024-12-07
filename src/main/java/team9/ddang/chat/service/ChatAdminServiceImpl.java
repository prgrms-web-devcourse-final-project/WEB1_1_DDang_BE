package team9.ddang.chat.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team9.ddang.global.service.S3Service;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatAdminServiceImpl implements ChatAdminService {

    private final S3Service s3Service;
    private final AmazonS3Client amazonS3Client;

    public List<Map<String, String>> extractMessagesFromCsv(LocalDate startDate, LocalDate endDate, Long chatRoomId) {
        try {
            List<String> files = listFilesInArchiveFolder();

            List<String> filteredFiles = files.stream()
                    .filter(file -> isFileInRange(file, startDate, endDate))
                    .toList();

            List<Map<String, String>> results = new ArrayList<>();

            for (String fileKey : filteredFiles) {
                File localFile = s3Service.downloadChatFile(fileKey);

                try {
                    results.addAll(filterMessagesByChatRoomId(localFile, chatRoomId));
                } finally {
                    if (localFile.exists()) {
                        localFile.delete();
                    }
                }
            }

            return results;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract messages from S3 CSV files", e);
        }
    }

    private List<String> listFilesInArchiveFolder() {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withPrefix("archive/");
        ListObjectsV2Result result = amazonS3Client.listObjectsV2(request);

        return result.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .toList();
    }

    private boolean isFileInRange(String fileName, LocalDate startDate, LocalDate endDate) {
        try {
            String[] parts = fileName.replace("archive/chats_archive_", "").replace(".csv", "").split("_");
            LocalDate fileStartDate = LocalDate.parse(parts[0], DateTimeFormatter.ofPattern("yyyyMMdd"));
            LocalDate fileEndDate = LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyyMMdd"));

            return !(fileEndDate.isBefore(startDate) || fileStartDate.isAfter(endDate));
        } catch (Exception e) {
            log.error("Invalid file name format: {}", fileName, e);
            return false;
        }
    }

    private String getTempFilePath(String fileKey) {
        return System.getProperty("java.io.tmpdir") + "/" + fileKey.substring(fileKey.lastIndexOf('/') + 1);
    }

    private List<Map<String, String>> filterMessagesByChatRoomId(File csvFile, Long chatRoomId) throws IOException {
        List<Map<String, String>> results = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                Map<String, String> message = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    message.put(headers[i], fields[i]);
                }

                if (message.get("ChatRoomId").equals(String.valueOf(chatRoomId))) {
                    results.add(message);
                }
            }
        }
        return results;
    }
}