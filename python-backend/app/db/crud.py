"""CRUD operations for market data models."""
from datetime import datetime
from typing import List, Optional
from sqlmodel import Session, select
from sqlalchemy.exc import SQLAlchemyError

from app.db.models import MarketData, MarketNews, MarketPrediction


class MarketDataCRUD:
    """CRUD operations for MarketData model."""
    
    @staticmethod
    def create(session: Session, market_data: MarketData) -> MarketData:
        """Create a new market data record."""
        try:
            session.add(market_data)
            session.commit()
            session.refresh(market_data)
            return market_data
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_by_id(session: Session, data_id: int) -> Optional[MarketData]:
        """Get market data by ID."""
        return session.get(MarketData, data_id)
    
    @staticmethod
    def get_by_symbol(session: Session, symbol: str, limit: int = 100) -> List[MarketData]:
        """Get market data by symbol."""
        statement = select(MarketData).where(MarketData.symbol == symbol).order_by(MarketData.timestamp.desc()).limit(limit)
        return session.exec(statement).all()
    
    @staticmethod
    def get_latest_by_symbol(session: Session, symbol: str) -> Optional[MarketData]:
        """Get the latest market data for a symbol."""
        statement = select(MarketData).where(MarketData.symbol == symbol).order_by(MarketData.timestamp.desc()).limit(1)
        result = session.exec(statement).first()
        return result
    
    @staticmethod
    def get_historical_data(session: Session, symbol: str, days: int = 30) -> List[MarketData]:
        """Get historical market data for the specified number of days."""
        from datetime import timedelta
        cutoff_date = datetime.utcnow() - timedelta(days=days)
        statement = (
            select(MarketData)
            .where(MarketData.symbol == symbol)
            .where(MarketData.timestamp >= cutoff_date)
            .order_by(MarketData.timestamp.desc())
        )
        return session.exec(statement).all()
    
    @staticmethod
    def update(session: Session, data_id: int, **kwargs) -> Optional[MarketData]:
        """Update market data record."""
        try:
            market_data = session.get(MarketData, data_id)
            if market_data:
                for key, value in kwargs.items():
                    if hasattr(market_data, key):
                        setattr(market_data, key, value)
                session.commit()
                session.refresh(market_data)
                return market_data
            return None
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def delete(session: Session, data_id: int) -> bool:
        """Delete market data record."""
        try:
            market_data = session.get(MarketData, data_id)
            if market_data:
                session.delete(market_data)
                session.commit()
                return True
            return False
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_all(session: Session, skip: int = 0, limit: int = 100) -> List[MarketData]:
        """Get all market data with pagination."""
        statement = select(MarketData).order_by(MarketData.timestamp.desc()).offset(skip).limit(limit)
        return session.exec(statement).all()


class MarketNewsCRUD:
    """CRUD operations for MarketNews model."""
    
    @staticmethod
    def create(session: Session, news: MarketNews) -> MarketNews:
        """Create a new market news record."""
        try:
            session.add(news)
            session.commit()
            session.refresh(news)
            return news
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def create_multiple(session: Session, news_items: List[MarketNews]) -> List[MarketNews]:
        """Create multiple market news records."""
        try:
            for news in news_items:
                session.add(news)
            session.commit()
            for news in news_items:
                session.refresh(news)
            return news_items
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_by_id(session: Session, news_id: int) -> Optional[MarketNews]:
        """Get market news by ID."""
        return session.get(MarketNews, news_id)
    
    @staticmethod
    def get_recent_news(session: Session, limit: int = 10) -> List[MarketNews]:
        """Get recent market news."""
        statement = select(MarketNews).order_by(MarketNews.published_at.desc()).limit(limit)
        return session.exec(statement).all()
    
    @staticmethod
    def get_by_sentiment(session: Session, sentiment: str, limit: int = 10) -> List[MarketNews]:
        """Get market news by sentiment."""
        statement = (
            select(MarketNews)
            .where(MarketNews.sentiment == sentiment)
            .order_by(MarketNews.published_at.desc())
            .limit(limit)
        )
        return session.exec(statement).all()
    
    @staticmethod
    def get_by_source(session: Session, source: str, limit: int = 10) -> List[MarketNews]:
        """Get market news by source."""
        statement = (
            select(MarketNews)
            .where(MarketNews.source == source)
            .order_by(MarketNews.published_at.desc())
            .limit(limit)
        )
        return session.exec(statement).all()
    
    @staticmethod
    def get_by_date_range(session: Session, start_date: datetime, end_date: datetime) -> List[MarketNews]:
        """Get market news within date range."""
        statement = (
            select(MarketNews)
            .where(MarketNews.published_at >= start_date)
            .where(MarketNews.published_at <= end_date)
            .order_by(MarketNews.published_at.desc())
        )
        return session.exec(statement).all()
    
    @staticmethod
    def update(session: Session, news_id: int, **kwargs) -> Optional[MarketNews]:
        """Update market news record."""
        try:
            news = session.get(MarketNews, news_id)
            if news:
                for key, value in kwargs.items():
                    if hasattr(news, key):
                        setattr(news, key, value)
                session.commit()
                session.refresh(news)
                return news
            return None
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def delete(session: Session, news_id: int) -> bool:
        """Delete market news record."""
        try:
            news = session.get(MarketNews, news_id)
            if news:
                session.delete(news)
                session.commit()
                return True
            return False
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_all(session: Session, skip: int = 0, limit: int = 100) -> List[MarketNews]:
        """Get all market news with pagination."""
        statement = select(MarketNews).order_by(MarketNews.published_at.desc()).offset(skip).limit(limit)
        return session.exec(statement).all()


