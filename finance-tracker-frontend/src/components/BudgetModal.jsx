import React, { useState } from 'react'
import { CATEGORY_OPTIONS, extractErrorMessage } from '../utils/format'
import AlertBanner from './AlertBanner'

const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
]

export default function BudgetModal({ initial, defaultMonth, defaultYear, onClose, onSubmit }) {
  const [form, setForm] = useState({
    category: initial?.category || CATEGORY_OPTIONS[5],
    monthlyLimit: initial?.monthlyLimit ?? '',
    month: initial?.month || defaultMonth,
    year: initial?.year || defaultYear,
  })
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const handleChange = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      await onSubmit({
        ...form,
        monthlyLimit: Number(form.monthlyLimit),
        month: Number(form.month),
        year: Number(form.year),
      })
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <h3>{initial ? 'Edit budget' : 'Set a monthly limit'}</h3>
        <AlertBanner>{error}</AlertBanner>
        <form onSubmit={handleSubmit}>
          <div className="form-field">
            <label>Category</label>
            <select value={form.category} onChange={handleChange('category')} disabled={!!initial}>
              {CATEGORY_OPTIONS.map((c) => <option key={c} value={c}>{c}</option>)}
            </select>
          </div>
          <div className="form-field">
            <label>Monthly limit</label>
            <input type="number" step="0.01" min="0.01" required value={form.monthlyLimit} onChange={handleChange('monthlyLimit')} />
          </div>
          <div className="form-grid">
            <div className="form-field">
              <label>Month</label>
              <select value={form.month} onChange={handleChange('month')}>
                {MONTH_NAMES.map((m, i) => <option key={m} value={i + 1}>{m}</option>)}
              </select>
            </div>
            <div className="form-field">
              <label>Year</label>
              <input type="number" required value={form.year} onChange={handleChange('year')} />
            </div>
          </div>
          <div className="form-actions">
            <button type="button" className="btn btn-outline" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Saving…' : initial ? 'Save changes' : 'Set budget'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
