<template>
    <div class="note-editor">
      <div v-if="currentNote" class="editor-container">
        <div class="editor-header">
          <el-input
            v-model="editTitle"
            placeholder="输入笔记标题..."
            class="title-input"
            @blur="handleTitleSave"
            @keyup.enter="handleTitleSave"
          />
          <div class="header-actions">
            <el-button 
              type="primary" 
              :loading="saving" 
              @click="handleSave"
            >
              <template #icon>
                <el-icon><Check /></el-icon>
              </template>
              保存
            </el-button>
          </div>
        </div>
        
        <div class="tags-section">
          <div class="tags-header">
            <span class="section-title">标签</span>
            <el-button text @click="showTagInput = !showTagInput">
              <el-icon><Plus /></el-icon>
              添加标签
            </el-button>
          </div>
          
          <div v-if="showTagInput" class="tag-input-container">
            <el-input
              v-model="newTag"
              placeholder="输入标签名称"
              size="small"
              style="width: 200px; margin-right: 8px;"
              @keyup.enter="handleAddTag"
            />
            <el-button type="primary" size="small" @click="handleAddTag">添加</el-button>
            <el-button size="small" @click="showTagInput = false">取消</el-button>
          </div>
          
          <div class="tags-list">
            <el-tag
              v-for="tag in currentNote.tags"
              :key="tag"
              closable
              @close="handleRemoveTag(tag)"
              class="tag-item"
            >
              {{ tag }}
            </el-tag>
            <div v-if="currentNote.tags.length === 0" class="no-tags">
              暂无标签
            </div>
          </div>
        </div>
        
        <div class="editor-content">
          <el-input
            v-model="editContent"
            type="textarea"
            placeholder="开始编写你的笔记内容..."
            :autosize="{ minRows: 20, maxRows: 50 }"
            class="content-textarea"
            @blur="handleContentSave"
          />
        </div>
        
        <div class="editor-footer">
          <div class="note-info">
            <span>创建时间: {{ formatFullDate(currentNote.createdAt) }}</span>
            <span>更新时间: {{ formatFullDate(currentNote.updatedAt) }}</span>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-editor">
        <el-empty description="选择或创建一篇笔记开始编辑" />
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, watch, nextTick } from 'vue'
  import { Check, Plus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { noteApi } from '../services/api'
  import { formatFullDate } from '../utils/dateUtils'
  
  const props = defineProps({
    currentNote: {
      type: Object,
      default: null
    }
  })
  
  const emit = defineEmits(['refresh-notes'])
  
  const editTitle = ref('')
  const editContent = ref('')
  const saving = ref(false)
  const showTagInput = ref(false)
  const newTag = ref('')
  
  watch(() => props.currentNote, (newNote) => {
    if (newNote) {
      editTitle.value = newNote.title || ''
      editContent.value = newNote.content || ''
      showTagInput.value = false
      newTag.value = ''
    }
  }, { immediate: true })
  
  const handleTitleSave = async () => {
    if (!props.currentNote || !editTitle.value.trim()) return
    
    try {
      await noteApi.updateNoteTitle(props.currentNote.id, editTitle.value)
      ElMessage.success('标题已保存')
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('保存标题失败')
    }
  }
  
  const handleContentSave = async () => {
    if (!props.currentNote) return
    
    try {
      await noteApi.updateNoteContent(props.currentNote.id, editContent.value)
      ElMessage.success('内容已保存')
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('保存内容失败')
    }
  }
  
  const handleSave = async () => {
    if (!props.currentNote) return
    
    saving.value = true
    try {
      await Promise.all([
        noteApi.updateNoteTitle(props.currentNote.id, editTitle.value),
        noteApi.updateNoteContent(props.currentNote.id, editContent.value)
      ])
      ElMessage.success('笔记已保存')
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  }
  
  const handleAddTag = async () => {
    if (!props.currentNote || !newTag.value.trim()) return
    
    try {
      await noteApi.addTag(props.currentNote.id, newTag.value.trim())
      ElMessage.success('标签添加成功')
      newTag.value = ''
      showTagInput.value = false
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('添加标签失败')
    }
  }
  
  const handleRemoveTag = async (tag) => {
    if (!props.currentNote) return
    
    try {
      await noteApi.removeTag(props.currentNote.id, tag)
      ElMessage.success('标签已移除')
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('移除标签失败')
    }
  }
  </script>
  
  <style scoped>
  .note-editor {
    height: 100%;
    background: white;
  }
  
  .editor-container {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  
  .editor-header {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .title-input {
    flex: 1;
  }
  
  .title-input :deep(.el-input__inner) {
    font-size: 24px;
    font-weight: 600;
    border: none;
    padding: 0;
    height: auto;
  }
  
  .title-input :deep(.el-input__inner):focus {
    box-shadow: none;
  }
  
  .header-actions {
    flex-shrink: 0;
  }
  
  .tags-section {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .tags-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }
  
  .section-title {
    font-weight: 600;
    color: #303133;
  }
  
  .tag-input-container {
    margin-bottom: 12px;
  }
  
  .tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .tag-item {
    margin-bottom: 4px;
  }
  
  .no-tags {
    color: #909399;
    font-size: 14px;
  }
  
  .editor-content {
    flex: 1;
    padding: 20px;
  }
  
  .content-textarea :deep(.el-textarea__inner) {
    border: none;
    resize: none;
    font-size: 16px;
    line-height: 1.6;
    padding: 0;
  }
  
  .content-textarea :deep(.el-textarea__inner):focus {
    box-shadow: none;
  }
  
  .editor-footer {
    padding: 16px 20px;
    border-top: 1px solid #f0f0f0;
    background-color: #fafafa;
  }
  
  .note-info {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #909399;
  }
  
  .empty-editor {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
  }
  </style>