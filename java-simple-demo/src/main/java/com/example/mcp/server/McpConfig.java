package com.example.mcp.server;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapperSupplier;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpStatelessServerFeatures;
import io.modelcontextprotocol.server.McpStatelessSyncServer;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import org.springframework.ai.mcp.server.webmvc.transport.WebMvcStatelessServerTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Map;

@Configuration
public class McpConfig {

    private static final String MCP_ENDPOINT = "/mcp";

    @Bean
    public McpJsonMapper mcpJsonMapper() {
        return new JacksonMcpJsonMapperSupplier().get();
    }

    @Bean
    public WebMvcStatelessServerTransport webMvcStatelessServerTransport(McpJsonMapper jsonMapper) {
        return WebMvcStatelessServerTransport.builder()
            .jsonMapper(jsonMapper)
            .messageEndpoint(MCP_ENDPOINT)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(WebMvcStatelessServerTransport transport) {
        return transport.getRouterFunction();
    }

    @Bean
    public McpStatelessSyncServer mcpServer(
            WebMvcStatelessServerTransport transport,
            McpJsonMapper jsonMapper,
            CalculatorService service) {
        String numberPairSchema = """
            {"type":"object","properties":{"a":{"type":"number","description":"First number"},"b":{"type":"number","description":"Second number"}},"required":["a","b"]}
            """;

        var sumTool = McpStatelessServerFeatures.SyncToolSpecification.builder()
            .tool(Tool.builder()
                .name("sum")
                .description("Calculate the sum of two numbers")
                .inputSchema(jsonMapper, numberPairSchema)
                .build())
            .callHandler((context, request) -> {
                Map<String, Object> args = request.arguments();
                double result = service.sum(asDouble(args.get("a")), asDouble(args.get("b")));
                return CallToolResult.builder()
                    .content(List.of(new TextContent(String.format("{\"value\": %s}", result))))
                    .isError(false)
                    .build();
            })
            .build();

        var subTool = McpStatelessServerFeatures.SyncToolSpecification.builder()
            .tool(Tool.builder()
                .name("subtract")
                .description("Calculate the difference between two numbers (a - b)")
                .inputSchema(jsonMapper, numberPairSchema)
                .build())
            .callHandler((context, request) -> {
                Map<String, Object> args = request.arguments();
                double result = service.subtract(asDouble(args.get("a")), asDouble(args.get("b")));
                return CallToolResult.builder()
                    .content(List.of(new TextContent(String.format("{\"value\": %s}", result))))
                    .isError(false)
                    .build();
            })
            .build();

        return McpServer.sync(transport)
            .serverInfo("mcp-demo", "1.0.0")
            .capabilities(ServerCapabilities.builder()
                .tools(true)
                .build())
            .tools(sumTool, subTool)
            .build();
    }

    private static double asDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        throw new IllegalArgumentException("Expected a number, got: " + value);
    }
}
