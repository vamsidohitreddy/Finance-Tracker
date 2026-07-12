import React, { useEffect, useState } from 'react'
import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend,
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
} from 'recharts'
import { dashboardApi, budgetApi } from '../api/endpoints'
import { formatCurrency, extractErrorMessage } from '../utils/format'
import StatCard from '../components/StatCard'
import LedgerStamp from '../components/LedgerStamp'
import AlertBanner from '../components/AlertBanner'

const SLICE_COLORS = ['#1c5c3f', '#a9791f', '#a4342a', '#5b6459', '#3f7a5a', '#c99a3f', '#7a4a3f', '#2f4858']

export default function Dashboard() {
  const [summary, setSummary] = useState(null)
  const [overBudgetCategories, setOverBudgetCategories] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const now = new Date()
        const [{ data: summaryData }, { data: budgets }] = await Promise.all([
          dashboardApi.summary(),
          budgetApi.forMonth(now.getMonth() + 1, now.getFullYear()),
        ])
        setSummary(summaryData)
        setOverBudgetCategories(budgets.filter((b) => b.overBudget))
      } catch (err) {
        setError(extractErrorMessage(err))
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  if (loading) return <div className="loading-line">Tallying the books…</div>

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Dashboard</h1>
          <div className="page-sub">A running summary of everything in your account</div>
        </div>
      </div>

      <AlertBanner>{error}</AlertBanner>

      {overBudgetCategories.length > 0 && (
        <AlertBanner>
          <strong>Over budget this month:</strong>{' '}
          {overBudgetCategories.map((b) => b.category).join(', ')}. Check the Budgets page for details.
        </AlertBanner>
      )}

      <div className="stat-grid">
        <StatCard label="Total income" value={formatCurrency(summary?.totalIncome)} tone="positive" />
        <StatCard label="Total expense" value={formatCurrency(summary?.totalExpense)} tone="negative" />
        <StatCard
          label="Net balance"
          value={formatCurrency(summary?.balance)}
          tone={summary?.balance >= 0 ? 'positive' : 'negative'}
        />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.3fr', gap: 20 }}>
        <div className="card">
          <h3 style={{ fontSize: 16, marginBottom: 14 }}>Expenses by category</h3>
          {summary?.expenseByCategory?.length ? (
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={summary.expenseByCategory}
                  dataKey="total"
                  nameKey="category"
                  cx="50%" cy="50%"
                  outerRadius={90}
                  label={({ category }) => category}
                >
                  {summary.expenseByCategory.map((_, i) => (
                    <Cell key={i} fill={SLICE_COLORS[i % SLICE_COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(v) => formatCurrency(v)} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="empty-state">No expenses recorded yet.</div>
          )}
        </div>

        <div className="card">
          <h3 style={{ fontSize: 16, marginBottom: 14 }}>Income vs. expense by month</h3>
          {summary?.monthlyTrends?.length ? (
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={summary.monthlyTrends}>
                <CartesianGrid strokeDasharray="3 3" stroke="#d9d0b8" />
                <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip formatter={(v) => formatCurrency(v)} />
                <Legend />
                <Bar dataKey="income" fill="#1c5c3f" name="Income" radius={[3, 3, 0, 0]} />
                <Bar dataKey="expense" fill="#a4342a" name="Expense" radius={[3, 3, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="empty-state">Add a few transactions to see trends here.</div>
          )}
        </div>
      </div>
    </div>
  )
}
