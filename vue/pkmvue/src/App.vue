<template>
  <div class="app-container">
    <el-container class="main-layout">
      <!-- 侧边栏 -->
      <el-aside width="280px" class="sidebar">
        <div class="sidebar-header">
          <h1 class="app-title">
            <el-icon class="app-icon"><Notebook /></el-icon>
            PKM 系统
          </h1>
          <div class="theme-switch">
            <span class="theme-label">{{ isDark ? '暗色' : '亮色' }}</span>
            <el-switch
              v-model="isDark"
              size="small"
              @change="handleThemeChange"
            />
          </div>
        </div>

        <div v-if="authService.state.isAuthenticated" class="user-info">
          <el-avatar :size="32" :src="authService.state.user?.avatar">
            {{ authService.state.user?.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <span class="username">{{ authService.state.user?.username }}</span>
          <el-button type="text" size="small" title="导入数据" @click="triggerImport">
            <el-icon><Upload /></el-icon>
          </el-button>
          <el-button type="text" size="small" title="导出数据" @click="handleExport">
            <el-icon><Download /></el-icon>
          </el-button>
          <el-button type="text" size="small" title="退出登录" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
          </el-button>
        </div>
        <div v-else class="auth-buttons">
          <el-button type="primary" size="small" @click="showLoginDialog">
            登录
          </el-button>
          <el-button size="small" @click="showRegisterDialog">
            注册
          </el-button>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="notes">
            <el-icon><Notebook /></el-icon>
            <span>笔记管理</span>
          </el-menu-item>
          <el-menu-item index="tags">
            <el-icon><Collection /></el-icon>
            <span>标签管理</span>
          </el-menu-item>
          <el-menu-item index="categories">
            <el-icon><FolderOpened /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container v-if="authService.state.isAuthenticated">
        <!-- 笔记列表区域 -->
        <el-aside v-if="activeMenu === 'notes'" width="360px" class="notes-sidebar">
          <NoteList
            :notes="displayNotes"
            :selected-note="selectedNote"
            :active-tag="activeTag"
            :active-category-id="activeCategoryId"
            :categories="categories"
            @select-note="handleSelectNote"
            @create-note="handleCreateNote"
            @edit-note="handleEditNote"
            @refresh-notes="loadNotes"
            @clear-tag-filter="handleClearTagFilter"
            @clear-category-filter="handleClearCategoryFilter"
          />
        </el-aside>

        <!-- 编辑区域 -->
        <el-main class="main-content">
          <div v-if="activeMenu === 'notes'" class="editor-wrapper">
            <NoteEditor
              :current-note="selectedNote"
              :categories="categories"
              @refresh-notes="loadNotes"
            />
          </div>
          
          <div v-else-if="activeMenu === 'tags'" class="tag-manager-wrapper">
            <TagManager @tag-click="handleTagClick" @refresh-tags="loadTags" />
          </div>
          
          <div v-else-if="activeMenu === 'categories'" class="category-manager-wrapper">
            <CategoryManager @category-click="handleCategoryClick" @refresh-categories="loadCategories" />
          </div>
        </el-main>
      </el-container>

      <el-container v-else class="unauth-container">
        <el-main class="welcome-screen">
          <el-empty description="请先登录以访问您的个人知识库">
            <el-button type="primary" @click="showLoginDialog">立即登录</el-button>
          </el-empty>
        </el-main>
      </el-container>
    </el-container>

    <!-- 创建笔记对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="新建笔记"
      width="500px"
    >
      <el-form>
        <el-form-item label="笔记标题">
          <el-input
            v-model="newNoteTitle"
            placeholder="请输入笔记标题"
            @keyup.enter="handleCreateNoteConfirm"
          />
        </el-form-item>
        <el-form-item label="笔记内容">
          <el-input
            v-model="newNoteContent"
            type="textarea"
            :rows="4"
            placeholder="请输入笔记内容（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateNoteConfirm" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>

    <Login
      v-model="showLogin"
      @login-success="handleLoginSuccess"
    />
    
    <Register
      v-model="showRegister"
      @switch-to-login="showLoginDialog"
    />

    <!-- 隐藏的导入文件输入框 -->
    <input
      ref="importFileRef"
      type="file"
      accept=".json"
      style="display: none"
      @change="handleImport"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import { Notebook, Collection, FolderOpened, SwitchButton, Download, Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NoteList from './components/NoteList.vue'
import NoteEditor from './components/NoteEditor.vue'
import TagManager from './components/TagManager.vue'
import CategoryManager from './components/CategoryManager.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import { noteApi, categoryApi } from './services/api'
import { AuthService } from './services/AuthService'

const STORAGE_KEY = 'pkm_notes_cache'
const THEME_KEY = 'pkm_theme'

const activeMenu = ref('notes')
const notes = ref([])
const selectedNote = ref(null)
const activeTag = ref('')
const activeCategoryId = ref(null)
const categories = ref([])
const isDark = ref(false)
const showCreateDialog = ref(false)
const newNoteTitle = ref('')
const newNoteContent = ref('')
const creating = ref(false)
const showLogin = ref(false)
const showRegister = ref(false)
const importFileRef = ref(null)
const authService = AuthService

const displayNotes = computed(() => {
  let filtered = notes.value
  
  // 先按标签筛选
  if (activeTag.value) {
    filtered = filtered.filter(note => Array.isArray(note.tags) && note.tags.includes(activeTag.value))
  }
  
  // 再按分类筛选
  if (activeCategoryId.value !== null) {
    if (activeCategoryId.value === null || activeCategoryId.value === '') {
      // 显示未分类的笔记
      filtered = filtered.filter(note => !note.categoryId)
    } else {
      filtered = filtered.filter(note => note.categoryId === activeCategoryId.value)
    }
  }
  
  return filtered
})

const saveNotesToLocal = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(notes.value))
  } catch (e) {
    console.error('保存到 localStorage 失败', e)
  }
}

