import React, { useState } from 'react';
import LiveTrades from './components/LiveTrades';
import P2PTrading from './components/P2PTrading';
import './App.css';

function App() {
  const [activeTab, setActiveTab] = useState<'liveTrades' | 'p2pTrading'>('liveTrades');

  return (
    <div className="container">
      {/* Tab Navigation */}
      <div className="tab-nav">
        <button 
          className={`tab-btn ${activeTab === 'liveTrades' ? 'active' : ''}`}
          onClick={() => setActiveTab('liveTrades')}
        >
          Live Trades
        </button>
        <button 
          className={`tab-btn ${activeTab === 'p2pTrading' ? 'active' : ''}`}
          onClick={() => setActiveTab('p2pTrading')}
        >
          P2P Trading
        </button>
      </div>

      {/* Tab Content */}
      {activeTab === 'liveTrades' && <LiveTrades />}
      {activeTab === 'p2pTrading' && <P2PTrading />}
    </div>
  );
}

export default App; 