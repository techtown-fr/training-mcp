package com.example.mcp.server;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

@Configuration
@EnableWebMvc
public class McpConfig {

    private static final String MESSAGE_ENDPOINT = "/messages";

    @Bean
    public McpJsonMapper mcpJsonMapper() {
        return McpJsonMapper.createDefault();
    }

    @Bean
    public WebMvcSseServerTransportProvider webMvcSseServerTransportProvider(McpJsonMapper jsonMapper) {
        return new WebMvcSseServerTransportProvider.Builder()
            .jsonMapper(jsonMapper)
            .messageEndpoint(MESSAGE_ENDPOINT)
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(WebMvcSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    @Bean
    public McpSyncServer mcpServer(WebMvcSseServerTransportProvider transportProvider, 
                                    McpJsonMapper jsonMapper, 
                                    CalculatorService service) {
        // Tool: sum - using Tool.Builder with JSON schema string
        String sumSchema = """
            {"type":"object","properties":{"a":{"type":"number","description":"First number"},"b":{"type":"number","description":"Second number"}},"required":["a","b"]}
            """;
            
        var sumTool = new Tool.Builder()
            .name("sum")
            .description("Calculate the sum of two numbers")
            .inputSchema(jsonMapper, sumSchema)
            .build();
        
        var sumToolSpec = new McpServerFeatures.SyncToolSpecification(
            sumTool,
            (exchange, args) -> {
                double a = ((Number) args.get("a")).doubleValue();
                double b = ((Number) args.get("b")).doubleValue();
                double result = service.sum(a, b);
                // Use TextContent for the result
                return new CallToolResult(
                    List.of(new TextContent(String.format("{\"value\": %s}", result))),
                    false
                );
            }
        );

        // Tool: subtract - using Tool.Builder with JSON schema string
        String subSchema = """
            {"type":"object","properties":{"a":{"type":"number","description":"First number"},"b":{"type":"number","description":"Second number"}},"required":["a","b"]}
            """;

        var subTool = new Tool.Builder()
            .name("subtract")
            .description("Calculate the difference between two numbers (a - b)")
            .inputSchema(jsonMapper, subSchema)
            .build();
        
        var subToolSpec = new McpServerFeatures.SyncToolSpecification(
            subTool,
            (exchange, args) -> {
                double a = ((Number) args.get("a")).doubleValue();
                double b = ((Number) args.get("b")).doubleValue();
                double result = service.subtract(a, b);
                // Use TextContent for the result
                return new CallToolResult(
                    List.of(new TextContent(String.format("{\"value\": %s}", result))),
                    false
                );
            }
        );

        var server = McpServer.sync(transportProvider)
            .serverInfo("mcp-demo", "1.0.0")
            .capabilities(ServerCapabilities.builder()
                .tools(true)
                .logging()
                .build())
            .tools(sumToolSpec, subToolSpec)
            .build();

        return server;
    }
}
