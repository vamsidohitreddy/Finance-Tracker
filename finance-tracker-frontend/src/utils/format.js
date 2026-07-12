export function formatCurrency(value) {
  const num = Number(value ?? 0)
  return num.toLocaleString('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 2 })
}

export function extractErrorMessage(err) {
  const data = err?.response?.data
  if (!data) return 'Something went wrong. Please try again.'
  if (data.errors) {
    return Object.values(data.errors).join(', ')
  }
  return data.message || 'Something went wrong. Please try again.'
}

export const CATEGORY_OPTIONS = [
  'Salary', 'Freelance', 'Investment', 'Gift', 'Other Income',
  'Food', 'Rent', 'Utilities', 'Transport', 'Entertainment',
  'Healthcare', 'Shopping', 'Education', 'Travel', 'Other Expense',
]
