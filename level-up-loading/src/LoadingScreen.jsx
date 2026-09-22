import React, { useState, useEffect } from 'react';
import './index.css';

const LoadingScreen = () => {
  const [tipIndex, setTipIndex] = useState(0);

  const loadingTips = [
    "Calibrating concentric stability rings...",
    "Apex AI is analyzing your growth trajectory...",
    "Preparing evening spinal decompression...",
    "Optimizing box breathing engines...",
    "Syncing your human perfection data..."
  ];

  useEffect(() => {
    const interval = setInterval(() => {
      setTipIndex((prevIndex) => (prevIndex + 1) % loadingTips.length);
    }, 2500);

    return () => clearInterval(interval);
  }, [loadingTips.length]);

  return (
    <div className="loading-container">
      {/* Concentric Stability Rings Animation */}
      <div className="rings-wrapper">
        <div className="ring ring-outer"></div>
        <div className="ring ring-middle"></div>
        <div className="ring ring-inner"></div>
        
        {/* Center Logo / Pulse */}
        <div className="center-logo">
          <span>LU</span>
        </div>
      </div>

      {/* Text & Typography Container */}
      <div className="text-container">
        <h1 className="title">Level Up</h1>
        <h2 className="subtitle">Human Perfection Engine</h2>

        {/* Dynamic Loading Tip */}
        <div className="tip-container">
          <p key={tipIndex} className="tip-text">
            {loadingTips[tipIndex]}
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoadingScreen;
