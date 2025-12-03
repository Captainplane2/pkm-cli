// 日期格式化工具
export const formatDate = (dateString) => {
    if (!dateString) return ''
    
    const date = new Date(dateString)
    const now = new Date()
    const diffTime = Math.abs(now - date)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    
    if (diffDays === 1) {
      return '昨天'
    } else if (diffDays === 2) {
      return '前天'
    } else if (diffDays > 2 && diffDays <= 7) {
      return `${diffDays}天前`
    } else if (diffDays > 7) {
      return date.toLocaleDateString('zh-CN')
    } else {
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }
  }
  
  export const formatFullDate = (dateString) => {
    if (!dateString) return ''
    return new Date(dateString).toLocaleString('zh-CN')
  }