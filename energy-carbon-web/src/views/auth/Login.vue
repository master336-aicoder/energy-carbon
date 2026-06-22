<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>世通AI+能碳管理平台</h2>
        <p>企业绿色低碳工作的技术底座</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input
              v-model="loginForm.captcha"
              placeholder="请输入验证码"
              :prefix-icon="CircleCheck"
              size="large"
              class="captcha-input"
            />
            <div class="captcha-img" @click="fetchCaptcha">
              <img v-if="captchaImg" :src="captchaImg" alt="验证码" />
              <span v-else class="captcha-placeholder">点击获取</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loading = ref(false)
const captchaImg = ref('')
const captchaKey = ref('')

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  captcha: '',
  captchaKey: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function fetchCaptcha() {
  try {
    const res = await getCaptcha()
    if (res.data) {
      captchaImg.value = res.data.imageBase64
      captchaKey.value = res.data.key
      loginForm.captchaKey = res.data.key
    }
  } catch (error) {
    ElMessage.error('获取验证码失败')
  }
}

async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)
    await userStore.fetchUserInfo()
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    fetchCaptcha()
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchCaptcha()
})
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    color: #1a1a2e;
    font-size: 24px;
    margin-bottom: 8px;
  }

  p {
    color: #6b7280;
    font-size: 14px;
  }
}

.login-form {
  .login-btn {
    width: 100%;
  }
}

.captcha-row {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;

  .captcha-input {
    flex: 1;
  }

  .captcha-img {
    width: 120px;
    height: 40px;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    cursor: pointer;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .captcha-placeholder {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
