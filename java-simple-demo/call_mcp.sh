#!/bin/bash

# MCP Server Demo - Inspector smoke tests (Streamable HTTP)
# Start the server first: java -jar target/mcp-server-demo-0.0.1-SNAPSHOT.jar

set -euo pipefail

MCP_SERVER="${MCP_SERVER:-http://localhost:8080/mcp}"
INSPECTOR=(npx --yes @modelcontextprotocol/inspector@2 --cli "${MCP_SERVER}" --transport http)

echo "================================================"
echo "MCP Server Demo - Inspector smoke tests"
echo "Endpoint: ${MCP_SERVER}"
echo "================================================"
echo ""

echo ">>> tools/list"
"${INSPECTOR[@]}" --method tools/list
echo ""

echo ">>> tools/call sum (10 + 5)"
"${INSPECTOR[@]}" --method tools/call --tool-name sum --tool-args-json '{"a":10,"b":5}'
echo ""

echo ">>> tools/call subtract (10 - 5)"
"${INSPECTOR[@]}" --method tools/call --tool-name subtract --tool-args-json '{"a":10,"b":5}'
echo ""

echo "================================================"
echo "Smoke tests completed"
echo "================================================"
