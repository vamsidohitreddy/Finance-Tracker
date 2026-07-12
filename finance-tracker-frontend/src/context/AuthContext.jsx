import React, { createContext, useContext, useEffect, useState } from 'react'
import { authApi } from '../api/endpoints'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [initializing, setInitializing] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('ledgerline_token')
    const storedUser = localStorage.getItem('ledgerline_user')
    if (token && storedUser) {
      setUser(JSON.parse(storedUser))
    }
    setInitializing(false)
  }, [])

  const persistSession = (authResponse) => {
    const { token, username, email } = authResponse
    localStorage.setItem('ledgerline_token', token)
    localStorage.setItem('ledgerline_user', JSON.stringify({ username, email }))
    setUser({ username, email })
  }

  const login = async (credentials) => {
    const { data } = await authApi.login(credentials)
    persistSession(data)
  }

  const signup = async (details) => {
    const { data } = await authApi.signup(details)
    persistSession(data)
  }

  const logout = () => {
    localStorage.removeItem('ledgerline_token')
    localStorage.removeItem('ledgerline_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, initializing, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
