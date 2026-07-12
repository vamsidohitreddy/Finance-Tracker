import client from './client'

export const authApi = {
  signup: (payload) => client.post('/api/auth/signup', payload),
  login: (payload) => client.post('/api/auth/login', payload),
}

export const transactionApi = {
  list: () => client.get('/api/transactions'),
  create: (payload) => client.post('/api/transactions', payload),
  update: (id, payload) => client.put(`/api/transactions/${id}`, payload),
  remove: (id) => client.delete(`/api/transactions/${id}`),
}

export const budgetApi = {
  list: () => client.get('/api/budgets'),
  forMonth: (month, year) => client.get('/api/budgets/month', { params: { month, year } }),
  create: (payload) => client.post('/api/budgets', payload),
  update: (id, payload) => client.put(`/api/budgets/${id}`, payload),
  remove: (id) => client.delete(`/api/budgets/${id}`),
}

export const dashboardApi = {
  summary: () => client.get('/api/dashboard/summary'),
}

export const reportApi = {
  // returns a Blob — caller turns it into a download
  monthly: (month, year) =>
    client.get('/api/reports/monthly', { params: { month, year }, responseType: 'blob' }),
}
