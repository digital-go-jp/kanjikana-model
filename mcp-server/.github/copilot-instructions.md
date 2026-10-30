# MCP Server Notes

- This server uses the [Spring AI MCP Server Boot Starter](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html) and its stdio transport.
- Keep stdout reserved for MCP protocol messages. Diagnostics must go to stderr.
- Tool implementations should call the `core` Java source directly; do not call the REST API.
- Tool methods use Spring AI `@McpTool` annotations; see the [server annotations guide](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-annotations-server.html).