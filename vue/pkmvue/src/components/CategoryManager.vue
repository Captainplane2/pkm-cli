<template>
  <div class="category-manager">
    <div class="manager-header">
      <h3>分类管理</h3>
      <el-button type="primary" size="small" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        新建分类
      </el-button>
    </div>
    
    <div class="categories-list">
      <!-- 未分类 -->
      <div
        class="category-item"
        :class="{ active: selectedCategoryId === null }"
        @click="handleSelectCategory(null)"
      >
        <div class="category-info">
          <el-icon class="category-icon"><FolderOpened /></el-icon>
          <div class="category-details">
            <span class="category-name">未分类</span>
            <span class="category-count">{{ uncategorizedCount }} 篇笔记</span>
          </div>
        </div>
      </div>
      
      <!-- 分类列表 -->
      <div
        v-for="category in categories"
        :key="category.id"
        class="category-item"
        :class="{ active: selectedCategoryId === category.id }"
        @click="handleSelectCategory(category.id)"
      >
        <div class="category-info">
          <el-icon class="category-icon"><Folder /></el-icon>
          <div class="category-details">
            <span class="category-name">{{ category.name }}</span>
            <span class="category-count">{{ category.noteCount || 0 }} 篇笔记</span>
          </div>
        </div>
        <el-dropdown @command="handleCategoryCommand(category, $event)" trigger="click">
          <el-icon class="more-btn" @click.stop><MoreFilled /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="edit">编辑</el-dropdown-item>
              <el-dropdown-item command="delete" style="color: #f56c6c;">删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      
      <div v-if="categories.length === 0" class="empty-categories">
        <el-empty description="暂无分类，创建分类来组织你的笔记" />
      </div>
    </div>
    
    <!-- 创建分类对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="新建分类"
      width="500px"
    >
      <el-form>
        <el-form-item label="分类名称" required>
          <el-input
            v-model="newCategoryName"
            placeholder="请输入分类名称"
            @keyup.enter="handleCreateCategory"
          />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input
            v-model="newCategoryDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreateCategory" :loading="creating">
          创建
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 编辑分类对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑分类"
      width="500px"
    >
      <el-form>
        <el-form-item label="分类名称" required>
          <el-input
            v-model="editCategoryName"
            placeholder="请输入分类名称"
            @keyup.enter="handleUpdateCategory"
          />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input
            v-model="editCategoryDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateCategory" :loading="updating">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Plus, Folder, FolderOpened, MoreFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi, noteApi } from '../services/api'

const emit = defineEmits(['category-click', 'refresh-categories'])

const categories = ref([])
const selectedCategoryId = ref(null)
const showCreateDialog = ref(false)
const showEditDialog = ref(false)
const newCategoryName = ref('')
const newCategoryDescription = ref('')
const editCategoryId = ref(null)
const editCategoryName = ref('')
const editCategoryDescription = ref('')
const creating = ref(false)
const updating = ref(false)
const uncategorizedCount = ref(0)

onMounted(() => {
  loadCategories()
  loadUncategorizedCount()
})

const loadCategories = async () => {
  try {
    const allCategories = await categoryApi.getAllCategories()
    // 为每个分类加载笔记数量
    const categoriesWithCount = await Promise.all(
      allCategories.map(async (cat) => {
        try {
          const countData = await categoryApi.getNoteCount(cat.id)
          return { ...cat, noteCount: countData.count || 0 }
        } catch (e) {
          console.warn(`获取分类 ${cat.name} 的笔记数量失败:`, e)
          return { ...cat, noteCount: 0 }
        }
      })
    )
    categories.value = categoriesWithCount
  } catch (error) {
    console.error('加载分类失败:', error)
    ElMessage.error(error.handledMessage || '加载分类失败')
  }
}

const loadUncategorizedCount = async () => {
  try {
    // 使用 'null' 字符串来获取未分类的笔记（后端会特殊处理）
    const notes = await noteApi.getNotesByCategory('null')
    uncategorizedCount.value = Array.isArray(notes) ? notes.length : 0
  } catch (error) {
    console.warn('加载未分类笔记数量失败:', error)
    uncategorizedCount.value = 0
  }
}

const handleSelectCategory = (categoryId) => {
  selectedCategoryId.value = categoryId
  emit('category-click', categoryId)
}

const handleCategoryCommand = async (category, command) => {
  if (command === 'edit') {
    editCategoryId.value = category.id
    editCategoryName.value = category.name
    editCategoryDescription.value = category.description || ''
    showEditDialog.value = true
  } else if (command === 'delete') {
    try {
      await ElMessageBox.confirm(
        `确定要删除分类 "${category.name}" 吗？此操作会将分类下的笔记移至"未分类"。`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await categoryApi.deleteCategory(category.id)
      ElMessage.success('分类删除成功')
      loadCategories()
      loadUncategorizedCount()
      if (selectedCategoryId.value === category.id) {
        selectedCategoryId.value = null
        emit('category-click', null)
      }
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error(error.handledMessage || '删除分类失败')
      }
    }
  }
}

const handleCreateCategory = async () => {
  if (!newCategoryName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  creating.value = true
  try {
    await categoryApi.createCategory(
      newCategoryName.value.trim(),
      newCategoryDescription.value.trim()
    )
    ElMessage.success('分类创建成功')
    showCreateDialog.value = false
    newCategoryName.value = ''
    newCategoryDescription.value = ''
    loadCategories()
    emit('refresh-categories')
  } catch (error) {
    console.error('创建分类失败:', error)
    ElMessage.error(error.handledMessage || '创建分类失败')
  } finally {
    creating.value = false
  }
}

const handleUpdateCategory = async () => {
  if (!editCategoryName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  updating.value = true
  try {
    await categoryApi.updateCategory(
      editCategoryId.value,
      editCategoryName.value.trim(),
      editCategoryDescription.value.trim()
    )
    ElMessage.success('分类更新成功')
    showEditDialog.value = false
    loadCategories()
    emit('refresh-categories')
  } catch (error) {
    console.error('更新分类失败:', error)
    ElMessage.error(error.handledMessage || '更新分类失败')
  } finally {
    updating.value = false
  }
}
</script>

<style scoped>
.category-manager {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
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

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.category-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.category-icon {
  font-size: 20px;
  color: #409eff;
}

.category-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.category-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.category-count {
  font-size: 12px;
  color: #909399;
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

.empty-categories {
  padding: 60px 20px;
  text-align: center;
}

/* 暗色模式适配 */
:deep(body.dark) .category-manager {
  background-color: #1f2933;
  color: #e5e7eb;
}

:deep(body.dark) .manager-header h3 {
  color: #e5e7eb;
}

:deep(body.dark) .category-item {
  background-color: #111827;
  border-color: #374151;
}

:deep(body.dark) .category-item:hover {
  border-color: #409eff;
}

:deep(body.dark) .category-item.active {
  background-color: #1e3a5f;
  border-color: #409eff;
}

:deep(body.dark) .category-name {
  color: #e5e7eb;
}

:deep(body.dark) .category-count {
  color: #9ca3af;
}

:deep(body.dark) .more-btn {
  color: #9ca3af;
}

:deep(body.dark) .more-btn:hover {
  background-color: #374151;
}
</style>