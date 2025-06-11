from fastapi import APIRouter, Depends, BackgroundTasks, HTTPException, Query
from sqlmodel import Session, select
from typing import List, Optional
import asyncio
from datetime import datetime

from app.core.database import get_session
from app.services.market_data import MarketDataService
from app.db.models import MarketData, MarketNews, MarketPrediction
from app.core.config import settings

router = APIRouter()

market_service = MarketDataService(api_key=settings.ALPHA_VANTAGE_API_KEY)


@router.get("/status")
async def get_market_status():
    """Get market data system status."""
    return {
        "status": "ok",
        "service": "S&P 500 Market Data API",
        "timestamp": datetime.utcnow().isoformat()
    }


@router.get("/data/current")
async def get_current_market_data(
    symbol: str = Query(default="SPY", description="Stock symbol to fetch data for")
):
    """Get current S&P 500 market data."""
    try:
        data = await market_service.fetch_sp500_data(symbol)
        if data:
            return {
                "success": True,
                "data": {
                    "symbol": data["symbol"],
                    "price": float(data["price"]),
                    "volume": data["volume"],
                    "change_percent": float(data["change_percent"]),
                    "timestamp": data["timestamp"].isoformat()
                }
            }
        else:
            raise HTTPException(status_code=404, detail="Market data not found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching market data: {str(e)}")


@router.get("/data/historical")
async def get_historical_data(
    symbol: str = Query(default="SPY", description="Stock symbol to fetch data for"),
    days: int = Query(default=30, description="Number of days of historical data")
):
    """Get historical market data."""
    try:
        data = await market_service.fetch_historical_data(symbol)
        if data:
            limited_data = data[:days]
            formatted_data = [
                {
                    "symbol": item["symbol"],
                    "price": float(item["price"]),
                    "volume": item["volume"],
                    "timestamp": item["timestamp"].isoformat()
                }
                for item in limited_data
            ]
            return {
                "success": True,
                "data": formatted_data,
                "count": len(formatted_data)
            }
        else:
            raise HTTPException(status_code=404, detail="Historical data not found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching historical data: {str(e)}")


@router.get("/news")
async def get_market_news(
    limit: int = Query(default=10, description="Number of news articles to return"),
    topics: str = Query(default="financial_markets", description="News topics to filter by")
):
    """Get recent market news with sentiment analysis."""
    try:
        news_items = await market_service.fetch_market_news(topics)
        if news_items:
            formatted_news = [
                {
                    "title": item["title"],
                    "summary": item["summary"],
                    "source": item["source"],
                    "sentiment": item["sentiment"],
                    "relevance_score": float(item["relevance_score"]),
                    "published_at": item["published_at"].isoformat(),
                    "url": item["url"]
                }
                for item in news_items[:limit]
            ]
            return {
                "success": True,
                "data": formatted_news,
                "count": len(formatted_news)
            }
        else:
            return {
                "success": True,
                "data": [],
                "count": 0,
                "message": "No news items found"
            }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching market news: {str(e)}")


@router.get("/trends/prediction")
async def get_trend_prediction(
    symbol: str = Query(default="SPY", description="Stock symbol to analyze"),
    timeframe: str = Query(default="1week", description="Time horizon for prediction")
):
    """Get market trend prediction."""
    try:
        prediction = await market_service.generate_trend_prediction(symbol)
        if prediction:
            return {
                "success": True,
                "data": {
                    "symbol": prediction["symbol"],
                    "prediction_type": prediction["prediction_type"],
                    "predicted_value": float(prediction["predicted_value"]),
                    "confidence_score": float(prediction["confidence_score"]),
                    "time_horizon": prediction["time_horizon"],
                    "model_used": prediction["model_used"],
                    "prediction_date": prediction["prediction_date"].isoformat(),
                    "target_date": prediction["target_date"].isoformat()
                }
            }
        else:
            raise HTTPException(status_code=404, detail="Unable to generate trend prediction")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating trend prediction: {str(e)}")


@router.get("/chart/data")
async def get_chart_data(
    symbol: str = Query(default="SPY", description="Stock symbol to chart"),
    days: int = Query(default=30, description="Number of days of data to include"),
    chart_type: str = Query(default="line", description="Type of chart (line, volume)")
):
    """Get chart data for market visualization."""
    try:
        historical_data = await market_service.fetch_historical_data(symbol)
        if historical_data:
            chart_data = historical_data[:days]
            
            formatted_data = {
                "symbol": symbol,
                "chart_type": chart_type,
                "data_points": [
                    {
                        "date": item["timestamp"].strftime("%Y-%m-%d"),
                        "price": float(item["price"]),
                        "volume": item["volume"]
                    }
                    for item in chart_data
                ],
                "date_range": {
                    "start": chart_data[-1]["timestamp"].strftime("%Y-%m-%d") if chart_data else None,
                    "end": chart_data[0]["timestamp"].strftime("%Y-%m-%d") if chart_data else None
                }
            }
            
            return {
                "success": True,
                "data": formatted_data,
                "count": len(chart_data)
            }
        else:
            raise HTTPException(status_code=404, detail="Chart data not found")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error generating chart data: {str(e)}")


@router.post("/refresh")
async def refresh_market_data(
    background_tasks: BackgroundTasks,
    symbol: str = Query(default="SPY", description="Stock symbol to refresh data for")
):
    """Refresh market data in background."""
    try:
        async def refresh_data_task():
            """Background task to refresh market data."""
            try:
                current_data = await market_service.fetch_sp500_data(symbol)
                if current_data:
                    await market_service.save_market_data(current_data)
                
                news_items = await market_service.fetch_market_news()
                if news_items:
                    await market_service.save_market_news(news_items)
                
                prediction = await market_service.generate_trend_prediction(symbol)
                if prediction:
                    await market_service.save_prediction(prediction)
                    
            except Exception as e:
                import logging
                logger = logging.getLogger(__name__)
                logger.error(f"Error in background refresh task: {e}")
        
        background_tasks.add_task(refresh_data_task)
        
        return {
            "success": True,
            "message": f"Market data refresh started for {symbol}",
            "timestamp": datetime.utcnow().isoformat()
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error starting data refresh: {str(e)}")


@router.get("/database/stats")
async def get_database_stats(session: Session = Depends(get_session)):
    """Get database statistics."""
    try:
        market_data_count = len(session.exec(select(MarketData)).all())
        market_news_count = len(session.exec(select(MarketNews)).all())
        predictions_count = len(session.exec(select(MarketPrediction)).all())
        
        return {
            "success": True,
            "data": {
                "market_data_records": market_data_count,
                "market_news_records": market_news_count,
                "prediction_records": predictions_count,
                "total_records": market_data_count + market_news_count + predictions_count
            },
            "timestamp": datetime.utcnow().isoformat()
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching database stats: {str(e)}")
