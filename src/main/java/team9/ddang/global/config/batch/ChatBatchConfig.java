package team9.ddang.global.config.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import team9.ddang.chat.entity.Chat;
import team9.ddang.chat.repository.ChatRepository;
import team9.ddang.global.service.S3Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ChatBatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final S3Service s3Service;
    private final ChatRepository chatRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Bean
    public Job chatArchiveJob(JobRepository jobRepository, Step chatArchiveStep) {
        return new JobBuilder("chatArchiveJob", jobRepository)
                .start(chatArchiveStep)
                .build();
    }

    @Bean
    public Step chatArchiveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chatArchiveStep", jobRepository)
                .<Chat, Chat>chunk(100, transactionManager)
                .reader(chatItemReader())
                .writer(chatItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Chat> chatItemReader() {
        return new JpaPagingItemReaderBuilder<Chat>()
                .name("chatItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Chat c WHERE c.createdAt < :twoWeeksAgo")
                .parameterValues(Map.of("twoWeeksAgo", LocalDateTime.now().minusWeeks(2)))
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemWriter<Chat> chatItemWriter() {
        return chunk -> {
            List<? extends Chat> chats = chunk.getItems();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileName = "chats_archive_" + timestamp + ".csv";

            File csvFile = createCsvFile(chats, fileName);

            s3Service.uploadChatFile(bucket, "archive/" + fileName, csvFile);

            csvFile.delete();

            chatRepository.deleteChatsOlderThan(LocalDateTime.now().minusWeeks(2));
        };
    }

    private File createCsvFile(List<? extends Chat> chats, String filePath) throws IOException {
        File file = new File(filePath);
        try (var writer = new FileWriter(file)) {
            writer.write("ID,Text,CreatedAt,ChatRoomId,MemberId,ChatType,IsRead\n");
            for (Chat chat : chats) {
                writer.write(String.format("%d,%s,%s,%d,%d,%s,%s\n",
                        chat.getChatId(),
                        sanitize(chat.getText()),
                        chat.getCreatedAt(),
                        chat.getChatRoom().getChatroomId(),
                        chat.getMember().getMemberId(),
                        chat.getChatType(),
                        chat.getIsRead()));
            }
        }
        return file;
    }

    private String sanitize(String text) {
        if (text == null) return "";
        return text.replaceAll(",", " ");
    }
}
