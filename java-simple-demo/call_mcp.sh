#!/bin/bash

# MCP Server Demo - Test Script
# This script demonstrates how to interact with the MCP server over HTTP/SSE

set -e

# Configuration
export MCP_SERVER="http://localhost:8081"

echo "================================================"
echo "MCP Server Demo - Test Script"
echo "================================================"
echo ""
echo "Step 1: Obtain an MCP session token"
echo "Note: This will start an SSE stream. Keep this terminal open!"
echo ""
echo "Run the following command in a SEPARATE terminal:"
echo "  curl \"${MCP_SERVER}/sse\""
echo ""
echo "You will get an endpoint like:"
echo "  id: abc123-def456-ghi789"
echo "  event: endpoint"
echo "  data: /messages"
echo ""
echo "Your MCP_ENDPOINT will be:"
echo "  http://localhost:8081/messages?sessionId=<the-id-from-above>"
echo ""
read -p "Press ENTER when you have obtained the sessionId..."

echo ""
read -p "Enter the full MCP endpoint URL (with sessionId): " MCP_ENDPOINT
export MCP_ENDPOINT

echo ""
echo "================================================"
echo "Step 2: Initialize the MCP session"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2025-06-18",
    "clientInfo": {
        "name": "mcp-client",
        "version": "1.0.0"
    }
  }
}'

echo ""
echo ""
echo "================================================"
echo "Step 2.5: Send initialized notification (REQUIRED!)"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "method": "notifications/initialized"
}'

echo ""
echo ""
echo "================================================"
echo "Step 3: List available MCP tools"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/list"
}'

echo ""
echo ""
echo "================================================"
echo "Step 4: Call the 'sum' tool (10 + 5)"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
        "name": "sum",
        "arguments": {
            "a": 10,
            "b": 5
        }
    }
}'

echo ""
echo ""
echo "================================================"
echo "Step 5: Call the 'subtract' tool (10 - 5)"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
    "jsonrpc": "2.0",
    "id": 4,
    "method": "tools/call",
    "params": {
        "name": "subtract",
        "arguments": {
            "a": 10,
            "b": 5
        }
    }
}'

echo ""
echo ""
echo "================================================"
echo "Step 6: Ping the MCP server"
echo "================================================"
echo ""

curl -X POST "${MCP_ENDPOINT}" -H "Content-Type: application/json" -d '{
  "jsonrpc": "2.0",
  "id": 5,
  "method": "ping"
}'

echo ""
echo ""
echo "================================================"
echo "Test completed successfully!"
echo "================================================"

