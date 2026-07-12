import React from 'react'

export default function StatCard({ label, value, tone }) {
  const toneClass = tone === 'positive' ? 'stat-positive' : tone === 'negative' ? 'stat-negative' : ''
  return (
    <div className="card stat-card">
      <div className="stat-label">{label}</div>
      <div className={`stat-value ${toneClass}`}>{value}</div>
    </div>
  )
}
