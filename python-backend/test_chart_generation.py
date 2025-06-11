#!/usr/bin/env python3
"""Test script to verify chart generation functionality."""

import asyncio
import sys
import traceback
import json
from datetime import datetime, timedelta

async def test_chart_generation():
    """Test chart generation functionality."""
    try:
        print("Testing chart generation functionality...")
        
        import matplotlib.pyplot as plt
        import plotly.graph_objects as go
        import plotly.utils
        print("✅ Successfully imported chart libraries (matplotlib, plotly)")
        
        print("Testing matplotlib chart generation...")
        fig, ax = plt.subplots(figsize=(10, 6))
        dates = [datetime.now() - timedelta(days=i) for i in range(30, 0, -1)]
        prices = [400 + i * 2 + (i % 5) for i in range(30)]
        
        ax.plot(dates, prices, marker='o', linewidth=2)
        ax.set_title('S&P 500 Mock Data - Last 30 Days')
        ax.set_xlabel('Date')
        ax.set_ylabel('Price ($)')
        ax.grid(True, alpha=0.3)
        
        plt.savefig('/tmp/test_chart.png', dpi=150, bbox_inches='tight')
        plt.close()
        print("✅ Successfully generated matplotlib chart")
        
        print("Testing plotly chart generation...")
        fig = go.Figure()
        fig.add_trace(go.Scatter(
            x=[d.strftime("%Y-%m-%d") for d in dates],
            y=prices,
            mode='lines+markers',
            name='SPY Price',
            line=dict(color='blue', width=2)
        ))
        fig.update_layout(
            title='S&P 500 Mock Data - Last 30 Days',
            xaxis_title='Date',
            yaxis_title='Price ($)',
            hovermode='x unified'
        )
        
        chart_json = plotly.utils.PlotlyJSONEncoder().encode(fig)
        chart_data = json.loads(chart_json)
        print("✅ Successfully generated plotly chart JSON")
        
        print("Testing volume chart generation...")
        volumes = [1000000 + i * 50000 + (i % 3) * 100000 for i in range(30)]
        fig_volume = go.Figure()
        fig_volume.add_trace(go.Bar(
            x=[d.strftime("%Y-%m-%d") for d in dates],
            y=volumes,
            name='SPY Volume',
            marker_color='green'
        ))
        fig_volume.update_layout(
            title='S&P 500 Volume - Last 30 Days',
            xaxis_title='Date',
            yaxis_title='Volume',
            hovermode='x unified'
        )
        
        volume_json = plotly.utils.PlotlyJSONEncoder().encode(fig_volume)
        volume_data = json.loads(volume_json)
        print("✅ Successfully generated volume chart JSON")
        
        print("Testing FastMCP chart tool...")
        from app.tools.market_tools import mcp as mcp_app
        
        tools = await mcp_app.list_tools()
        chart_tool = None
        for tool in tools:
            if hasattr(tool, 'name') and tool.name == 'generate_market_chart':
                chart_tool = tool
                break
        
        if chart_tool:
            print("✅ Found generate_market_chart tool in FastMCP")
            print(f"  - Description: {getattr(chart_tool, 'description', 'No description')}")
        else:
            print("❌ generate_market_chart tool not found in FastMCP")
            return False
        
        print("✅ Chart generation functionality test completed successfully")
        return True
        
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        print("Full traceback:")
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = asyncio.run(test_chart_generation())
    sys.exit(0 if success else 1)
