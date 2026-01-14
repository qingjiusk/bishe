<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>{{ t('auth.register') }}</h2>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            :placeholder="t('auth.username')"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            :placeholder="t('auth.nickname')"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            :placeholder="t('auth.password')"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            :placeholder="t('auth.confirmPassword')"
            size="large"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            :placeholder="t('auth.phone')"
            size="large"
            :prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="college">
          <el-input
            v-model="registerForm.college"
            :placeholder="t('auth.college')"
            size="large"
            :prefix-icon="School"
          />
        </el-form-item>
        <el-form-item prop="major">
          <el-input
            v-model="registerForm.major"
            :placeholder="t('auth.major')"
            size="large"
            :prefix-icon="Reading"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleRegister" :loading="loading" style="width: 100%">
            {{ t('auth.register') }}
          </el-button>
        </el-form-item>
        <div class="footer">
          <span>{{ t('auth.hasAccount') }}</span>
          <el-link type="primary" @click="$router.push('/login')">{{ t('auth.goToLogin') }}</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, School, Reading } from '@element-plus/icons-vue'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  phone: '',
  college: '',
  major: ''
})

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error(t('auth.passwordPlaceholder')))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error(t('auth.confirmPasswordPlaceholder')))
  } else if (value !== registerForm.password) {
    callback(new Error(t('auth.passwordMismatch')))
  } else {
    callback()
  }
}

const rules = computed(() => ({
  username: [{ required: true, message: t('auth.usernamePlaceholder'), trigger: 'blur' }],
  nickname: [{ required: true, message: t('auth.nicknamePlaceholder'), trigger: 'blur' }],
  password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
  phone: [
    { required: true, message: t('auth.phonePlaceholder'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  college: [{ required: true, message: t('auth.collegePlaceholder'), trigger: 'blur' }],
  major: [{ required: true, message: t('auth.majorPlaceholder'), trigger: 'blur' }]
}))

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    const { confirmPassword, ...userData } = registerForm
    const success = await userStore.register(userData)
    if (success) {
      ElMessage.success(t('auth.registerSuccess'))
      router.push('/')
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 0;
}

.register-card {
  width: 400px;
}

.register-card h2 {
  text-align: center;
  margin: 0;
  color: #333;
}

.footer {
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>