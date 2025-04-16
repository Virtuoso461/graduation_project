/**
 * 验证邮箱
 * @param {string} email - 邮箱地址
 * @returns {boolean} - 返回验证结果
 */
export function validateEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

/**
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean} - 返回验证结果
 */
export function validatePhone(phone) {
  const re = /^1[3-9]\d{9}$/;
  return re.test(phone);
}

/**
 * 验证密码强度
 * @param {string} password - 密码
 * @returns {Object} - 返回验证结果和强度
 */
export function validatePassword(password) {
  if (!password) {
    return {
      valid: false,
      strength: 0,
      message: '请输入密码'
    };
  }
  
  if (password.length < 6) {
    return {
      valid: false,
      strength: 1,
      message: '密码长度不能少于6个字符'
    };
  }
  
  let strength = 0;
  
  // 包含小写字母
  if (/[a-z]/.test(password)) {
    strength += 1;
  }
  
  // 包含大写字母
  if (/[A-Z]/.test(password)) {
    strength += 1;
  }
  
  // 包含数字
  if (/\d/.test(password)) {
    strength += 1;
  }
  
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) {
    strength += 1;
  }
  
  // 长度大于等于8
  if (password.length >= 8) {
    strength += 1;
  }
  
  let message = '';
  
  switch (strength) {
    case 0:
    case 1:
      message = '密码强度：弱';
      break;
    case 2:
    case 3:
      message = '密码强度：中';
      break;
    case 4:
    case 5:
      message = '密码强度：强';
      break;
    default:
      break;
  }
  
  return {
    valid: true,
    strength,
    message
  };
}

/**
 * 验证URL
 * @param {string} url - URL地址
 * @returns {boolean} - 返回验证结果
 */
export function validateUrl(url) {
  try {
    new URL(url);
    return true;
  } catch (e) {
    return false;
  }
}

/**
 * 验证身份证号
 * @param {string} idCard - 身份证号
 * @returns {boolean} - 返回验证结果
 */
export function validateIdCard(idCard) {
  const re = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  return re.test(idCard);
}

/**
 * 验证学号/工号
 * @param {string} studentId - 学号/工号
 * @returns {boolean} - 返回验证结果
 */
export function validateStudentId(studentId) {
  const re = /^\d{8,12}$/;
  return re.test(studentId);
}

/**
 * 验证表单
 * @param {Object} form - 表单数据
 * @param {Object} rules - 验证规则
 * @returns {Object} - 返回验证结果
 */
export function validateForm(form, rules) {
  const errors = {};
  let valid = true;
  
  Object.keys(rules).forEach(field => {
    const fieldRules = rules[field];
    
    for (const rule of fieldRules) {
      // 必填验证
      if (rule.required && !form[field]) {
        errors[field] = rule.message || '该字段为必填项';
        valid = false;
        break;
      }
      
      // 跳过空值的非必填验证
      if (!form[field] && !rule.required) {
        continue;
      }
      
      // 自定义验证函数
      if (rule.validator && typeof rule.validator === 'function') {
        const result = rule.validator(form[field], form);
        if (result !== true) {
          errors[field] = result || rule.message || '验证失败';
          valid = false;
          break;
        }
      }
      
      // 正则验证
      if (rule.pattern && !rule.pattern.test(form[field])) {
        errors[field] = rule.message || '格式不正确';
        valid = false;
        break;
      }
      
      // 最小长度验证
      if (rule.min !== undefined && form[field].length < rule.min) {
        errors[field] = rule.message || `长度不能少于${rule.min}个字符`;
        valid = false;
        break;
      }
      
      // 最大长度验证
      if (rule.max !== undefined && form[field].length > rule.max) {
        errors[field] = rule.message || `长度不能超过${rule.max}个字符`;
        valid = false;
        break;
      }
    }
  });
  
  return {
    valid,
    errors
  };
}
