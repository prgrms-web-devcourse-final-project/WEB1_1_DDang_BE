package team9.ddang.global.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ChatBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job chatArchiveJob;

    @Scheduled(cron = "0 0 3 ? * SUN")
    public void runChatArchiveJob() {
        try {
            jobLauncher.run(chatArchiveJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            log.error("Batch job failed: {}", e.getMessage(), e);
        }
    }
}