from pydantic_settings import BaseSettings
from typing import Optional


class Settings(BaseSettings):
    DATABASE_URL: str = "sqlite+aiosqlite:///./market_data.db"
    
    ALPHA_VANTAGE_API_KEY: str = "demo"  # Default demo key, should be overridden
    
    APP_NAME: str = "S&P 500 Market Data Chatbot"
    DEBUG: bool = False
    
    ALLOWED_ORIGINS: list[str] = ["http://localhost:3000", "http://127.0.0.1:3000"]
    
    class Config:
        env_file = ".env"
        case_sensitive = True


settings = Settings()
