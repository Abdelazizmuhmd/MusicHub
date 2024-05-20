package songservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"songservice.*","common.*"})
@EnableJpaRepositories(basePackages = {"common.Repository"})
@EntityScan(basePackages = "common.entity")
@PropertySource("classpath:common.properties")
public class SongService {
    public static void main(String[] args) {
        SpringApplication.run(SongService.class, args);
    }
}