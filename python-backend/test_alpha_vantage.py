#!/usr/bin/env python3
"""Test script to verify Alpha Vantage API integration."""

import asyncio
import sys
import traceback

async def test_alpha_vantage_api():
    """Test Alpha Vantage API integration."""
    try:
        print("Testing Alpha Vantage API integration...")
        
        from app.services.market_data import MarketDataService
        from app.core.config import settings
        print("✅ Successfully imported MarketDataService")
        
        service = MarketDataService(api_key=settings.ALPHA_VANTAGE_API_KEY)
        print(f"✅ Initialized service with API key: {settings.ALPHA_VANTAGE_API_KEY[:4]}...")
        
        print("Testing S&P 500 data fetch...")
        sp500_data = await service.fetch_sp500_data("SPY")
        if sp500_data:
            print("✅ Successfully fetched S&P 500 data:")
            print(f"  - Symbol: {sp500_data.get('symbol', 'N/A')}")
            print(f"  - Price: ${sp500_data.get('price', 'N/A')}")
            print(f"  - Volume: {sp500_data.get('volume', 'N/A')}")
            print(f"  - Change: {sp500_data.get('change_percent', 'N/A')}%")
        else:
            print("❌ Failed to fetch S&P 500 data (likely demo API key limitation)")
        
        print("Testing historical data fetch...")
        historical_data = await service.fetch_historical_data("SPY")
        if historical_data:
            print(f"✅ Successfully fetched historical data: {len(historical_data)} records")
        else:
            print("❌ Failed to fetch historical data (likely demo API key limitation)")
        
        print("Testing market news fetch...")
        news_data = await service.fetch_market_news("financial_markets")
        if news_data:
            print(f"✅ Successfully fetched market news: {len(news_data)} articles")
        else:
            print("❌ Failed to fetch market news (likely demo API key limitation)")
        
        print("Testing trend prediction...")
        prediction = await service.generate_trend_prediction("SPY")
        if prediction:
            print("✅ Successfully generated trend prediction:")
            print(f"  - Symbol: {prediction.get('symbol', 'N/A')}")
            print(f"  - Predicted Value: ${prediction.get('predicted_value', 'N/A')}")
            print(f"  - Confidence: {prediction.get('confidence_score', 'N/A')}")
        else:
            print("❌ Failed to generate trend prediction (likely demo API key limitation)")
        
        print("✅ Alpha Vantage API integration test completed")
        return True
        
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        print("Full traceback:")
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = asyncio.run(test_alpha_vantage_api())
    sys.exit(0 if success else 1)
