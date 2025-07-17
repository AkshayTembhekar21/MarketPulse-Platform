import React, { useState, useEffect } from 'react';

interface P2PTrade {
  id: number;
  senderId: number;
  receiverId: number;
  instrument: string;
  quantity: number;
  price: number;
  tradeType: string;
  status: string;
  description?: string;
  createdAt: string;
  negotiationHistory?: string[];
}

interface TradeModalProps {
  trade: P2PTrade;
  currentUserId: number;
  onClose: () => void;
}

const TradeModal: React.FC<TradeModalProps> = ({ trade, currentUserId, onClose }) => {
  const [counterPrice, setCounterPrice] = useState(trade.price.toString());
  const [counterMessage, setCounterMessage] = useState('');
  const [timeLeft, setTimeLeft] = useState(60); // 1 minute timer

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          onClose();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [onClose]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleAcceptTrade = async () => {
    try {
      const response = await fetch(`http://localhost:8083/trades/${trade.id}/accept?userId=${currentUserId}`, {
        method: 'POST'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      onClose();
    } catch (error) {
      console.error('Error accepting trade:', error);
      alert('Error accepting trade: ' + (error as Error).message);
    }
  };

  const handleRejectTrade = async () => {
    try {
      const response = await fetch(`http://localhost:8083/trades/${trade.id}/reject?userId=${currentUserId}`, {
        method: 'POST'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      onClose();
    } catch (error) {
      console.error('Error rejecting trade:', error);
      alert('Error rejecting trade: ' + (error as Error).message);
    }
  };

  const handleSendCounterOffer = async () => {
    if (!counterPrice) {
      alert('Please enter a counter price');
      return;
    }

    try {
      const message = `Counter offer: $${counterPrice}${counterMessage ? ' - ' + counterMessage : ''}`;
      const response = await fetch(`http://localhost:8083/trades/${trade.id}/negotiate?userId=${currentUserId}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: message
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      onClose();
    } catch (error) {
      console.error('Error sending counter offer:', error);
      alert('Error sending counter offer: ' + (error as Error).message);
    }
  };

  const isReceiver = trade.receiverId === currentUserId;

  return (
    <div 
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        background: 'rgba(0, 0, 0, 0.7)',
        zIndex: 1000,
        backdropFilter: 'blur(5px)',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center'
      }}
      onClick={(e) => {
        if (e.target === e.currentTarget) {
          onClose();
        }
      }}
    >
      <div style={{
        background: 'white',
        borderRadius: '15px',
        padding: '30px',
        minWidth: '500px',
        maxWidth: '600px',
        boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)'
      }}>
        {/* Modal Header */}
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '20px',
          paddingBottom: '15px',
          borderBottom: '2px solid #e9ecef'
        }}>
          <h2 style={{ color: '#2c3e50', margin: 0 }}>Trade Details</h2>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{
              background: '#e74c3c',
              color: 'white',
              padding: '5px 10px',
              borderRadius: '15px',
              fontSize: '12px',
              fontWeight: 'bold'
            }}>
              {formatTime(timeLeft)}
            </span>
            <button 
              onClick={onClose}
              style={{
                background: 'none',
                border: 'none',
                fontSize: '24px',
                cursor: 'pointer',
                color: '#7f8c8d'
              }}
            >
              ×
            </button>
          </div>
        </div>

        {/* Trade Information */}
        <div style={{
          background: '#f8f9fa',
          padding: '20px',
          borderRadius: '8px',
          marginBottom: '15px'
        }}>
          <h3 style={{ color: '#2c3e50', marginBottom: '15px' }}>Trade #{trade.id}</h3>
          <div style={{
            display: 'grid',
            gridTemplateColumns: '1fr 1fr',
            gap: '15px',
            fontSize: '14px'
          }}>
            <div><strong>Type:</strong> {trade.tradeType}</div>
            <div><strong>Instrument:</strong> {trade.instrument}</div>
            <div><strong>Quantity:</strong> {trade.quantity}</div>
            <div><strong>Price:</strong> ${trade.price.toLocaleString()}</div>
            <div><strong>Status:</strong> <span style={{ color: '#ffc107', fontWeight: 'bold' }}>{trade.status}</span></div>
            <div><strong>From:</strong> User {trade.senderId}</div>
            <div><strong>Created:</strong> {new Date(trade.createdAt).toLocaleString()}</div>
            <div><strong>Description:</strong> {trade.description || 'No description'}</div>
          </div>
        </div>
        
        <div style={{
          background: '#e9ecef',
          padding: '15px',
          borderRadius: '8px',
          marginBottom: '20px'
        }}>
          <h4 style={{ color: '#495057', marginBottom: '10px' }}>Negotiation History</h4>
          <div style={{ maxHeight: '150px', overflowY: 'auto', fontSize: '13px' }}>
            {trade.negotiationHistory && trade.negotiationHistory.length > 0 
              ? trade.negotiationHistory.map((msg, index) => (
                  <div key={index} style={{ padding: '5px 0', borderBottom: '1px solid #dee2e6' }}>
                    {msg}
                  </div>
                ))
              : <div style={{ color: '#6c757d' }}>No negotiation history yet.</div>
            }
          </div>
        </div>

        {/* Counter Offer Section */}
        <div style={{
          marginBottom: '20px',
          padding: '15px',
          background: '#f8f9fa',
          borderRadius: '8px'
        }}>
          <h3 style={{ color: '#34495e', marginBottom: '10px' }}>Make Counter Offer</h3>
          
          {/* Price Counter Offer */}
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', color: '#495057', fontWeight: 500 }}>
              New Price:
            </label>
            <input 
              type="number" 
              value={counterPrice}
              onChange={(e) => setCounterPrice(e.target.value)}
              placeholder="Enter new price" 
              style={{
                width: '100%',
                padding: '10px',
                border: '2px solid #e9ecef',
                borderRadius: '6px',
                fontSize: '14px'
              }}
            />
          </div>

          {/* Message */}
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', color: '#495057', fontWeight: 500 }}>
              Message:
            </label>
            <textarea 
              value={counterMessage}
              onChange={(e) => setCounterMessage(e.target.value)}
              placeholder="Enter your message" 
              rows={3}
              style={{
                width: '100%',
                padding: '10px',
                border: '2px solid #e9ecef',
                borderRadius: '6px',
                fontSize: '14px',
                resize: 'vertical'
              }}
            />
          </div>

          <button 
            onClick={handleSendCounterOffer}
            style={{
              padding: '10px 20px',
              background: '#f39c12',
              color: 'white',
              border: 'none',
              borderRadius: '6px',
              fontWeight: 'bold',
              cursor: 'pointer',
              transition: 'background 0.2s'
            }}
          >
            Send Counter Offer
          </button>
        </div>

        {/* Action Buttons */}
        {isReceiver && trade.status === 'pending' && (
          <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
            <button 
              onClick={handleAcceptTrade}
              style={{
                padding: '12px 30px',
                background: '#27ae60',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontWeight: 'bold',
                cursor: 'pointer',
                transition: 'background 0.2s',
                flex: 1
              }}
            >
              Accept Trade
            </button>
            <button 
              onClick={handleRejectTrade}
              style={{
                padding: '12px 30px',
                background: '#e74c3c',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontWeight: 'bold',
                cursor: 'pointer',
                transition: 'background 0.2s',
                flex: 1
              }}
            >
              Reject Trade
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default TradeModal; 