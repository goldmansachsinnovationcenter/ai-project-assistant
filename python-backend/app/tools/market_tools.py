import asyncio
import json
import logging
from datetime import datetime
from typing import Dict, List, Optional
import matplotlib.pyplot as plt
import plotly.graph_objects as go
import plotly.utils
from fastmcp import FastMCP
from app.services.market_data import MarketDataService
from app.core.config import settings

logger = logging.getLogger(__name__)
mcp = FastMCP('market-data-tools')

market_service = MarketDataService(api_key=settings.ALPHA_VANTAGE_API_KEY)


@mcp.tool()
def get_sp500_data(symbol: str = "SPY") -> str:
    """Get current S&P 500 market data including price, volume, and change percentage.
    
    Args:
        symbol: Stock symbol to fetch data for (default: SPY for S&P 500)
        
    Returns:
        JSON string containing current market data
    """
    try:
        loop = asyncio.get_event_loop()
        if loop.is_running():
            import concurrent.futures
            with concurrent.futures.ThreadPoolExecutor() as executor:
                future = executor.submit(asyncio.run, market_service.fetch_sp500_data(symbol))
                data = future.result()
        else:
            data = asyncio.run(market_service.fetch_sp500_data(symbol))
        
        if data:
            formatted_data = {
                "symbol": data["symbol"],
                "current_price": f"${float(data['price']):.2f}",
                "volume": f"{data['volume']:,}",
                "change_percent": f"{float(data['change_percent']):.2f}%",
                "timestamp": data["timestamp"].strftime("%Y-%m-%d %H:%M:%S UTC"),
                "status": "success"
            }
            return json.dumps(formatted_data, indent=2)
        else:
            return json.dumps({
                "error": "Failed to fetch S&P 500 data",
                "status": "error"
            })
    except Exception as e:
        logger.error(f"Error in get_sp500_data: {e}")
        return json.dumps({
            "error": f"Error fetching market data: {str(e)}",
            "status": "error"
        })


@mcp.tool()
def get_market_trend(timeframe: str = "1week", symbol: str = "SPY") -> str:
    """Analyze market trends and provide predictions for the specified timeframe.
    
    Args:
        timeframe: Time horizon for prediction (1week, 1month, 3months)
        symbol: Stock symbol to analyze (default: SPY for S&P 500)
        
    Returns:
        JSON string containing trend analysis and predictions
    """
    try:
        loop = asyncio.get_event_loop()
        if loop.is_running():
            import concurrent.futures
            with concurrent.futures.ThreadPoolExecutor() as executor:
                future = executor.submit(asyncio.run, market_service.generate_trend_prediction(symbol))
                prediction = future.result()
        else:
            prediction = asyncio.run(market_service.generate_trend_prediction(symbol))
        
        if prediction:
            formatted_prediction = {
                "symbol": prediction["symbol"],
                "prediction_type": prediction["prediction_type"],
                "predicted_price": f"${float(prediction['predicted_value']):.2f}",
                "confidence_score": f"{float(prediction['confidence_score']) * 100:.1f}%",
                "time_horizon": prediction["time_horizon"],
                "model_used": prediction["model_used"],
                "prediction_date": prediction["prediction_date"].strftime("%Y-%m-%d %H:%M:%S UTC"),
                "target_date": prediction["target_date"].strftime("%Y-%m-%d %H:%M:%S UTC"),
                "status": "success"
            }
            return json.dumps(formatted_prediction, indent=2)
        else:
            return json.dumps({
                "error": "Failed to generate market trend prediction",
                "status": "error"
            })
    except Exception as e:
        logger.error(f"Error in get_market_trend: {e}")
        return json.dumps({
            "error": f"Error generating trend prediction: {str(e)}",
            "status": "error"
        })


