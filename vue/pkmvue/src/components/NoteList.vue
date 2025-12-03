<template>
    <div class="note-list">
      <div class="list-header">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索笔记..."
          clearable
          @input="handleSearch"
          @clear="handleSearchClear"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-button type="primary" @click="handleCreateNote" class="create-btn">
          <el-icon><Plus /></el-icon>
          新建笔记
        </el-button>
      </div>
  
      <div class="notes-container">
        <div
          v-for="note in filteredNotes"
          :key="note.id"
          :class="['note-card', { active: selectedNote?.id === note.id }]"
          @click="handleSelectNote(note)"
        >
          <div class="note-header">
            <h3 class="note-title">{{ note.title || '无标题' }}</h3>
            <el-dropdown @command="handleNoteCommand(note, $event)" trigger="click">
              <el-icon class="more-btn"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="delete" style="color: #f56c6c;">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          
          <div class="note-preview">
            {{ note.content ? note.content.substring(0, 100) + (note.content.length > 100 ? '...' : '') : '无内容' }}
          </div>
          
          <div class="note-footer">
            <div class="note-tags">
              <el-tag
                v-for="tag in note.tags.slice(0, 3)"
                :key="tag"
                size="small"
                class="tag"
              >
                {{ tag }}
              </el-tag>
              <span v-if="note.tags.length > 3" class="more-tags">+{{ note.tags.length - 3 }}</span>
            </div>
            <div class="note-time">{{ formatDate(note.updatedAt) }}</div>
          </div>
        </div>
        
        <div v-if="filteredNotes.length === 0" class="empty-state">
          <el-empty description="暂无笔记" />
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { Search, Plus, MoreFilled } from '@element-plus/icons-vue'
  import { formatDate } from '../utils/dateUtils'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { noteApi } from '../services/api'
  
  const props = defineProps({
    notes: {
      type: Array,
      default: () => []
    },
    selectedNote: {
      type: Object,
      default: null
    }
  })
  
  const emit = defineEmits(['select-note', 'create-note', 'edit-note', 'refresh-notes'])
  
  const searchKeyword = ref('')
  const filteredNotes = computed(() => {
    if (!searchKeyword.value) {
      return props.notes
    }
    
    const keyword = searchKeyword.value.toLowerCase()
    return props.notes.filter(note => 
      note.title?.toLowerCase().includes(keyword) ||
      note.content?.toLowerCase().includes(keyword) ||
      note.tags?.some(tag => tag.toLowerCase().includes(keyword))
    )
  })
  
  const handleSelectNote = (note) => {
    emit('select-note', note)
  }
  
  const handleCreateNote = () => {
    emit('create-note')
  }
  
  const handleSearch = () => {
    // 搜索逻辑已经在computed属性中处理
  }
  
  const handleSearchClear = () => {
    searchKeyword.value = ''
  }
  
  const handleNoteCommand = async (note, command) => {
    if (command === 'edit') {
      emit('edit-note', note)
    } else if (command === 'delete') {
      try {
        await ElMessageBox.confirm(
          '确定要删除这篇笔记吗？此操作不可恢复。',
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await noteApi.deleteNote(note.id)
        ElMessage.success('笔记删除成功')
        emit('refresh-notes')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .note-list {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .list-header {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    gap: 12px;
    align-items: center;
  }
  
  .create-btn {
    flex-shrink: 0;
  }
  
  .notes-container {
    flex: 1;
    overflow-y: auto;
    padding: 0;
  }
  
  .note-card {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
  }
  
  .note-card:hover {
    background-color: #f5f7fa;
  }
  
  .note-card.active {
    background-color: #ecf5ff;
    border-right: 3px solid #409eff;
  }
  
  .note-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
  }
  
  .note-title {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    flex: 1;
    line-height: 1.4;
  }
  
  .more-btn {
    color: #909399;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    transition: background-color 0.3s;
  }
  
  .more-btn:hover {
    background-color: #e4e7ed;
  }
  
  .note-preview {
    color: #606266;
    font-size: 14px;
    line-height: 1.5;
    margin-bottom: 12px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    line-clamp: 2;
    overflow: hidden;
  }
  
  .note-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .note-tags {
    display: flex;
    gap: 4px;
    align-items: center;
    flex-wrap: wrap;
  }
  
  .tag {
    margin-right: 4px;
  }
  
  .more-tags {
    font-size: 12px;
    color: #909399;
  }
  
  .note-time {
    font-size: 12px;
    color: #909399;
    flex-shrink: 0;
  }
  
  .empty-state {
    padding: 60px 20px;
    text-align: center;
  }
  </style>