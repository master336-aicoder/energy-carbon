/**
 * 是否为空
 * @param value 值
 * @returns 是否为空
 */
export function isEmpty(value: unknown): boolean {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

/**
 * 是否为邮箱
 * @param value 值
 * @returns 是否为邮箱
 */
export function isEmail(value: string): boolean {
  const pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
  return pattern.test(value)
}

/**
 * 是否为手机号（中国大陆）
 * @param value 值
 * @returns 是否为手机号
 */
export function isPhone(value: string): boolean {
  const pattern = /^1[3-9]\d{9}$/
  return pattern.test(value)
}

/**
 * 是否为固定电话
 * @param value 值
 * @returns 是否为固定电话
 */
export function isTel(value: string): boolean {
  const pattern = /^(0\d{2,3}-?)?\d{7,8}$/
  return pattern.test(value)
}

/**
 * 是否为 URL
 * @param value 值
 * @returns 是否为 URL
 */
export function isUrl(value: string): boolean {
  const pattern = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+)(:[0-9]+)?(\/.*)?$/
  return pattern.test(value)
}

/**
 * 是否为 IPv4
 * @param value 值
 * @returns 是否为 IPv4
 */
export function isIPv4(value: string): boolean {
  const pattern = /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/
  return pattern.test(value)
}

/**
 * 是否为数字
 * @param value 值
 * @returns 是否为数字
 */
export function isNumber(value: unknown): value is number {
  return typeof value === 'number' && !isNaN(value)
}

/**
 * 是否为整数
 * @param value 值
 * @returns 是否为整数
 */
export function isInteger(value: unknown): boolean {
  return isNumber(value) && Number.isInteger(value)
}

/**
 * 是否为正整数
 * @param value 值
 * @returns 是否为正整数
 */
export function isPositiveInteger(value: unknown): boolean {
  return isInteger(value) && value > 0
}

/**
 * 是否在某个范围内
 * @param value 值
 * @param min 最小值
 * @param max 最大值
 * @returns 是否在范围内
 */
export function isInRange(value: number, min: number, max: number): boolean {
  return value >= min && value <= max
}

/**
 * 是否为身份证号（中国大陆）
 * @param value 值
 * @returns 是否为身份证号
 */
export function isIdCard(value: string): boolean {
  const pattern = /^[1-9]\d{5}(?:18|19|20)\d{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/
  return pattern.test(value)
}

/**
 * 是否为日期格式（YYYY-MM-DD）
 * @param value 值
 * @returns 是否为日期格式
 */
export function isDate(value: string): boolean {
  const pattern = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/
  if (!pattern.test(value)) return false
  const date = new Date(value)
  return date instanceof Date && !isNaN(date.getTime())
}

/**
 * 是否为密码（至少包含字母和数字，长度6-20）
 * @param value 值
 * @returns 是否符合密码规则
 */
export function isPassword(value: string): boolean {
  const pattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{6,20}$/
  return pattern.test(value)
}

/**
 * 是否为强密码（包含大小写字母、数字、特殊字符，长度8-20）
 * @param value 值
 * @returns 是否为强密码
 */
export function isStrongPassword(value: string): boolean {
  const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[a-zA-Z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]{8,20}$/
  return pattern.test(value)
}

/**
 * 是否为中文
 * @param value 值
 * @returns 是否为中文
 */
export function isChinese(value: string): boolean {
  const pattern = /^[\u4e00-\u9fa5]+$/
  return pattern.test(value)
}

/**
 * 是否为英文
 * @param value 值
 * @returns 是否为英文
 */
export function isEnglish(value: string): boolean {
  const pattern = /^[a-zA-Z]+$/
  return pattern.test(value)
}

/**
 * 是否为合法用户名（字母开头，允许字母数字下划线，长度4-20）
 * @param value 值
 * @returns 是否为合法用户名
 */
export function isUsername(value: string): boolean {
  const pattern = /^[a-zA-Z][a-zA-Z0-9_]{3,19}$/
  return pattern.test(value)
}

/**
 * 是否为合法编码（字母数字下划线横线，长度2-50）
 * @param value 值
 * @returns 是否为合法编码
 */
export function isCode(value: string): boolean {
  const pattern = /^[a-zA-Z0-9_-]{2,50}$/
  return pattern.test(value)
}

/**
 * 表单验证规则生成器
 */
export const rules = {
  /** 必填 */
  required(message = '此项为必填项') {
    return { required: true, message, trigger: 'blur' }
  },

  /** 必填（选择器） */
  requiredSelect(message = '请选择') {
    return { required: true, message, trigger: 'change' }
  },

  /** 邮箱 */
  email(message = '请输入正确的邮箱地址') {
    return { type: 'email', message, trigger: 'blur' }
  },

  /** 手机号 */
  phone(message = '请输入正确的手机号') {
    return {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value || isPhone(value)) {
          callback()
        } else {
          callback(new Error(message))
        }
      },
      trigger: 'blur'
    }
  },

  /** 密码 */
  password(message = '密码需包含字母和数字，长度6-20位') {
    return {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value || isPassword(value)) {
          callback()
        } else {
          callback(new Error(message))
        }
      },
      trigger: 'blur'
    }
  },

  /** 强密码 */
  strongPassword(message = '密码需包含大小写字母、数字和特殊字符，长度8-20位') {
    return {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value || isStrongPassword(value)) {
          callback()
        } else {
          callback(new Error(message))
        }
      },
      trigger: 'blur'
    }
  },

  /** 用户名 */
  username(message = '用户名需以字母开头，允许字母数字下划线，长度4-20位') {
    return {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value || isUsername(value)) {
          callback()
        } else {
          callback(new Error(message))
        }
      },
      trigger: 'blur'
    }
  },

  /** 编码 */
  code(message = '编码只能包含字母、数字、下划线和横线，长度2-50位') {
    return {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (!value || isCode(value)) {
          callback()
        } else {
          callback(new Error(message))
        }
      },
      trigger: 'blur'
    }
  },

  /** 长度范围 */
  length(min: number, max: number, message?: string) {
    return {
      min,
      max,
      message: message || `长度需在 ${min} 到 ${max} 个字符之间`,
      trigger: 'blur'
    }
  },

  /** 数值范围 */
  numberRange(min: number, max: number, message?: string) {
    return {
      validator: (_rule: unknown, value: number, callback: (error?: Error) => void) => {
        if (value === undefined || value === null || isInRange(value, min, max)) {
          callback()
        } else {
          callback(new Error(message || `数值需在 ${min} 到 ${max} 之间`))
        }
      },
      trigger: 'blur'
    }
  }
}
