import React from 'react'
import { NavLink } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Sidebar() {
  const { user, logout } = useAuth()

  return (
    <aside className="sidebar">
      <div>
        <div className="brand">Finance Tracker</div>
        {/* <div className="brand-sub">Personal Finance Ledger</div> */}
      </div>

      <nav>
        <NavLink to="/" end className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          Dashboard
        </NavLink>
        <NavLink to="/transactions" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          Transactions
        </NavLink>
        <NavLink to="/budgets" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          Budgets
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-username">Signed in as {user?.username}</div>
        <button className="logout-btn" onClick={logout}>Sign out</button>
      </div>
    </aside>
  )
}
