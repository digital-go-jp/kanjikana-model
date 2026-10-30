package jp.go.digital.kanjikana.mcp.sample;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class KanjikanaClientSample {
    public static void main(String[] args) {
        try (ConfigurableApplicationContext context = SpringApplication.run(KanjikanaClientSample.class, args)) {
            List<?> clients = context.getBean("mcpSyncClients", List.class);
            McpSyncClient client = clients.stream()
                .filter(McpSyncClient.class::isInstance)
                .map(McpSyncClient.class::cast)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("MCPクライアントが構成されていません。"));

            System.out.println("利用可能なツール:");
            client.listTools().tools().forEach(tool -> System.out.println("- " + tool.name()));

            var result = client.callTool(new CallToolRequest("match_name_simple", Map.of(
                    "kanji", "山田　太郎",
                    "kana", "ヤマダ　タロウ")));

            System.out.println("\nmatch_name_simple の結果:");
            result.content().stream()
                    .filter(TextContent.class::isInstance)
                    .map(TextContent.class::cast)
                    .forEach(content -> System.out.println(content.text()));

            if (Boolean.TRUE.equals(result.isError())) {
                throw new IllegalStateException("MCPツールの呼び出しに失敗しました。");
            }
        }
    }
}