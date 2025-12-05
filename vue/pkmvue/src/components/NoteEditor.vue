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
        
        <div class="category-section">
          <div class="category-header">
            <span class="section-title">分类</span>
          </div>
          <el-select
            v-model="selectedCategoryId"
            placeholder="选择分类（可选）"
            clearable
            style="width: 100%"
            @change="handleCategoryChange"
          >
            <el-option
              label="未分类"
              value=""
            />
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
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
          <div class="editor-toolbar">
            <span class="section-title">内容</span>
            <div class="toolbar-actions">
              <el-button
                size="small"
                :type="showPreview ? 'default' : 'primary'"
                @click="showPreview = false"
              >
                编辑
              </el-button>
              <el-button
                size="small"
                :type="showPreview ? 'primary' : 'default'"
                @click="showPreview = true"
              >
                预览 (Markdown)
              </el-button>
              <el-dropdown @command="handleExport">
                <el-button size="small">
                  导出
                  <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="md">导出为 Markdown</el-dropdown-item>
                    <el-dropdown-item command="txt">导出为文本</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
          <div class="editor-main">
            <div class="editor-pane">
              <el-input
                v-model="editContent"
                type="textarea"
                placeholder="开始编写你的笔记内容（支持 Markdown 语法）..."
                :autosize="{ minRows: 18, maxRows: 50 }"
                class="content-textarea"
                @blur="handleContentSave"
              />
            </div>
            <div
              v-show="showPreview"
              class="preview-pane"
            >
              <div
                class="markdown-preview"
                v-html="renderedMarkdown"
              ></div>
            </div>
          </div>
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
  import { ref, watch, nextTick, computed } from 'vue'
  import { Check, Plus, ArrowDown } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { noteApi } from '../services/api'
  import { formatFullDate } from '../utils/dateUtils'
  import { marked } from 'marked'
  
  const props = defineProps({
    currentNote: {
      type: Object,
      default: null
    },
    categories: {
      type: Array,
      default: () => []
    }
  })
  
  const emit = defineEmits(['refresh-notes'])
  
  const editTitle = ref('')
  const editContent = ref('')
  const saving = ref(false)
  const showTagInput = ref(false)
  const newTag = ref('')
  const showPreview = ref(false)
  const selectedCategoryId = ref('')

  marked.setOptions({
    breaks: true
  })

  const renderedMarkdown = computed(() => {
    if (!editContent.value) return '<p style="color:#909399;">暂无内容</p>'
    try {
      return marked.parse(editContent.value)
    } catch (e) {
      return '<p style="color:#f56c6c;">Markdown 解析失败</p>'
    }
  })
  
  watch(() => props.currentNote, (newNote) => {
    if (newNote) {
      editTitle.value = newNote.title || ''
      editContent.value = newNote.content || ''
      selectedCategoryId.value = newNote.categoryId || ''
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

  const handleCategoryChange = async (categoryId) => {
    if (!props.currentNote) return
    
    try {
      await noteApi.updateNoteCategory(props.currentNote.id, categoryId || null)
      ElMessage.success('分类已更新')
      emit('refresh-notes')
    } catch (error) {
      ElMessage.error('更新分类失败')
    }
  }

  const handleExport = (format) => {
    if (!props.currentNote) return

    const title = (editTitle.value || props.currentNote.title || '未命名笔记').trim()
    const content = editContent.value || ''

    let fileName = title.replace(/[\\/:*?"<>|]/g, '_')
    let data = ''

    if (format === 'md') {
      fileName = `${fileName || 'note'}.md`
      data = `# ${title}\n\n${content}`
    } else {
      fileName = `${fileName || 'note'}.txt`
      data = `${title}\n\n${content}`
    }

    try {
      const blob = new Blob([data], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    } catch (e) {
      ElMessage.error('导出失败')
    }
  }
  </script>
  
  <style scoped>
  .note-editor {
    height: 100%;
    background: white;
    color: #303133;
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
    background: transparent;
    color: #303133;
  }
  
  .title-input :deep(.el-input__inner):focus {
    box-shadow: none;
  }
  
  .header-actions {
    flex-shrink: 0;
  }
  
  .category-section {
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;
  }

  .category-header {
    margin-bottom: 12px;
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
    display: flex;
    flex-direction: column;
  }

  .editor-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  .toolbar-actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .editor-main {
    flex: 1;
    display: flex;
    gap: 16px;
  }
  
  .content-textarea :deep(.el-textarea__inner) {
    border: none;
    resize: none;
    font-size: 16px;
    line-height: 1.6;
    padding: 0;
    background: transparent;
    color: #303133;
  }
  
  .content-textarea :deep(.el-textarea__inner):focus {
    box-shadow: none;
  }
  
  .content-textarea :deep(.el-textarea__inner)::placeholder {
    color: #909399;
  }

  .editor-pane {
    flex: 1;
  }

  .preview-pane {
    flex: 1;
    border-left: 1px solid #e4e7ed;
    padding-left: 16px;
    overflow-y: auto;
  }

  .markdown-preview {
    font-size: 14px;
    line-height: 1.7;
  }

  .markdown-preview h1,
  .markdown-preview h2,
  .markdown-preview h3 {
    margin: 12px 0 8px;
    font-weight: 600;
  }

  .markdown-preview p {
    margin: 6px 0;
  }

  .markdown-preview ul,
  .markdown-preview ol {
    padding-left: 20px;
    margin: 8px 0;
  }

  .markdown-preview code {
    background: #f5f7fa;
    padding: 2px 4px;
    border-radius: 3px;
    font-family: Menlo, Monaco, Consolas, 'Courier New', monospace;
  }

  .markdown-preview pre code {
    display: block;
    padding: 10px;
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