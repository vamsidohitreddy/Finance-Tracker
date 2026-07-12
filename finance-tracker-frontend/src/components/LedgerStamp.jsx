import React from 'react'
import { formatCurrency } from '../utils/format'

export default function LedgerStamp({ balance }) {
  const isSurplus = Number(balance) >= 0
  return (
    <div className="stamp-wrap">
      <div className={`stamp ${isSurplus ? 'stamp-surplus' : 'stamp-deficit'}`}>
        <div className="stamp-title">{isSurplus ? 'In the black' : 'In the red'}</div>
        <div className="stamp-amount">{formatCurrency(Math.abs(balance))}</div>
      </div>
    </div>
  )
}