const loadNotesFromLocal = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const cached = JSON.parse(raw)
      if (Array.isArray(cached)) {
        notes.value = cached
      }
    }
  } catch (e) {
    console.error('从 localStorage 读取失败', e)
  }
}

onMounted(() => {
  // 恢复主题
  const savedTheme = localStorage.getItem(THEME_KEY)
  if (savedTheme === 'dark') {
    isDark.value = true
    document.body.classList.add('dark')
  }

  // 先从 localStorage 恢复一份，保证刷新页面数据不丢失
  loadNotesFromLocal()
  // 再从后端拉最新数据，更新内存 and localStorage
  loadNotes()
  loadCategories()
})

const loadCategories = async () => {
  try {
    categories.value = await categoryApi.getAllCategories()
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

watch(isDark, (val) => {
  if (val) {
    document.body.classList.add('dark')
    localStorage.setItem(THEME_KEY, 'dark')
  } else {
    document.body.classList.remove('dark')
    localStorage.setItem(THEME_KEY, 'light')
  }
})

const loadNotes = async () => {
  try {
    const serverNotes = await noteApi.getAllNotes()
    notes.value = Array.isArray(serverNotes) ? serverNotes : []
    // 按更新时间倒序排列
    notes.value.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
    saveNotesToLocal()

    // 刷新后尽量保留当前选中的笔记
    if (selectedNote.value) {
      const currentId = selectedNote.value.id
      selectedNote.value = notes.value.find(n => n.id === currentId) || null
    }
  } catch (error) {
    ElMessage.error('加载笔记失败')
  }
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'tags' || index === 'categories') {
    selectedNote.value = null
  }
  if (index === 'notes') {
    // 返回笔记管理时清除筛选，让用户看到全部笔记
    activeTag.value = ''
    activeCategoryId.value = null
  }
}

const handleSelectNote = (note) => {
  selectedNote.value = note
}

const handleCreateNote = () => {
  newNoteTitle.value = ''
  newNoteContent.value = ''
  showCreateDialog.value = true
}

const handleCreateNoteConfirm = async () => {
  if (!newNoteTitle.value.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }

  creating.value = true
  try {
    await noteApi.createNote({
      title: newNoteTitle.value.trim(),
      content: newNoteContent.value.trim()
    })
    ElMessage.success('笔记创建成功')
    showCreateDialog.value = false
    loadNotes()
  } catch (error) {
    ElMessage.error('创建笔记失败：' + (error.handledMessage || '未知错误'))
  } finally {
    creating.value = false
  }
}

const handleEditNote = (note) => {
  selectedNote.value = note
}

const handleTagClick = (tagName) => {
  // 切换到笔记管理并筛选该标签的笔记
  activeMenu.value = 'notes'
  activeTag.value = tagName
  ElMessage.success(`已按标签「${tagName}」筛选笔记`)
}

const handleClearTagFilter = () => {
  activeTag.value = ''
}

const handleClearCategoryFilter = () => {
  activeCategoryId.value = null
}

const handleCategoryClick = (categoryId) => {
  // 切换到笔记管理并筛选该分类的笔记
  activeMenu.value = 'notes'
  activeCategoryId.value = categoryId
  activeTag.value = '' // 清除标签筛选
  ElMessage.success(categoryId ? `已按分类筛选笔记` : `已显示未分类笔记`)
}

const handleThemeChange = () => {
  // 具体逻辑由 watch(isDark) 统一处理，这里无需额外代码
}

const showLoginDialog = () => {
  showLogin.value = true
}

const showRegisterDialog = () => {
  showRegister.value = true
}

const handleLogout = () => {
  AuthService.logout()
  ElMessage.success('已退出登录')
}

const handleExport = async () => {
  try {
    const data = await noteApi.exportData()
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `pkm_export_${new Date().getTime()}.json`
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('导出失败：' + (error.handledMessage || '未知错误'))
  }
}

const triggerImport = () => {
  if (importFileRef.value) {
    importFileRef.value.click()
  }
}

const handleImport = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      const data = JSON.parse(e.target.result)
      await noteApi.importData(data)
      ElMessage.success('数据导入成功')
      loadNotes()
      loadCategories()
    } catch (error) {
      ElMessage.error('导入失败：格式不正确或服务器错误')
      console.error(error)
    } finally {
      event.target.value = '' // 重置输入框
    }
  }
  reader.readAsText(file)
}

