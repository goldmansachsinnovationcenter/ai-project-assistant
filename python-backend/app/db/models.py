from datetime import datetime
from decimal import Decimal
from typing import Optional
from sqlmodel import SQLModel, Field


class MarketData(SQLModel, table=True):
    __tablename__ = "market_data"
    
    id: Optional[int] = Field(default=None, primary_key=True)
    symbol: str = Field(nullable=False, index=True)
    price: Decimal = Field(nullable=False, decimal_places=2)
    volume: int = Field(nullable=False)
    market_cap: Optional[Decimal] = Field(default=None, decimal_places=2)
    pe_ratio: Optional[Decimal] = Field(default=None, decimal_places=2)
    change_percent: Optional[Decimal] = Field(default=None, decimal_places=4)
    timestamp: datetime = Field(nullable=False, index=True)
    created_at: datetime = Field(default_factory=datetime.utcnow)


class MarketNews(SQLModel, table=True):
    __tablename__ = "market_news"
    
    id: Optional[int] = Field(default=None, primary_key=True)
    title: str = Field(nullable=False)
    summary: Optional[str] = Field(default=None)
    url: str = Field(nullable=False)
    source: str = Field(nullable=False)
    sentiment: Optional[str] = Field(default=None)  # positive, negative, neutral
    relevance_score: Optional[Decimal] = Field(default=None, decimal_places=2)
    published_at: datetime = Field(nullable=False, index=True)
    created_at: datetime = Field(default_factory=datetime.utcnow)


class MarketPrediction(SQLModel, table=True):
    __tablename__ = "market_predictions"
    
    id: Optional[int] = Field(default=None, primary_key=True)
    symbol: str = Field(nullable=False, index=True)
    prediction_type: str = Field(nullable=False)  # price, trend, volatility
    predicted_value: Decimal = Field(nullable=False, decimal_places=2)
    confidence_score: Decimal = Field(nullable=False, decimal_places=2)
    time_horizon: str = Field(nullable=False)  # 1d, 1w, 1m, 3m
    model_used: str = Field(nullable=False)
    prediction_date: datetime = Field(nullable=False, index=True)
    target_date: datetime = Field(nullable=False, index=True)
    created_at: datetime = Field(default_factory=datetime.utcnow)
