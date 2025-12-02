"""
MCP Client Test Script for python-simple-demo server.

This script tests all MCP features exposed by the server:
- Tools: health_check, get_weather
- Resources: static (README, config) + template (file://{path})
- Prompts: code_review

Usage:
    python3 test_server.py
"""

import asyncio
import sys
from pathlib import Path

from fastmcp import Client
from fastmcp.client.transports import StdioTransport


async def test_server():
    """Test the MCP server using STDIO transport (subprocess)."""
    
    # Get the path to server.py relative to this test file
    server_path = Path(__file__).parent / "server.py"
    
    print("=" * 60)
    print("🚀 MCP Client Test - python-simple-demo")
    print("=" * 60)
    print(f"📂 Server path: {server_path}")
    print()
    
    # Create STDIO transport to spawn server as subprocess
    # Note: Use "python3" on macOS/Linux, "python" on Windows
    python_cmd = "python3" if sys.platform != "win32" else "python"
    
    transport = StdioTransport(
        command=python_cmd,
        args=[str(server_path)],
        cwd=str(server_path.parent)  # Set working directory to server's folder
    )
    
    async with Client(transport) as client:
        
        # =============================================
        # 1. TEST TOOLS
        # =============================================
        print("=" * 60)
        print("🔧 TESTING TOOLS")
        print("=" * 60)
        
        # List available tools
        tools = await client.list_tools()
        print(f"\n📋 Found {len(tools)} tool(s):")
        for tool in tools:
            print(f"   - {tool.name}: {tool.description}")
        
        # Test health_check tool
        print("\n>>> Calling health_check tool...")
        result = await client.call_tool("health_check", {})
        # CallToolResult has .content (list) and .data attributes
        print(f"<<< Result: {result.content[0].text}")
        
        # Test get_weather tool
        print("\n>>> Calling get_weather tool for 'Paris'...")
        result = await client.call_tool("get_weather", {"location": "Paris"})
        print(f"<<< Result: {result.content[0].text}")
        
        # =============================================
        # 2. TEST RESOURCES
        # =============================================
        print("\n" + "=" * 60)
        print("📦 TESTING RESOURCES")
        print("=" * 60)
        
        # List available resources (only static resources appear here)
        resources = await client.list_resources()
        print(f"\n📋 Found {len(resources)} static resource(s):")
        for resource in resources:
            print(f"   - {resource.uri}: {resource.name}")
        
        # Read the config resource (TextResource)
        print("\n>>> Reading resource://config (TextResource)...")
        result = await client.read_resource("resource://config")
        # read_resource returns list[ReadResourceContents]
        # Each item has .text (for text) or .blob (for binary)
        content = result[0].text if hasattr(result[0], 'text') else str(result[0])
        print(f"<<< Content: {content}")
        
        # Read a static FileResource (README.md)
        # Find the README resource from the list
        readme_resource = next((r for r in resources if "README" in r.name), None)
        if readme_resource:
            print(f"\n>>> Reading {readme_resource.uri} (FileResource)...")
            result = await client.read_resource(str(readme_resource.uri))
            content = result[0].text if hasattr(result[0], 'text') else str(result[0])
            content_preview = content[:150] + "..." if len(content) > 150 else content
            print(f"<<< Content preview: {content_preview}")
        
        # =============================================
        # 3. TEST PROMPTS
        # =============================================
        print("\n" + "=" * 60)
        print("💬 TESTING PROMPTS")
        print("=" * 60)
        
        # List available prompts
        prompts = await client.list_prompts()
        print(f"\n📋 Found {len(prompts)} prompt(s):")
        for prompt in prompts:
            print(f"   - {prompt.name}: {prompt.description}")
        
        # Get the code_review prompt
        print("\n>>> Getting code_review prompt for 'Python'...")
        result = await client.get_prompt("code_review", {"language": "Python"})
        print(f"<<< Prompt messages:")
        for msg in result.messages:
            role = msg.role
            # Content can be TextContent with .text or just a string
            if hasattr(msg.content, 'text'):
                content = msg.content.text
            else:
                content = str(msg.content)
            preview = content[:100] + "..." if len(content) > 100 else content
            print(f"    [{role}]: {preview}")
        
        # =============================================
        # SUMMARY
        # =============================================
        print("\n" + "=" * 60)
        print("✅ ALL TESTS COMPLETED SUCCESSFULLY")
        print("=" * 60)
        print("   Tools tested: health_check, get_weather")
        print("   Resources tested: resource://config, file://...README.md")
        print("   Prompts tested: code_review")
        print()


if __name__ == "__main__":
    asyncio.run(test_server())
