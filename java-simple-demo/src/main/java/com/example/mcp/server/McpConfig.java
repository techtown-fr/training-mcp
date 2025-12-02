package com.example.mcp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public WebMvcSseServerTransportProvider webMvcSseServerTransportProvider(ObjectMapper objectMapper) {
        return new WebMvcSseServerTransportProvider(objectMapper, MESSAGE_ENDPOINT);
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(WebMvcSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    @Bean
    public McpSyncServer mcpServer(WebMvcSseServerTransportProvider transportProvider, CalculatorService service) {
        // Tool: sum
        String sumSchema = """
            {"type":"object","properties":{"a":{"type":"number","description":"First number"},"b":{"type":"number","description":"Second number"}},"required":["a","b"]}
            """;
            
        var sumTool = new Tool("sum", "Calculate the sum of two numbers", sumSchema);
        
        var sumToolSpec = new McpServerFeatures.SyncToolSpecification(
            sumTool,
            (exchange, args) -> {
                double a = ((Number) args.get("a")).doubleValue();
                double b = ((Number) args.get("b")).doubleValue();
                double result = service.sum(a, b);
                return new CallToolResult(List.of(new TextContent(String.valueOf(result))), false);
            }
        );

        // Tool: subtract
        String subSchema = """
            {"type":"object","properties":{"a":{"type":"number","description":"First number"},"b":{"type":"number","description":"Second number"}},"required":["a","b"]}
            """;

        var subTool = new Tool("subtract", "Calculate the difference between two numbers (a - b)", subSchema);
        
        var subToolSpec = new McpServerFeatures.SyncToolSpecification(
            subTool,
            (exchange, args) -> {
                double a = ((Number) args.get("a")).doubleValue();
                double b = ((Number) args.get("b")).doubleValue();
                double result = service.subtract(a, b);
                return new CallToolResult(List.of(new TextContent(String.valueOf(result))), false);
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