const handleLoginSuccess = async () => {
  ElMessage.success('登录成功')
  loadNotes()
}

const handleAuthChange = () => {
  if (!AuthService.isAuthenticated()) {
    notes.value = []
    categories.value = []
    selectedNote.value = null
    activeTag.value = ''
    activeCategoryId.value = null
    // 登出时不应再从本地读取旧数据
    // loadNotesFromLocal() 已经被 AuthService.logout 中的 removeItem 清理
  }
}

onMounted(() => {
  window.addEventListener('auth-change', handleAuthChange)
})

onUnmounted(() => {
  window.removeEventListener('auth-change', handleAuthChange)
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  background-color: #f5f7fa;
  transition: background-color 0.3s;
}

.main-layout {
  height: 100%;
}

.sidebar {
  background-color: #001529;
  color: white;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #2d3748;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.app-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.theme-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #a0aec0;
}

.theme-label {
  white-space: nowrap;
}

.app-icon {
  font-size: 24px;
}

.user-info {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 1px solid #2d3748;
  background-color: rgba(255, 255, 255, 0.05);
}

.user-info .username {
  flex: 1;
  font-size: 14px;
  color: #e2e8f0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-info .el-button {
  color: #a0aec0;
}

.user-info .el-button:hover {
  color: #f56565;
}

.auth-buttons {
  padding: 12px 20px;
  display: flex;
  gap: 8px;
  border-bottom: 1px solid #2d3748;
}

.auth-buttons .el-button {
  flex: 1;
}

.sidebar-menu {
  border: none;
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #a0aec0;
  height: 56px;
  line-height: 56px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: #2d3748;
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #1890ff;
  color: white;
}

.sidebar-menu :deep(.el-icon) {
  font-size: 18px;
}

.notes-sidebar {
  background: white;
  border-right: 1px solid #e4e7ed;
}

.main-content {
  padding: 0;
  background: white;
}

.editor-wrapper,
.tag-manager-wrapper,
.category-manager-wrapper {
  height: 100%;
}

.unauth-container {
  height: 100%;
}

.welcome-screen {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  transition: background-color 0.3s, color 0.3s;
}
</style>

<style>
/* --- 全局暗色模式适配 (针对 Teleport 出来的 Dialog 等) --- */

/* 1. 全局背景适配 */
body.dark .app-container,
body.dark .welcome-screen,
body.dark .main-content,
body.dark .notes-sidebar,
body.dark .unauth-container {
  background-color: #121212 !important;
  color: #ffffff;
}

/* 2. 对话框背景与变量 */
body.dark .el-dialog {
  background-color: #1d1e1f !important;
  --el-dialog-bg-color: #1d1e1f !important;
  border: 1px solid #4c4d4f;
}

/* 3. 提升标题亮度 */
body.dark .el-dialog__title {
  color: #ffffff !important;
  font-weight: 600;
}

/* 4. 表单标签文字颜色 */
body.dark .el-form-item__label {
  color: #cfd3dc !important;
}

/* 5. 统一输入框样式 (黑底白字) */
/* 针对普通的 Input 包装层 */
body.dark .el-input__wrapper {
  background-color: #141414 !important;
  box-shadow: 0 0 0 1px #4c4d4f inset !important;
}

/* 针对 Input 的真实文本区域 */
body.dark .el-input__inner {
  color: #ffffff !important;
  background-color: transparent !important;
  caret-color: #409eff;
}

/* 针对 Textarea 文本区域 */
body.dark .el-textarea__inner {
  background-color: #141414 !important;
  color: #ffffff !important;
  box-shadow: 0 0 0 1px #4c4d4f inset !important;
}

/* 5. 修复占位符 (Placeholder) 颜色 */
body.dark .el-input__inner::placeholder,
body.dark .el-textarea__inner::placeholder {
  color: #606266 !important;
}

/* 6. 焦点状态 (Focus) */
body.dark .el-input__wrapper.is-focus,
body.dark .el-textarea__inner:focus {
  box-shadow: 0 0 0 1px #409eff inset !important;
}

/* 7. 暗色模式下的辅助按钮外观 */
body.dark .el-button:not(.el-button--primary):not(.el-button--danger) {
  background-color: #2b2b2b !important;
  border-color: #4c4d4f !important;
  color: #cfd3dc !important;
}

/* 8. 欢迎页暗色模式文字与按钮 */
body.dark .welcome-screen .el-empty__description p {
  color: #cfd3dc !important;
}

body.dark .welcome-screen .el-button--primary {
  background-color: #409eff !important;
  border-color: #409eff !important;
  color: #ffffff !important;
}

/* --- 亮色模式强制恢复 (解决变黑问题) --- */
body:not(.dark) .el-dialog {
  background-color: #ffffff !important;
  --el-dialog-bg-color: #ffffff !important;
}

body:not(.dark) .el-dialog__title {
  color: #303133 !important;
}

body:not(.dark) .el-form-item__label {
  color: #606266 !important;
}

body:not(.dark) .el-input__wrapper {
  background-color: #ffffff !important;
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
}

body:not(.dark) .el-textarea__inner {
  background-color: #ffffff !important;
  box-shadow: 0 0 0 1px #dcdfe6 inset !important;
  color: #606266 !important;
}

body:not(.dark) .el-input__inner {
  color: #606266 !important;
}
</style>
