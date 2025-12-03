import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加认证token等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 笔记相关API
export const noteApi = {
  // 获取所有笔记
  getAllNotes: () => api.get('/notes'),
  
  // 获取单个笔记
  getNoteById: (id) => api.get(`/notes/${id}`),
  
  // 创建笔记
  createNote: (noteData) => api.post('/notes', noteData),
  
  // 更新笔记标题
  updateNoteTitle: (id, title) => api.put(`/notes/${id}/title`, { title }),
  
  // 更新笔记内容
  updateNoteContent: (id, content) => api.put(`/notes/${id}/content`, { content }),
  
  // 删除笔记
  deleteNote: (id) => api.delete(`/notes/${id}`),
  
  // 搜索笔记
  searchNotes: (keyword) => api.get(`/notes/search?keyword=${keyword}`),
  
  // 添加标签
  addTag: (id, tag) => api.post(`/notes/${id}/tags`, { tag }),
  
  // 移除标签
  removeTag: (id, tag) => api.delete(`/notes/${id}/tags/${tag}`)
}

// 标签相关API
export const tagApi = {
  // 获取所有标签
  getAllTags: () => api.get('/tags'),
  
  // 获取标签统计
  getTagStatistics: () => api.get('/tags/statistics'),
  
  // 按标签搜索笔记
  getNotesByTag: (tag) => api.get(`/tags/${tag}/notes`),
  
  // 创建标签
  createTag: (name) => api.post('/tags', { name }),
  
  // 删除标签
  deleteTag: (name) => api.delete(`/tags/${name}`)
}

export default api