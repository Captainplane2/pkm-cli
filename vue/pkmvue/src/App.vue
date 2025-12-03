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
            :notes="notes"
            :selected-note="selectedNote"
            @select-note="handleSelectNote"
            @create-note="handleCreateNote"
            @edit-note="handleEditNote"
            @refresh-notes="loadNotes"
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
import { ref, onMounted } from 'vue'
import { Notebook, Collection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import NoteList from './components/NoteList.vue'
import NoteEditor from './components/NoteEditor.vue'
import TagManager from './components/TagManager.vue'
import { noteApi } from './services/api'

const activeMenu = ref('notes')
const notes = ref([])
const selectedNote = ref(null)
const showCreateDialog = ref(false)
const newNoteTitle = ref('')
const newNoteContent = ref('')
const creating = ref(false)

onMounted(() => {
  loadNotes()
})

const loadNotes = async () => {
  try {
    notes.value = await noteApi.getAllNotes()
    // 按更新时间倒序排列
    notes.value.sort((a, b) => new Date(b.updatedAt) - new Date(a.updatedAt))
  } catch (error) {
    ElMessage.error('加载笔记失败')
  }
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'tags') {
    selectedNote.value = null
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
  // 这里可以添加按标签筛选的功能
  ElMessage.info(`点击了标签: ${tagName}`)
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
}

.app-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
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