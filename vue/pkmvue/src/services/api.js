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
    // 解析后端返回的错误响应
    let errorMessage = '操作失败，请稍后重试'
    if (error.response && error.response.data) {
      const errorData = error.response.data
      // 检查是否是后端统一的错误响应格式
      if (errorData.errorCode && errorData.message) {
        errorMessage = errorData.message
        // 将完整的错误信息附加到error对象上，方便组件使用
        error.errorResponse = errorData
      } else {
        errorMessage = errorData.message || errorData.error || '操作失败'
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    
    // 将处理后的错误信息添加到error对象
    error.handledMessage = errorMessage
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
  removeTag: (id, tag) => api.delete(`/notes/${id}/tags/${tag}`),
  
  // 更新笔记分类
  updateNoteCategory: (id, categoryId) => api.put(`/notes/${id}/category`, { categoryId }),
  
  // 按分类获取笔记
  getNotesByCategory: (categoryId) => {
    // 如果 categoryId 为 null 或空字符串，使用 'null' 作为路径参数
    const pathParam = categoryId === null || categoryId === '' ? 'null' : categoryId
    return api.get(`/notes/category/${pathParam}`)
  }
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

// 分类相关API
export const categoryApi = {
  // 获取所有分类
  getAllCategories: () => api.get('/categories'),
  
  // 获取单个分类
  getCategoryById: (id) => api.get(`/categories/${id}`),
  
  // 创建分类
  createCategory: (name, description) => api.post('/categories', { name, description }),
  
  // 更新分类
  updateCategory: (id, name, description) => api.put(`/categories/${id}`, { name, description }),
  
  // 删除分类
  deleteCategory: (id) => api.delete(`/categories/${id}`),
  
  // 获取分类下的笔记数量
  getNoteCount: (id) => {
    // 如果id为null或空字符串，使用'null'作为路径参数
    const pathParam = id === null || id === '' ? 'null' : id;
    return api.get(`/categories/${pathParam}/count`);
  },
}

export default api