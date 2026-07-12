import React, { useEffect, useState } from 'react'
import { transactionApi } from '../api/endpoints'
import { formatCurrency, extractErrorMessage } from '../utils/format'
import AlertBanner from '../components/AlertBanner'
import TransactionModal from '../components/TransactionModal'

export default function Transactions() {
  const [transactions, setTransactions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)

  const load = async () => {
    setLoading(true)
    try {
      const { data } = await transactionApi.list()
      setTransactions(data)
    } catch (err) {
      setError(extractErrorMessage(err))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleCreateOrUpdate = async (payload) => {
    if (editing) {
      await transactionApi.update(editing.id, payload)
    } else {
      await transactionApi.create(payload)
    }
    setModalOpen(false)
    setEditing(null)
    await load()
  }

  const handleDelete = async (tx) => {
    if (!window.confirm(`Delete this ${tx.type.toLowerCase()} entry of ${formatCurrency(tx.amount)}?`)) return
    try {
      await transactionApi.remove(tx.id)
      await load()
    } catch (err) {
      setError(extractErrorMessage(err))
    }
  }

  const openEdit = (tx) => { setEditing(tx); setModalOpen(true) }
  const openCreate = () => { setEditing(null); setModalOpen(true) }

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Transactions</h1>
          <div className="page-sub">Every entry, in the order it was recorded</div>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>+ Add entry</button>
      </div>

      <AlertBanner>{error}</AlertBanner>

      <div className="card">
        {loading ? (
          <div className="loading-line">Loading entries…</div>
        ) : transactions.length === 0 ? (
          <div className="empty-state">No entries yet — add your first transaction to start the ledger.</div>
        ) : (
          <table className="ledger-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Category</th>
                <th>Description</th>
                <th>Amount</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((tx) => (
                <tr key={tx.id}>
                  <td className="mono">{tx.date}</td>
                  <td><span className="category-pill">{tx.category}</span></td>
                  <td>{tx.description || '—'}</td>
                  <td className={tx.type === 'INCOME' ? 'amount-income' : 'amount-expense'}>
                    {tx.type === 'INCOME' ? '+' : '−'}{formatCurrency(tx.amount)}
                  </td>
                  <td>
                    <button className="btn-link" onClick={() => openEdit(tx)}>Edit</button>
                    <button className="btn-danger-text" onClick={() => handleDelete(tx)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {modalOpen && (
        <TransactionModal
          initial={editing}
          onClose={() => { setModalOpen(false); setEditing(null) }}
          onSubmit={handleCreateOrUpdate}
        />
      )}
    </div>
  )
}
