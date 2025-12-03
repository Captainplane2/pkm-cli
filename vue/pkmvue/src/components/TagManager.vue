<template>
    <div class="tag-manager">
      <div class="manager-header">
        <h3>标签管理</h3>
        <el-button type="primary" size="small" @click="showCreateTag = true">
          <el-icon><Plus /></el-icon>
          新建标签
        </el-button>
      </div>
      
      <div class="tags-grid">
        <div
          v-for="tag in tagStats"
          :key="tag.name"
          class="tag-card"
          @click="handleTagClick(tag.name)"
        >
          <div class="tag-header">
            <span class="tag-name">{{ tag.name }}</span>
            <el-dropdown @command="handleTagCommand(tag.name, $event)" trigger="click">
              <el-icon class="more-btn"><MoreFilled /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="delete" style="color: #f56c6c;">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="tag-count">{{ tag.count }} 篇笔记</div>
        </div>
        
        <div v-if="tagStats.length === 0" class="empty-tags">
          <el-empty description="暂无标签" />
        </div>
      </div>
      
      <!-- 创建标签对话框 -->
      <el-dialog
        v-model="showCreateTag"
        title="新建标签"
        width="400px"
      >
        <el-form>
          <el-form-item label="标签名称">
            <el-input
              v-model="newTagName"
              placeholder="请输入标签名称"
              @keyup.enter="handleCreateTag"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showCreateTag = false">取消</el-button>
          <el-button type="primary" @click="handleCreateTag" :loading="creating">
            创建
          </el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { Plus, MoreFilled } from '@element-plus/icons-vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { tagApi } from '../services/api'
  
  const emit = defineEmits(['tag-click'])
  
  const tagStats = ref([])
  const showCreateTag = ref(false)
  const newTagName = ref('')
  const creating = ref(false)
  
  onMounted(() => {
    loadTagStatistics()
  })
  
  const loadTagStatistics = async () => {
    try {
      const stats = await tagApi.getTagStatistics()
      // 将对象转换为数组
      tagStats.value = Object.entries(stats).map(([name, count]) => ({
        name,
        count
      })).sort((a, b) => b.count - a.count)
    } catch (error) {
      ElMessage.error('加载标签统计失败')
    }
  }
  
  const handleTagClick = (tagName) => {
    emit('tag-click', tagName)
  }
  
  const handleTagCommand = async (tagName, command) => {
    if (command === 'delete') {
      try {
        await ElMessageBox.confirm(
          `确定要删除标签 "${tagName}" 吗？此操作会从所有笔记中移除该标签。`,
          '删除确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        await tagApi.deleteTag(tagName)
        ElMessage.success('标签删除成功')
        loadTagStatistics()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除标签失败')
        }
      }
    }
  }
  
  const handleCreateTag = async () => {
    if (!newTagName.value.trim()) {
      ElMessage.warning('请输入标签名称')
      return
    }
    
    creating.value = true
    try {
      await tagApi.createTag(newTagName.value.trim())
      ElMessage.success('标签创建成功')
      showCreateTag.value = false
      newTagName.value = ''
      loadTagStatistics()
    } catch (error) {
      ElMessage.error('创建标签失败')
    } finally {
      creating.value = false
    }
  }
  </script>
  
  <style scoped>
  .tag-manager {
    padding: 20px;
  }
  
  .manager-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .manager-header h3 {
    margin: 0;
    color: #303133;
  }
  
  .tags-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .tag-card {
    background: white;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    padding: 16px;
    cursor: pointer;
    transition: all 0.3s ease;
    position: relative;
  }
  
  .tag-card:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .tag-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 8px;
  }
  
  .tag-name {
    font-weight: 600;
    color: #303133;
    font-size: 16px;
    flex: 1;
  }
  
  .more-btn {
    color: #909399;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    transition: background-color 0.3s;
    flex-shrink: 0;
  }
  
  .more-btn:hover {
    background-color: #e4e7ed;
  }
  
  .tag-count {
    color: #909399;
    font-size: 14px;
  }
  
  .empty-tags {
    grid-column: 1 / -1;
    padding: 40px 20px;
  }
  </style>