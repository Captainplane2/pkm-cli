<template>
  <el-dialog
    v-model="dialogVisible"
    title="用户注册"
    width="400px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="registerFormRef"
      :model="formData"
      :rules="rules"
      label-width="80px"
      status-icon
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="formData.username"
          placeholder="请输入用户名（3-20个字符）"
          prefix-icon="User"
          clearable
        />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input
          v-model="formData.email"
          placeholder="请输入邮箱"
          prefix-icon="Message"
          clearable
        />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input
          v-model="formData.password"
          type="password"
          placeholder="请输入密码（6-20个字符）"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="formData.confirmPassword"
          type="password"
          placeholder="请再次输入密码"
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
        注册
      </el-button>
    </template>
    <div style="text-align: center; margin-top: -20px; margin-bottom: 10px;">
      <el-link type="primary" @click="switchToLogin">已有账号？去登录</el-link>
    </div>
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

const emit = defineEmits(['update:modelValue', 'switch-to-login'])

const dialogVisible = ref(false)
const registerFormRef = ref(null)
const authService = AuthService

const formData = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== formData.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

const switchToLogin = () => {
  dialogVisible.value = false
  emit('switch-to-login')
}

const handleSubmit = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      const result = await AuthService.register(
        formData.username,
        formData.email,
        formData.password
      )
      if (result.success) {
        ElMessage.success(result.message)
        emit('switch-to-login')
        handleClose()
      } else {
        ElMessage.error(result.message)
      }
    }
  })
}

const handleClose = () => {
  dialogVisible.value = false
  formData.username = ''
  formData.email = ''
  formData.password = ''
  formData.confirmPassword = ''
  if (registerFormRef.value) {
    registerFormRef.value.resetFields()
  }
}
</script>