class MarketPredictionCRUD:
    """CRUD operations for MarketPrediction model."""
    
    @staticmethod
    def create(session: Session, prediction: MarketPrediction) -> MarketPrediction:
        """Create a new market prediction record."""
        try:
            session.add(prediction)
            session.commit()
            session.refresh(prediction)
            return prediction
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_by_id(session: Session, prediction_id: int) -> Optional[MarketPrediction]:
        """Get market prediction by ID."""
        return session.get(MarketPrediction, prediction_id)
    
    @staticmethod
    def get_by_symbol(session: Session, symbol: str, limit: int = 10) -> List[MarketPrediction]:
        """Get market predictions by symbol."""
        statement = (
            select(MarketPrediction)
            .where(MarketPrediction.symbol == symbol)
            .order_by(MarketPrediction.prediction_date.desc())
            .limit(limit)
        )
        return session.exec(statement).all()
    
    @staticmethod
    def get_latest_prediction(session: Session, symbol: str, prediction_type: str) -> Optional[MarketPrediction]:
        """Get the latest prediction for a symbol and type."""
        statement = (
            select(MarketPrediction)
            .where(MarketPrediction.symbol == symbol)
            .where(MarketPrediction.prediction_type == prediction_type)
            .order_by(MarketPrediction.prediction_date.desc())
            .limit(1)
        )
        result = session.exec(statement).first()
        return result
    
    @staticmethod
    def get_by_time_horizon(session: Session, time_horizon: str, limit: int = 10) -> List[MarketPrediction]:
        """Get predictions by time horizon."""
        statement = (
            select(MarketPrediction)
            .where(MarketPrediction.time_horizon == time_horizon)
            .order_by(MarketPrediction.prediction_date.desc())
            .limit(limit)
        )
        return session.exec(statement).all()
    
    @staticmethod
    def get_by_confidence_range(session: Session, min_confidence: float, max_confidence: float = 1.0) -> List[MarketPrediction]:
        """Get predictions within confidence score range."""
        statement = (
            select(MarketPrediction)
            .where(MarketPrediction.confidence_score >= min_confidence)
            .where(MarketPrediction.confidence_score <= max_confidence)
            .order_by(MarketPrediction.confidence_score.desc())
        )
        return session.exec(statement).all()
    
    @staticmethod
    def get_active_predictions(session: Session, symbol: str) -> List[MarketPrediction]:
        """Get active predictions (target date in the future)."""
        current_time = datetime.utcnow()
        statement = (
            select(MarketPrediction)
            .where(MarketPrediction.symbol == symbol)
            .where(MarketPrediction.target_date > current_time)
            .order_by(MarketPrediction.target_date.asc())
        )
        return session.exec(statement).all()
    
    @staticmethod
    def update(session: Session, prediction_id: int, **kwargs) -> Optional[MarketPrediction]:
        """Update market prediction record."""
        try:
            prediction = session.get(MarketPrediction, prediction_id)
            if prediction:
                for key, value in kwargs.items():
                    if hasattr(prediction, key):
                        setattr(prediction, key, value)
                session.commit()
                session.refresh(prediction)
                return prediction
            return None
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def delete(session: Session, prediction_id: int) -> bool:
        """Delete market prediction record."""
        try:
            prediction = session.get(MarketPrediction, prediction_id)
            if prediction:
                session.delete(prediction)
                session.commit()
                return True
            return False
        except SQLAlchemyError as e:
            session.rollback()
            raise e
    
    @staticmethod
    def get_all(session: Session, skip: int = 0, limit: int = 100) -> List[MarketPrediction]:
        """Get all market predictions with pagination."""
        statement = select(MarketPrediction).order_by(MarketPrediction.prediction_date.desc()).offset(skip).limit(limit)
        return session.exec(statement).all()
