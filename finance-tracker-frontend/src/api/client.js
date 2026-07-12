import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const client = axios.create({ baseURL })

// Attach the JWT to every outgoing request, if we have one
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('ledgerline_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// If the token is invalid/expired, the backend returns 401 — send the user back to login
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('ledgerline_token')
      localStorage.removeItem('ledgerline_user')
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default client
