<template>
  <div class="profile">
    <h2>{{ t('profile.title') }}</h2>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <div class="user-info">
            <el-avatar :size="100" :src="userStore.user?.avatar || ''">
              {{ userStore.user?.nickname?.charAt(0) }}
            </el-avatar>
            <h3>{{ userStore.user?.nickname }}</h3>
            <p>{{ userStore.user?.college }} - {{ userStore.user?.major }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane :label="t('profile.userInfo')" name="info">
              <el-form :model="userForm" label-width="100px">
                <el-form-item :label="t('auth.username')">
                  <el-input v-model="userStore.user.username" disabled />
                </el-form-item>
                <el-form-item :label="t('auth.nickname')">
                  <el-input v-model="userForm.nickname" />
                </el-form-item>
                <el-form-item :label="t('auth.phone')">
                  <el-input v-model="userForm.phone" />
                </el-form-item>
                <el-form-item :label="t('auth.email')">
                  <el-input v-model="userForm.email" />
                </el-form-item>
                <el-form-item :label="t('auth.college')">
                  <el-input v-model="userForm.college" />
                </el-form-item>
                <el-form-item :label="t('auth.major')">
                  <el-input v-model="userForm.major" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateInfo">{{ t('common.language') }}</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane :label="t('profile.changePassword')" name="password">
              <el-form :model="passwordForm" label-width="100px">
                <el-form-item :label="t('profile.oldPassword')">
                  <el-input v-model="passwordForm.oldPassword" type="password" />
                </el-form-item>
                <el-form-item :label="t('profile.newPassword')">
                  <el-input v-model="passwordForm.newPassword" type="password" />
                </el-form-item>
                <el-form-item :label="t('profile.confirmPassword')">
                  <el-input v-model="passwordForm.confirmPassword" type="password" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updatePassword">{{ t('profile.changePassword') }}</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { updateUserInfo, updatePassword as updatePasswordApi } from '@/api/user'

const { t } = useI18n()
const userStore = useUserStore()
const activeTab = ref('info')

const userForm = reactive({
  nickname: userStore.user?.nickname || '',
  phone: userStore.user?.phone || '',
  email: userStore.user?.email || '',
  college: userStore.user?.college || '',
  major: userStore.user?.major || ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const updateInfo = async () => {
  try {
    await updateUserInfo(userForm)
    await userStore.getUserInfo()
    ElMessage.success(t('profile.updateSuccess'))
  } catch (error) {
    console.error('更新失败:', error)
  }
}

const updatePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error(t('profile.passwordMismatch'))
    return
  }
  try {
    await updatePasswordApi(passwordForm.oldPassword, passwordForm.newPassword)
    ElMessage.success(t('profile.passwordChangedSuccess'))
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}
</script>

<style scoped>
.profile h2 {
  margin-bottom: 20px;
}

.user-info {
  text-align: center;
  padding: 20px;
}

.user-info h3 {
  margin: 15px 0 5px 0;
}

.user-info p {
  color: #666;
  margin: 0;
}
</style>