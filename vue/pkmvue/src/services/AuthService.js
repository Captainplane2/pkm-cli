import { authApi } from './api'
import { reactive } from 'vue'

const authState = reactive({
  isAuthenticated: false,
  user: null,
  token: null,
  loading: false
})

function loadAuthState() {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user')
  if (token && user) {
    authState.token = token
    authState.user = JSON.parse(user)
    authState.isAuthenticated = true
  }
}

loadAuthState()

window.addEventListener('auth-change', () => {
  loadAuthState()
})

export const AuthService = {
  state: authState,

  async login(username, password) {
    authState.loading = true
    try {
      const response = await authApi.login({ username, password })
      const { token, user } = response
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
      authState.token = token
      authState.user = user
      authState.isAuthenticated = true
      return { success: true }
    } catch (error) {
      return {
        success: false,
        message: error.handledMessage || '登录失败，请检查用户名和密码'
      }
    } finally {
      authState.loading = false
    }
  },

  async register(username, email, password) {
    authState.loading = true
    try {
      await authApi.register({ username, email, password })
      return { success: true, message: '注册成功，请登录' }
    } catch (error) {
      return {
        success: false,
        message: error.handledMessage || '注册失败，请稍后重试'
      }
    } finally {
      authState.loading = false
    }
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('pkm_notes_cache')
    localStorage.removeItem('pkm_theme') // 可选：是否也重置主题
    authState.token = null
    authState.user = null
    authState.isAuthenticated = false
    window.dispatchEvent(new Event('auth-change'))
  },

  isAuthenticated() {
    return authState.isAuthenticated
  },

  getUser() {
    return authState.user
  }
}
