<template>
  <div class="settings-page">
    <el-card>
      <template #header>
        <span>系统设置</span>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicForm" label-width="120px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="系统名称">
              <el-input v-model="basicForm.siteName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-input v-model="basicForm.siteLogo" placeholder="Logo URL" />
            </el-form-item>
            <el-form-item label="默认语言">
              <el-select v-model="basicForm.language" style="width: 200px">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasic">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="安全设置" name="security">
          <el-form :model="securityForm" label-width="120px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="Token过期时间">
              <el-input-number v-model="securityForm.tokenExpire" :min="30" :max="86400" style="width: 200px" />
              <span style="margin-left: 10px">秒</span>
            </el-form-item>
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securityForm.minPasswordLength" :min="6" :max="20" style="width: 200px" />
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-input-number v-model="securityForm.maxLoginAttempts" :min="3" :max="10" style="width: 200px" />
              <span style="margin-left: 10px">次</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveSecurity">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="通知设置" name="notification">
          <el-form label-width="120px" style="max-width: 600px; margin-top: 20px">
            <el-form-item label="告警通知">
              <el-switch v-model="notificationForm.alarmEnabled" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationForm.emailEnabled" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="通知邮箱">
              <el-input v-model="notificationForm.emailAddress" placeholder="请输入通知邮箱" style="width: 300px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveNotification">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')

const basicForm = ref({
  siteName: '能碳数智化平台',
  siteLogo: '',
  language: 'zh-CN'
})

const securityForm = ref({
  tokenExpire: 7200,
  minPasswordLength: 6,
  maxLoginAttempts: 5
})

const notificationForm = ref({
  alarmEnabled: true,
  emailEnabled: false,
  emailAddress: ''
})

function handleSaveBasic() {
  ElMessage.success('基本设置保存成功')
}

function handleSaveSecurity() {
  ElMessage.success('安全设置保存成功')
}

function handleSaveNotification() {
  ElMessage.success('通知设置保存成功')
}
</script>

<style scoped lang="scss">
.settings-page {
  padding: 20px;
}
</style>