@mcp.tool()
def get_market_news(limit: int = 5, topics: str = "financial_markets") -> str:
    """Fetch recent market news with sentiment analysis.
    
    Args:
        limit: Number of news articles to return (default: 5)
        topics: News topics to filter by (default: financial_markets)
        
    Returns:
        JSON string containing recent market news with sentiment
    """
    try:
        loop = asyncio.get_event_loop()
        if loop.is_running():
            import concurrent.futures
            with concurrent.futures.ThreadPoolExecutor() as executor:
                future = executor.submit(asyncio.run, market_service.fetch_market_news(topics))
                news_items = future.result()
        else:
            news_items = asyncio.run(market_service.fetch_market_news(topics))
        
        if news_items:
            formatted_news = []
            for item in news_items[:limit]:
                formatted_item = {
                    "title": item["title"],
                    "summary": item["summary"][:200] + "..." if len(item["summary"]) > 200 else item["summary"],
                    "source": item["source"],
                    "sentiment": item["sentiment"],
                    "relevance_score": f"{float(item['relevance_score']):.2f}",
                    "published_at": item["published_at"].strftime("%Y-%m-%d %H:%M:%S UTC"),
                    "url": item["url"]
                }
                formatted_news.append(formatted_item)
            
            return json.dumps({
                "news_count": len(formatted_news),
                "news_items": formatted_news,
                "status": "success"
            }, indent=2)
        else:
            return json.dumps({
                "error": "Failed to fetch market news",
                "status": "error"
            })
    except Exception as e:
        logger.error(f"Error in get_market_news: {e}")
        return json.dumps({
            "error": f"Error fetching market news: {str(e)}",
            "status": "error"
        })


@mcp.tool()
def generate_market_chart(days: int = 30, chart_type: str = "line", symbol: str = "SPY") -> str:
    """Generate market chart data for visualization.
    
    Args:
        days: Number of days of historical data to include (default: 30)
        chart_type: Type of chart to generate (line, candlestick, volume)
        symbol: Stock symbol to chart (default: SPY for S&P 500)
        
    Returns:
        JSON string containing chart data and configuration
    """
    try:
        loop = asyncio.get_event_loop()
        if loop.is_running():
            import concurrent.futures
            with concurrent.futures.ThreadPoolExecutor() as executor:
                future = executor.submit(asyncio.run, market_service.fetch_historical_data(symbol))
                historical_data = future.result()
        else:
            historical_data = asyncio.run(market_service.fetch_historical_data(symbol))
        
        if historical_data:
            chart_data = historical_data[:days]
            
            dates = [item["timestamp"].strftime("%Y-%m-%d") for item in chart_data]
            prices = [float(item["price"]) for item in chart_data]
            volumes = [item["volume"] for item in chart_data]
            
            if chart_type == "line":
                fig = go.Figure()
                fig.add_trace(go.Scatter(
                    x=dates,
                    y=prices,
                    mode='lines+markers',
                    name=f'{symbol} Price',
                    line=dict(color='blue', width=2)
                ))
                fig.update_layout(
                    title=f'{symbol} Price Chart - Last {days} Days',
                    xaxis_title='Date',
                    yaxis_title='Price ($)',
                    hovermode='x unified'
                )
                
            elif chart_type == "volume":
                fig = go.Figure()
                fig.add_trace(go.Bar(
                    x=dates,
                    y=volumes,
                    name=f'{symbol} Volume',
                    marker_color='green'
                ))
                fig.update_layout(
                    title=f'{symbol} Volume Chart - Last {days} Days',
                    xaxis_title='Date',
                    yaxis_title='Volume',
                    hovermode='x unified'
                )
                
            else:  # Default to line chart
                fig = go.Figure()
                fig.add_trace(go.Scatter(
                    x=dates,
                    y=prices,
                    mode='lines',
                    name=f'{symbol} Price'
                ))
                fig.update_layout(
                    title=f'{symbol} Chart - Last {days} Days',
                    xaxis_title='Date',
                    yaxis_title='Price ($)'
                )
            
            chart_json = plotly.utils.PlotlyJSONEncoder().encode(fig)
            
            return json.dumps({
                "chart_data": json.loads(chart_json),
                "chart_type": chart_type,
                "symbol": symbol,
                "days": len(chart_data),
                "date_range": {
                    "start": dates[-1] if dates else None,
                    "end": dates[0] if dates else None
                },
                "status": "success"
            }, indent=2)
        else:
            return json.dumps({
                "error": "Failed to fetch historical data for chart generation",
                "status": "error"
            })
    except Exception as e:
        logger.error(f"Error in generate_market_chart: {e}")
        return json.dumps({
            "error": f"Error generating market chart: {str(e)}",
            "status": "error"
        })


__all__ = ['mcp']
