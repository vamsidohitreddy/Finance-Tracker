import React, { useEffect, useState } from 'react'
import { budgetApi, reportApi } from '../api/endpoints'
import { extractErrorMessage } from '../utils/format'
import AlertBanner from '../components/AlertBanner'
import BudgetBar from '../components/BudgetBar'
import BudgetModal from '../components/BudgetModal'

const MONTH_NAMES = [
  'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December',
]

export default function Budgets() {
  const now = new Date()
  const [month, setMonth] = useState(now.getMonth() + 1)
  const [year, setYear] = useState(now.getFullYear())
  const [budgets, setBudgets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [downloading, setDownloading] = useState(false)

  const load = async () => {
    setLoading(true)
    try {
      const { data } = await budgetApi.forMonth(month, year)
      setBudgets(data)
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [month, year])

  const handleCreateOrUpdate = async (payload) => {
    if (editing) {
      await budgetApi.update(editing.id, payload)
    } else {
      await budgetApi.create(payload)
    }
    setModalOpen(false)
    setEditing(null)
    await load()
  }

  const handleDelete = async (budget) => {
    if (!window.confirm(`Remove the ${budget.category} budget for ${MONTH_NAMES[budget.month - 1]}?`)) return
    try {
      await budgetApi.remove(budget.id)
      await load()
    } catch (err) {
      setError(extractErrorMessage(err))
    }
  }

  const handleDownloadReport = async () => {
    setDownloading(true)
    setError('')
    try {
      const { data } = await reportApi.monthly(month, year)
      const blob = new Blob([data], { type: 'application/pdf' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `finance-report-${year}-${String(month).padStart(2, '0')}.pdf`
      document.body.appendChild(link)
      link.click()
      link.remove()
      window.URL.revokeObjectURL(url)
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setDownloading(false)
    }
  }

  const openEdit = (budget) => { setEditing(budget); setModalOpen(true) }
  const openCreate = () => { setEditing(null); setModalOpen(true) }
  const overCount = budgets.filter((b) => b.overBudget).length

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Budgets</h1>
          <div className="page-sub">Set monthly limits per category and track spend against them</div>
        </div>
        <div style={{ display: 'flex', gap: 10 }}>
          <button className="btn btn-outline" onClick={handleDownloadReport} disabled={downloading}>
            {downloading ? 'Preparing PDF…' : 'Download monthly report'}
          </button>
          <button className="btn btn-primary" onClick={openCreate}>+ Set budget</button>
        </div>
      </div>

      <AlertBanner>{error}</AlertBanner>

      {overCount > 0 && (
        <AlertBanner>
          <strong>{overCount} categor{overCount === 1 ? 'y is' : 'ies are'} over budget</strong> this month — see the bars below.
        </AlertBanner>
      )}

      <div className="card" style={{ marginBottom: 20 }}>
        <div className="form-grid" style={{ marginBottom: 0 }}>
          <div className="form-field">
            <label>Month</label>
            <select value={month} onChange={(e) => setMonth(Number(e.target.value))}>
              {MONTH_NAMES.map((m, i) => <option key={m} value={i + 1}>{m}</option>)}
            </select>
          </div>
          <div className="form-field">
            <label>Year</label>
            <input type="number" value={year} onChange={(e) => setYear(Number(e.target.value))} />
          </div>
        </div>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading-line">Loading budgets…</div>
        ) : budgets.length === 0 ? (
          <div className="empty-state">No budgets set for {MONTH_NAMES[month - 1]} {year} yet.</div>
        ) : (
          budgets.map((b) => (
            <BudgetBar key={b.id} budget={b} onEdit={openEdit} onDelete={handleDelete} />
          ))
        )}
      </div>

      {modalOpen && (
        <BudgetModal
          initial={editing}
          defaultMonth={month}
          defaultYear={year}
          onClose={() => { setModalOpen(false); setEditing(null) }}
          onSubmit={handleCreateOrUpdate}
        />
      )}
    </div>
  )
}
