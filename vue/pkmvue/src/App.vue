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
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 笔记列表区域 -->
        <el-aside v-if="activeMenu === 'notes'" width="360px" class="notes-sidebar">
          <NoteList
            :notes="displayNotes"
            :selected-note="selectedNote"
            :active-tag="activeTag"
            @select-note="handleSelectNote"
            @create-note="handleCreateNote"
            @edit-note="handleEditNote"
            @refresh-notes="loadNotes"
            @clear-tag-filter="handleClearTagFilter"
          />
        </el-aside>

        <!-- 编辑区域 -->
        <el-main class="main-content">
          <div v-if="activeMenu === 'notes'" class="editor-wrapper">
            <NoteEditor
              :current-note="selectedNote"
              @refresh-notes="loadNotes"
            />
          </div>
          
          <div v-else-if="activeMenu === 'tags'" class="tag-manager-wrapper">
            <TagManager @tag-click="handleTagClick" />
          </div>
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Notebook, Collection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NoteList from './components/NoteList.vue'
import NoteEditor from './components/NoteEditor.vue'
import TagManager from './components/TagManager.vue'
import { noteApi } from './services/api'

const STORAGE_KEY = 'pkm_notes_cache'
const THEME_KEY = 'pkm_theme'

const activeMenu = ref('notes')
const notes = ref([])
const selectedNote = ref(null)
const activeTag = ref('')
const isDark = ref(false)
const showCreateDialog = ref(false)
const newNoteTitle = ref('')
const newNoteContent = ref('')
const creating = ref(false)

const displayNotes = computed(() => {
  if (!activeTag.value) return notes.value
  return notes.value.filter(note => Array.isArray(note.tags) && note.tags.includes(activeTag.value))
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
  // 再从后端拉最新数据，更新内存和 localStorage
  loadNotes()
})

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
  if (index === 'tags') {
    selectedNote.value = null
  }
  if (index === 'notes') {
    // 返回笔记管理时清除标签筛选，让用户看到全部笔记
    activeTag.value = ''
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
    ElMessage.error('创建笔记失败')
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

const handleThemeChange = () => {
  // 具体逻辑由 watch(isDark) 统一处理，这里无需额外代码
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  background-color: #f5f7fa;
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
.tag-manager-wrapper {
  height: 100%;
}
</style>