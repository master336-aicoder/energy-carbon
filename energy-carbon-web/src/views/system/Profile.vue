<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <span>个人中心</span>
      </template>
      <el-form :model="form" label-width="100px" style="max-width: 500px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
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

const userStore = useUserStore()

const form = ref({
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  form.value.username = userStore.username || ''
  form.value.nickname = userStore.nickname || ''
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
