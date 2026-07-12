import React from 'react'
import { formatCurrency } from '../utils/format'

export default function BudgetBar({ budget, onEdit, onDelete }) {
  const pct = Math.min(budget.percentUsed, 100)
  const state = budget.overBudget ? 'over' : budget.percentUsed >= 80 ? 'warn' : 'ok'

  return (
    <div className="budget-row">
      <div className="budget-row-top">
        <span className="cat-name">{budget.category}</span>
        <span className="mono">
          {formatCurrency(budget.spent)} / {formatCurrency(budget.monthlyLimit)}
        </span>
      </div>
      <div className="progress-track">
        <div className={`progress-fill ${state}`} style={{ width: `${pct}%` }} />
      </div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 6 }}>
        <span style={{ fontSize: 12, color: budget.overBudget ? 'var(--danger)' : 'var(--ink-soft)' }}>
          {budget.overBudget
            ? `Over by ${formatCurrency(Math.abs(budget.remaining))}`
            : `${formatCurrency(budget.remaining)} remaining`}
        </span>
        <span>
          <button className="btn-link" onClick={() => onEdit(budget)}>Edit</button>
          <button className="btn-danger-text" onClick={() => onDelete(budget)}>Delete</button>
        </span>
      </div>
    </div>
  )
}
