#!/usr/bin/env python3
"""Test script to verify FastMCP tool integration."""

import sys
import traceback

async def test_fastmcp_tools():
    """Test FastMCP tool integration."""
    try:
        print("Testing FastMCP tool integration...")
        
        from app.tools.market_tools import mcp as mcp_app
        print("✅ Successfully imported FastMCP market tools")
        
        tools = await mcp_app.list_tools()
        if tools:
            print(f"✅ Available tools: {len(tools)} tools found")
            for i, tool in enumerate(tools):
                if hasattr(tool, 'name'):
                    print(f"  - {tool.name}: {getattr(tool, 'description', 'No description')}")
                else:
                    print(f"  - Tool {i}: {tool}")
        else:
            print("❌ No tools found in FastMCP app")
            return False
        
        resources = await mcp_app.list_resources()
        if resources:
            print(f"✅ Available resources: {len(resources)} resources found")
            for i, resource in enumerate(resources):
                if hasattr(resource, 'name'):
                    print(f"  - {resource.name}: {getattr(resource, 'description', 'No description')}")
                else:
                    print(f"  - Resource {i}: {resource}")
        else:
            print("ℹ️  No resources found (this is optional)")
        
        print("✅ FastMCP tool integration test completed successfully")
        return True
        
    except ImportError as e:
        print(f"❌ Import error: {e}")
        print("This indicates missing dependencies or module structure issues")
        return False
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        print("Full traceback:")
        traceback.print_exc()
        return False

if __name__ == "__main__":
    import asyncio
    success = asyncio.run(test_fastmcp_tools())
    sys.exit(0 if success else 1)
