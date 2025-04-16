/**
 * 格式化日期
 * @param {Date|string|number} date - 日期对象、日期字符串或时间戳
 * @param {string} format - 格式化模板，默认为 'YYYY-MM-DD'
 * @returns {string} - 返回格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  
  const d = new Date(date);
  
  if (isNaN(d.getTime())) {
    console.error('Invalid date:', date);
    return '';
  }
  
  const year = d.getFullYear();
  const month = d.getMonth() + 1;
  const day = d.getDate();
  const hours = d.getHours();
  const minutes = d.getMinutes();
  const seconds = d.getSeconds();
  
  const padZero = (num) => (num < 10 ? `0${num}` : num);
  
  return format
    .replace(/YYYY/g, year)
    .replace(/YY/g, String(year).slice(2))
    .replace(/MM/g, padZero(month))
    .replace(/M/g, month)
    .replace(/DD/g, padZero(day))
    .replace(/D/g, day)
    .replace(/HH/g, padZero(hours))
    .replace(/H/g, hours)
    .replace(/hh/g, padZero(hours % 12 || 12))
    .replace(/h/g, hours % 12 || 12)
    .replace(/mm/g, padZero(minutes))
    .replace(/m/g, minutes)
    .replace(/ss/g, padZero(seconds))
    .replace(/s/g, seconds);
}

/**
 * 格式化相对时间
 * @param {Date|string|number} date - 日期对象、日期字符串或时间戳
 * @returns {string} - 返回相对时间字符串
 */
export function formatRelativeTime(date) {
  if (!date) return '';
  
  const d = new Date(date);
  
  if (isNaN(d.getTime())) {
    console.error('Invalid date:', date);
    return '';
  }
  
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  
  // 小于1分钟
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  
  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000));
    return `${minutes}分钟前`;
  }
  
  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000));
    return `${hours}小时前`;
  }
  
  // 小于1周
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000));
    return `${days}天前`;
  }
  
  // 小于1个月
  if (diff < 30 * 24 * 60 * 60 * 1000) {
    const weeks = Math.floor(diff / (7 * 24 * 60 * 60 * 1000));
    return `${weeks}周前`;
  }
  
  // 小于1年
  if (diff < 12 * 30 * 24 * 60 * 60 * 1000) {
    const months = Math.floor(diff / (30 * 24 * 60 * 60 * 1000));
    return `${months}个月前`;
  }
  
  // 大于等于1年
  const years = Math.floor(diff / (12 * 30 * 24 * 60 * 60 * 1000));
  return `${years}年前`;
}

/**
 * 格式化持续时间
 * @param {number} minutes - 分钟数
 * @returns {string} - 返回格式化后的持续时间字符串
 */
export function formatDuration(minutes) {
  if (minutes === undefined || minutes === null) return '';
  
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  
  if (hours > 0) {
    return `${hours}小时${mins > 0 ? ` ${mins}分钟` : ''}`;
  }
  
  return `${mins}分钟`;
}

/**
 * 获取日期范围
 * @param {string} period - 时间段（today/week/month/year）
 * @returns {Object} - 返回开始日期和结束日期
 */
export function getDateRange(period) {
  const now = new Date();
  const start = new Date(now);
  const end = new Date(now);
  
  switch (period) {
    case 'today':
      start.setHours(0, 0, 0, 0);
      end.setHours(23, 59, 59, 999);
      break;
    case 'week':
      // 获取本周一
      const day = start.getDay() || 7;
      start.setDate(start.getDate() - day + 1);
      start.setHours(0, 0, 0, 0);
      // 获取本周日
      end.setDate(end.getDate() + (7 - day));
      end.setHours(23, 59, 59, 999);
      break;
    case 'month':
      start.setDate(1);
      start.setHours(0, 0, 0, 0);
      end.setMonth(end.getMonth() + 1);
      end.setDate(0);
      end.setHours(23, 59, 59, 999);
      break;
    case 'year':
      start.setMonth(0, 1);
      start.setHours(0, 0, 0, 0);
      end.setMonth(11, 31);
      end.setHours(23, 59, 59, 999);
      break;
    default:
      break;
  }
  
  return {
    start,
    end
  };
}
