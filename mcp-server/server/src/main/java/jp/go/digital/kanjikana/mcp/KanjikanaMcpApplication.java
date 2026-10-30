package jp.go.digital.kanjikana.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KanjikanaMcpApplication {
    public static void main(String[] args) {
        System.setProperty("logback.statusListenerClass", "ch.qos.logback.core.status.NopStatusListener");
        SpringApplication.run(KanjikanaMcpApplication.class, args);
    }
}