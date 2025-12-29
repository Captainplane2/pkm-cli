<template>
  <el-dialog
    :model-value="visible"
    title="用户登录"
    width="400px"
    :close-on-click-modal="false"
    destroy-on-close
    @update:model-value="handleVisibleChange"
    @close="handleClose"
  >
    <el-form
      ref="loginFormRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      status-icon
    >
      <el-form-item label="账号" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="请输入用户名或邮箱"
          prefix-icon="User"
          clearable
        />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          show-password
          @keyup.enter="handleSubmit"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button
        type="primary"
        :loading="authService.state.loading"
        @click="handleSubmit"
      >
        登录
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { AuthService } from '../services/AuthService'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'login-success'])

const visible = ref(false)
const loginFormRef = ref(null)
const authService = AuthService

const formData = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, max: 50, message: '账号长度在3-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleVisibleChange = (val) => {
  visible.value = val
}

const handleSubmit = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      const result = await AuthService.login(formData.username, formData.password)
      if (result.success) {
        ElMessage.success('登录成功')
        emit('login-success')
        handleClose()
      } else {
        ElMessage.error(result.message)
      }
    }
  })
}

const handleClose = () => {
  visible.value = false
  formData.username = ''
  formData.password = ''
  if (loginFormRef.value) {
    loginFormRef.value.resetFields()
  }
}
</script>
