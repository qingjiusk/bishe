<template>
  <div class="layout">
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <el-icon size="32"><Shop /></el-icon>
            <span>{{ t('common.appName') }}</span>
          </div>
          <div class="nav">
            <el-input
              v-model="searchKeyword"
              :placeholder="t('home.searchPlaceholder')"
              class="search-input"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button :icon="Search" @click="handleSearch" />
              </template>
            </el-input>
          </div>
          <div class="language-switcher">
            <LanguageSwitcher />
          </div>
          <div class="user-menu">
            <template v-if="userStore.token">
              <el-dropdown>
                <span class="user-info">
                  <el-avatar :size="32" :src="userStore.user?.avatar || ''">
                    {{ userStore.user?.nickname?.charAt(0) }}
                  </el-avatar>
                  <span>{{ userStore.user?.nickname }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/cart')">
                      <el-icon><ShoppingCart /></el-icon>
                      {{ t('common.cart') }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/orders')">
                      <el-icon><Document /></el-icon>
                      {{ t('common.orders') }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/favorite')">
                      <el-icon><Star /></el-icon>
                      {{ t('common.favorite') }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/publish')">
                      <el-icon><Plus /></el-icon>
                      {{ t('common.publish') }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/profile')">
                      <el-icon><User /></el-icon>
                      {{ t('common.profile') }}
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="handleLogout">
                      <el-icon><SwitchButton /></el-icon>
                      {{ t('common.logout') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button @click="$router.push('/login')">{{ t('common.login') }}</el-button>
              <el-button type="primary" @click="$router.push('/register')">{{ t('common.register') }}</el-button>
            </template>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu
            :default-active="$route.path"
            router
            background-color="#f5f5f5"
          >
            <el-menu-item index="/category/1">{{ t('category.electronics') }}</el-menu-item>
            <el-menu-item index="/category/2">{{ t('category.books') }}</el-menu-item>
            <el-menu-item index="/category/3">{{ t('category.daily') }}</el-menu-item>
            <el-menu-item index="/category/4">{{ t('category.sports') }}</el-menu-item>
            <el-menu-item index="/category/5">{{ t('category.clothing') }}</el-menu-item>
            <el-menu-item index="/category/6">{{ t('category.other') }}</el-menu-item>
          </el-menu>
        </el-aside>
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { Shop, Search, ShoppingCart, Document, Star, Plus, User, SwitchButton } from '@element-plus/icons-vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const router = useRouter()
const { t } = useI18n()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchKeyword.value } })
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  color: #409eff;
}

.search-input {
  width: 400px;
}

.language-switcher {
  display: flex;
  align-items: center;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.aside {
  background-color: #f5f5f5;
  padding: 20px 0;
}

.main {
  max-width: 1000px;
  margin: 20px auto;
  background-color: #fff;
  min-height: calc(100vh - 100px);
  padding: 20px;
  border-radius: 8px;
}
</style>