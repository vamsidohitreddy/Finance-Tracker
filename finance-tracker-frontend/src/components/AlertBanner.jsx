import React from 'react'

export default function AlertBanner({ children, variant = 'danger' }) {
  if (!children) return null
  return (
    <div className={`alert-banner ${variant === 'success' ? 'success' : ''}`}>
      <span>{children}</span>
    </div>
  )
}
