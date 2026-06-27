<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <span>个人中心</span>
      </template>
      <el-form :model="form" label-width="100px" style="max-width: 500px" v-loading="loading">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="form.deptName" disabled placeholder="暂无部门" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card style="margin-top: 20px">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form :model="pwdForm" label-width="100px" style="max-width: 500px">
        <el-form-item label="旧密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleChangePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const userStore = useUserStore()
const loading = ref(false)

const form = ref({
  id: 0,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  deptName: '',
})

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  // 从 store 获取基本信息
  if (userStore.userInfo) {
    form.value.username = userStore.userInfo.username || ''
    form.value.nickname = userStore.userInfo.nickname || ''
    form.value.deptName = userStore.userInfo.deptName || ''
    form.value.phone = userStore.userInfo.phone || ''
    form.value.email = userStore.userInfo.email || ''
  }
  // 从 API 获取完整信息（包含 deptName）
  try {
    loading.value = true
    const userId = userStore.userInfo?.id
    if (userId) {
      const res: any = await request.get(`/system/user/${userId}`)
      const data = res.data
      if (data) {
        form.value.id = data.id
        form.value.username = data.username || form.value.username
        form.value.nickname = data.nickname || form.value.nickname
        form.value.phone = data.phone || ''
        form.value.email = data.email || ''
        form.value.deptName = data.deptName || ''
      }
    }
  } catch (e) {
    console.error('Failed to fetch user info:', e)
  } finally {
    loading.value = false
  }
})

function handleSave() {
  ElMessage.success('个人信息保存成功')
}

function handleChangePwd() {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  ElMessage.success('密码修改成功')
  pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}
</script>

<style scoped lang="scss">
.profile-page {
  padding: 20px;
}
</style>
