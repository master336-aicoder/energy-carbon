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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigList, updateConfig, createConfig } from '@/api/config'

const activeTab = ref('basic')

const basicForm = ref({
  siteName: '',
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

const configMap = ref<Record<string, { id?: number; configValue?: string }>>({})

const basicKeyMap: Record<string, string> = {
  siteName: 'site_name',
  siteLogo: 'logo_url',
  language: 'default_language'
}

const securityKeyMap: Record<string, string> = {
  tokenExpire: 'token_expire',
  minPasswordLength: 'min_password_length',
  maxLoginAttempts: 'max_login_attempts'
}

const notificationKeyMap: Record<string, string> = {
  alarmEnabled: 'alarm_enabled',
  emailEnabled: 'email_enabled',
  emailAddress: 'email_address'
}

async function fetchConfigList() {
  try {
    const res: any = await getConfigList({ current: 1, size: 999 })
    const records = res?.data?.records || []
    const map: Record<string, { id?: number; configValue?: string }> = {}
    records.forEach((item: any) => {
      map[item.configKey] = { id: item.id, configValue: item.configValue }
    })
    configMap.value = map
    applyConfigToForms()
  } catch (error) {
    ElMessage.error('获取配置失败')
  }
}

function applyConfigToForms() {
  Object.entries(basicKeyMap).forEach(([formKey, configKey]) => {
    const cfg = configMap.value[configKey]
    if (cfg && cfg.configValue !== undefined) {
      (basicForm.value as any)[formKey] = cfg.configValue
    }
  })
  Object.entries(securityKeyMap).forEach(([formKey, configKey]) => {
    const cfg = configMap.value[configKey]
    if (cfg && cfg.configValue !== undefined) {
      let val: any = cfg.configValue
      if (!isNaN(Number(val))) val = Number(val)
      ;(securityForm.value as any)[formKey] = val
    }
  })
  Object.entries(notificationKeyMap).forEach(([formKey, configKey]) => {
    const cfg = configMap.value[configKey]
    if (cfg && cfg.configValue !== undefined) {
      let val: any = cfg.configValue
      if (val === 'true') val = true
      if (val === 'false') val = false
      ;(notificationForm.value as any)[formKey] = val
    }
  })
}

async function saveConfigs(formData: any, keyMap: Record<string, string>) {
  try {
    for (const [formKey, configKey] of Object.entries(keyMap)) {
      const value = formData[formKey]
      const cfg = configMap.value[configKey]
      const payload = { configKey, configValue: String(value) } as any
      if (cfg && cfg.id) {
        await updateConfig(cfg.id, payload)
      } else {
        await createConfig(payload)
      }
    }
    ElMessage.success('保存成功')
    fetchConfigList()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

function handleSaveBasic() {
  saveConfigs(basicForm.value, basicKeyMap)
}

function handleSaveSecurity() {
  saveConfigs(securityForm.value, securityKeyMap)
}

function handleSaveNotification() {
  saveConfigs(notificationForm.value, notificationKeyMap)
}

onMounted(fetchConfigList)
</script>

<style scoped lang="scss">
.settings-page {
  padding: 20px;
}
</style>
