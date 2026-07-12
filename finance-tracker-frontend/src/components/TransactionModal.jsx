import React, { useState } from 'react'
import { CATEGORY_OPTIONS, extractErrorMessage } from '../utils/format'
import AlertBanner from './AlertBanner'

const today = () => new Date().toISOString().slice(0, 10)

export default function TransactionModal({ initial, onClose, onSubmit }) {
  const [form, setForm] = useState({
    category: initial?.category || CATEGORY_OPTIONS[0],
    amount: initial?.amount ?? '',
    type: initial?.type || 'EXPENSE',
    date: initial?.date || today(),
    description: initial?.description || '',
  })
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      await onSubmit({ ...form, amount: Number(form.amount) })
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <h3>{initial ? 'Edit entry' : 'Add ledger entry'}</h3>
        <AlertBanner>{error}</AlertBanner>
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            <div className="form-field">
              <label>Type</label>
              <select value={form.type} onChange={handleChange('type')}>
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
            </div>
            <div className="form-field">
              <label>Amount</label>
              <input type="number" step="0.01" min="0.01" required value={form.amount} onChange={handleChange('amount')} />
            </div>
            <div className="form-field">
              <label>Category</label>
              <select value={form.category} onChange={handleChange('category')}>
                {CATEGORY_OPTIONS.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div className="form-field">
              <label>Date</label>
              <input type="date" required value={form.date} onChange={handleChange('date')} />
            </div>
            <div className="form-field full">
              <label>Description (optional)</label>
              <input type="text" placeholder="e.g. Weekly groceries" value={form.description} onChange={handleChange('description')} />
            </div>
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : initial ? 'Save changes' : 'Add entry'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
