import React, { useState, useEffect } from 'react';
import TradeModal from './TradeModal';

interface Partner {
  id: number;
  partnerId: number;
  status: string;
}

interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  status: string;
}

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

const P2PTrading: React.FC = () => {
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [currentUserId, setCurrentUserId] = useState<number>(1);
  const [partners, setPartners] = useState<Partner[]>([]);
  const [existingUsers, setExistingUsers] = useState<User[]>([]);
  const [p2pTrades, setP2PTrades] = useState<P2PTrade[]>([]);
  const [selectedTrade, setSelectedTrade] = useState<P2PTrade | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [partnerInput, setPartnerInput] = useState('');

  // Fetch all users for the dropdown
  useEffect(() => {
    fetch('http://localhost:8083/users')
      .then(res => res.json())
      .then(setAllUsers);
  }, []);

  // Reload data when currentUserId changes
  useEffect(() => {
    loadExistingUsers();
    loadPartners();
    loadP2PTrades();
  }, [currentUserId]);

  // Poll for new trades every 5 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      loadP2PTrades();
    }, 5000);
    return () => clearInterval(interval);
  }, [currentUserId]);

  // Accept trade handler
  const acceptTrade = async (tradeId: number) => {
    try {
      await fetch(`http://localhost:8083/trades/${tradeId}/accept?userId=${currentUserId}`, {
        method: 'POST'
      });
      loadP2PTrades();
    } catch (error) {
      alert('Error accepting trade');
    }
  };

  // Reject trade handler
  const rejectTrade = async (tradeId: number) => {
    try {
      await fetch(`http://localhost:8083/trades/${tradeId}/reject?userId=${currentUserId}`, {
        method: 'POST'
      });
      loadP2PTrades();
    } catch (error) {
      alert('Error rejecting trade');
    }
  };

  const loadExistingUsers = async () => {
    try {
      const response = await fetch('http://localhost:8083/users');
      const users = await response.json();
      setExistingUsers(users);
    } catch (error) {
      console.error('Error loading existing users:', error);
    }
  };

  const loadPartners = async () => {
    try {
      const response = await fetch(`http://localhost:8083/partners/${currentUserId}`);
      const partnersData = await response.json();
      setPartners(partnersData);
    } catch (error) {
      console.error('Error loading partners:', error);
    }
  };

  const loadP2PTrades = async () => {
    try {
      const response = await fetch(`http://localhost:8083/trades/${currentUserId}`);
      const trades = await response.json();
      setP2PTrades(trades);
    } catch (error) {
      console.error('Error loading P2P trades:', error);
    }
  };

  const addPartner = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!partnerInput.trim()) return;

    try {
      // First create the partner user
      const userResponse = await fetch('http://localhost:8083/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: partnerInput,
          email: partnerInput + '@example.com',
          fullName: 'Partner ' + partnerInput,
          status: 'ACTIVE'
        })
      });
      
      const newUser = await userResponse.json();
      
      // Then add as partner
      const partnerResponse = await fetch('http://localhost:8083/partners', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUserId,
          partnerId: newUser.id,
          status: 'active'
        })
      });
      
      const partner = await partnerResponse.json();
      setPartners(prev => [...prev, partner]);
      setPartnerInput('');
      
    } catch (error) {
      console.error('Error adding partner:', error);
      alert('Error adding partner: ' + (error as Error).message);
    }
  };

  const addExistingUserAsPartner = async (user: User) => {
    try {
      const partnerResponse = await fetch('http://localhost:8083/partners', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          userId: currentUserId,
          partnerId: user.id,
          status: 'active'
        })
      });
      
      const partner = await partnerResponse.json();
      setPartners(prev => [...prev, partner]);
      alert(`Added ${user.username} as partner!`);
      
    } catch (error) {
      console.error('Error adding existing user as partner:', error);
      alert('Error adding partner: ' + (error as Error).message);
    }
  };

  const initiateTradeWithPartner = async (partnerId: number) => {
    const tradeData = {
      senderId: currentUserId,
      receiverId: partnerId,
      instrument: 'BTC',
      quantity: 1.0,
      price: 50000.0,
      tradeType: 'BUY',
      description: 'P2P trade initiated from UI'
    };

    try {
      const response = await fetch('http://localhost:8083/trades/initiate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(tradeData)
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`HTTP ${response.status}: ${errorText}`);
      }
      
      const trade = await response.json();
      loadP2PTrades();
      
    } catch (error) {
      console.error('Error initiating trade:', error);
      alert('Error initiating trade: ' + (error as Error).message);
    }
  };

  const showTradeDetails = (tradeId: number) => {
    const trade = p2pTrades.find(t => t.id === tradeId);
    if (trade) {
      setSelectedTrade(trade);
      setShowModal(true);
    }
  };

  const isNewTrade = (trade: P2PTrade) => {
    const tradeCreated = new Date(trade.createdAt);
    const now = new Date();
    return (now.getTime() - tradeCreated.getTime()) < 30000; // 30 seconds
  };

  const isExpired = (trade: P2PTrade) => {
    const tradeCreated = new Date(trade.createdAt);
    const now = new Date();
    return (now.getTime() - tradeCreated.getTime()) > 60000 && trade.status === 'pending';
  };

  const getStatusColor = (trade: P2PTrade) => {
    if (isExpired(trade)) return '#888';
    if (isNewTrade(trade)) return '#28a745';
    if (trade.status === 'pending') return '#ffc107';
    if (trade.status === 'rejected') return '#dc3545';
    if (trade.status === 'agreed') return '#28a745';
    return '#6c757d';
  };

  return (
    <>
      {/* User Switcher Dropdown */}
      <div style={{ marginBottom: '20px' }}>
        <label style={{ fontWeight: 600, marginRight: 8 }}>Select User:</label>
        <select
          value={currentUserId}
          onChange={e => setCurrentUserId(Number(e.target.value))}
          style={{ padding: '8px', borderRadius: '6px', fontSize: '14px' }}
        >
          {allUsers.map(user => (
            <option key={user.id} value={user.id}>
              {user.username} (ID: {user.id})
            </option>
          ))}
        </select>
      </div>

      {/* Upper Section: Trading Partners Management */}
      <div style={{ height: '50vh', background: 'rgba(255, 255, 255, 0.95)', borderRadius: '15px', padding: '20px', marginBottom: '20px', boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)' }}>
        <h2 style={{ color: '#2c3e50', marginBottom: '20px' }}>🤝 Trading Partners</h2>
        
        {/* Add Partner Section */}
        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ color: '#34495e', marginBottom: '10px' }}>Add New Partner</h3>
          <form onSubmit={addPartner} style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
            <input 
              type="text" 
              value={partnerInput}
              onChange={(e) => setPartnerInput(e.target.value)}
              placeholder="Enter partner username" 
              style={{ flex: 1, padding: '12px', border: '2px solid #e9ecef', borderRadius: '8px', fontSize: '14px' }}
            />
            <button type="submit" style={{ padding: '12px 24px', background: '#3498db', color: 'white', border: 'none', borderRadius: '8px', fontWeight: 'bold', cursor: 'pointer', transition: 'background 0.2s' }}>
              Add Partner
            </button>
          </form>
        </div>

        {/* Existing Users Section */}
        <div style={{ marginBottom: '20px' }}>
          <h3 style={{ color: '#34495e', marginBottom: '10px' }}>Quick Add from Existing Users</h3>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
            {existingUsers
              .filter(user => user.id !== currentUserId)
              .map(user => (
                <button
                  key={user.id}
                  onClick={() => addExistingUserAsPartner(user)}
                  style={{ padding: '8px 12px', background: '#007bff', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontSize: '12px' }}
                >
                  {user.username} (ID: {user.id})
                </button>
              ))
            }
          </div>
        </div>

        {/* Partners List */}
        <div>
          <h3 style={{ color: '#34495e', marginBottom: '10px' }}>Active Partners</h3>
          <div style={{ maxHeight: '200px', overflowY: 'auto' }}>
            {partners.length === 0 ? (
              <div style={{ color: '#7f8c8d', textAlign: 'center', padding: '20px' }}>
                No trading partners yet. Add some partners to start trading!
              </div>
            ) : (
              partners.map(partner => (
                <div
                  key={partner.id}
                  onClick={() => initiateTradeWithPartner(partner.partnerId)}
                  style={{ padding: '8px', margin: '5px 0', background: '#e9ecef', borderRadius: '5px', cursor: 'pointer' }}
                >
                  Partner ID: {partner.partnerId} ({partner.status}) - Click to initiate trade
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      {/* Lower Section: Trade Negotiations Table */}
      <div style={{ height: '50vh', background: 'rgba(255, 255, 255, 0.95)', borderRadius: '15px', padding: '20px', boxShadow: '0 8px 32px rgba(0, 0, 0, 0.1)' }}>
        <h2 style={{ color: '#2c3e50', marginBottom: '20px' }}>📊 Trade Negotiations</h2>
        
        {/* Trade Table */}
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
            <thead>
              <tr style={{ background: '#f8f9fa', borderBottom: '2px solid #dee2e6' }}>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Trade ID</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Type</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Instrument</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Quantity</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Price</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Status</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Counterparty</th>
                <th style={{ padding: '12px', textAlign: 'left', color: '#495057', fontWeight: 600 }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {p2pTrades.length === 0 ? (
                <tr>
                  <td colSpan={8} style={{ padding: '40px', textAlign: 'center', color: '#7f8c8d' }}>
                    No active trades. Start trading with your partners!
                  </td>
                </tr>
              ) : (
                p2pTrades.map(trade => {
                  const isSender = trade.senderId === currentUserId;
                  const isReceiver = trade.receiverId === currentUserId;
                  const expired = isExpired(trade);
                  const counterparty = isSender ? `To: User ${trade.receiverId}` : `From: User ${trade.senderId}`;
                  const statusColor = getStatusColor(trade);
                  const newTradeBadge = isNewTrade(trade) ? 
                    <span style={{ background: '#28a745', color: 'white', padding: '2px 6px', borderRadius: '3px', fontSize: '10px', marginLeft: '5px' }}>NEW</span> : '';
                  const statusText = expired ? 'expired' : trade.status;

                  return (
                    <tr key={trade.id}>
                      <td style={{ padding: '12px', fontWeight: 'bold' }}>{trade.id}</td>
                      <td style={{ padding: '12px' }}>{trade.tradeType}</td>
                      <td style={{ padding: '12px' }}>{trade.instrument}</td>
                      <td style={{ padding: '12px' }}>{trade.quantity}</td>
                      <td style={{ padding: '12px', fontWeight: 'bold' }}>${trade.price.toLocaleString()}</td>
                      <td style={{ padding: '12px', color: statusColor, fontWeight: 'bold' }}>
                        {statusText} {newTradeBadge}
                      </td>
                      <td style={{ padding: '12px' }}>{counterparty}</td>
                      <td style={{ padding: '12px' }}>
                        <button 
                          onClick={() => showTradeDetails(trade.id)}
                          style={{ padding: '8px 16px', background: '#007bff', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', marginRight: '8px' }}
                        >
                          View
                        </button>
                        {isReceiver && trade.status === 'pending' && (
                          <>
                            <button
                              onClick={() => acceptTrade(trade.id)}
                              style={{ padding: '8px 16px', background: '#27ae60', color: 'white', border: 'none', borderRadius: '6px', cursor: expired ? 'not-allowed' : 'pointer', fontWeight: 'bold', marginRight: '8px', opacity: expired ? 0.5 : 1 }}
                              disabled={expired}
                            >
                              Accept
                            </button>
                            <button
                              onClick={() => rejectTrade(trade.id)}
                              style={{ padding: '8px 16px', background: '#e74c3c', color: 'white', border: 'none', borderRadius: '6px', cursor: expired ? 'not-allowed' : 'pointer', fontWeight: 'bold', opacity: expired ? 0.5 : 1 }}
                              disabled={expired}
                            >
                              Reject
                            </button>
                          </>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Trade Modal */}
      {showModal && selectedTrade && (
        <TradeModal 
          trade={selectedTrade}
          currentUserId={currentUserId}
          onClose={() => {
            setShowModal(false);
            setSelectedTrade(null);
            loadP2PTrades();
          }}
        />
      )}
    </>
  );
};

export default P2PTrading; 