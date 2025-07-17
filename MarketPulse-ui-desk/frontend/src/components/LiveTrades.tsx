import React, { useEffect, useState } from 'react';

interface Trade {
  ticker: string;
  price: number;
  timestamp: string;
}

const LiveTrades: React.FC = () => {
  const [trades, setTrades] = useState<Trade[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [socket, setSocket] = useState<WebSocket | null>(null);

  useEffect(() => {
    connectWebSocket();
    return () => {
      if (socket) {
        socket.close();
      }
    };
  }, []);

  const connectWebSocket = () => {
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
      const wsUrl = `${protocol}//${window.location.host}/ws/trades`;
      
      const newSocket = new WebSocket(wsUrl);
      
      newSocket.onopen = () => {
        setIsConnected(true);
      };
      
      newSocket.onmessage = (event) => {
        try {
          const trade = JSON.parse(event.data);
          addTrade(trade);
        } catch (error) {
          console.error('Error parsing trade data:', error);
        }
      };
      
      newSocket.onclose = () => {
        setIsConnected(false);
      };
      
      newSocket.onerror = (error) => {
        console.error('WebSocket error:', error);
        setIsConnected(false);
      };
      
      setSocket(newSocket);
    } catch (error) {
      console.error('WebSocket connection error:', error);
      setIsConnected(false);
    }
  };

  const addTrade = (trade: Trade) => {
    if (!trade || !trade.price || !trade.timestamp) {
      return;
    }
    
    setTrades(prevTrades => {
      const newTrades = [trade, ...prevTrades.slice(0, 49)]; // Keep only last 50 trades
      return newTrades;
    });
  };

  const formatPrice = (price: number): string => {
    return parseFloat(price.toString()).toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  };

  return (
    <>
      <div className="header">
        <h1>📈 MarketPulse - Live Trades</h1>
        <div className="status">
          <div className={`status-indicator ${isConnected ? 'connected' : ''}`}></div>
          <span>{isConnected ? 'Connected' : 'Connecting...'}</span>
        </div>
      </div>
      
      <div className="trades-container">
        <div className="trades-header">
          <h2>Live Trades</h2>
          <span className="trade-count">{trades.length} trade{trades.length !== 1 ? 's' : ''}</span>
        </div>
        
        <div className="trades-list">
          {trades.length === 0 ? (
            <div className="empty-state">
              <h3>Waiting for trade data...</h3>
              <p>The system will display real-time Bitcoin trades as they come in.</p>
            </div>
          ) : (
            trades.map((trade, index) => (
              <div key={`${trade.timestamp}-${index}`} className={`trade-item ${index === 0 ? 'new' : ''}`}>
                <div className="trade-header">
                  <span className="ticker">{trade.ticker}</span>
                  <span className="price">${formatPrice(trade.price)}</span>
                </div>
                <div className="timestamp">📅 {new Date(trade.timestamp).toLocaleString()}</div>
              </div>
            ))
          )}
        </div>
      </div>
    </>
  );
};

export default LiveTrades; 