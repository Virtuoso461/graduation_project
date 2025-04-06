/**
 * IndexedDB工具类 - 用于存储和检索用户信息
 */

// 用户信息接口
export interface UserInfo {
  id: string;
  username: string; // 在系统中是邮箱
  phoneNumber?: string;
  number?: string; // 学号
  role: string;
  [key: string]: any; // 允许其他属性
}

// 数据库配置
const DB_NAME = 'learningPlatformDB';
const DB_VERSION = 1;
const USER_STORE = 'userInfo';

/**
 * 打开数据库连接
 * @returns Promise<IDBDatabase>
 */
const openDB = (): Promise<IDBDatabase> => {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onerror = (event) => {
      console.error('打开数据库失败:', event);
      reject(new Error('无法打开数据库'));
    };

    request.onsuccess = (event) => {
      const db = (event.target as IDBOpenDBRequest).result;
      resolve(db);
    };

    request.onupgradeneeded = (event) => {
      const db = (event.target as IDBOpenDBRequest).result;
      // 如果对象仓库不存在，则创建
      if (!db.objectStoreNames.contains(USER_STORE)) {
        db.createObjectStore(USER_STORE, { keyPath: 'id' });
      }
    };
  });
};

/**
 * 保存用户信息
 * @param userInfo 用户信息对象
 * @returns Promise<void>
 */
export const saveUserInfo = async (userInfo: UserInfo): Promise<void> => {
  try {
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readwrite');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.put(userInfo);

      request.onsuccess = () => {
        console.log('用户信息保存成功');
        resolve();
      };

      request.onerror = (event) => {
        console.error('保存用户信息失败:', event);
        reject(new Error('保存用户信息失败'));
      };

      transaction.oncomplete = () => {
        db.close();
      };
    });
  } catch (error) {
    console.error('保存用户信息时发生错误:', error);
    throw error;
  }
};

/**
 * 获取用户信息
 * @param userId 用户ID
 * @returns Promise<UserInfo | null>
 */
export const getUserInfo = async (userId: string): Promise<UserInfo | null> => {
  try {
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readonly');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.get(userId);

      request.onsuccess = () => {
        const result = request.result as UserInfo;
        resolve(result || null);
      };

      request.onerror = (event) => {
        console.error('获取用户信息失败:', event);
        reject(new Error('获取用户信息失败'));
      };

      transaction.oncomplete = () => {
        db.close();
      };
    });
  } catch (error) {
    console.error('获取用户信息时发生错误:', error);
    return null;
  }
};

/**
 * 获取当前登录用户信息
 * @returns Promise<UserInfo | null>
 */
export const getCurrentUserInfo = async (): Promise<UserInfo | null> => {
  try {
    // 从localStorage获取当前用户ID
    const currentUserId = localStorage.getItem('currentUserId');
    if (!currentUserId) {
      return null;
    }
    
    return await getUserInfo(currentUserId);
  } catch (error) {
    console.error('获取当前用户信息失败:', error);
    return null;
  }
};

/**
 * 清除用户信息
 * @param userId 用户ID
 * @returns Promise<void>
 */
export const clearUserInfo = async (userId: string): Promise<void> => {
  try {
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readwrite');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.delete(userId);

      request.onsuccess = () => {
        console.log('用户信息清除成功');
        resolve();
      };

      request.onerror = (event) => {
        console.error('清除用户信息失败:', event);
        reject(new Error('清除用户信息失败'));
      };

      transaction.oncomplete = () => {
        db.close();
      };
    });
  } catch (error) {
    console.error('清除用户信息时发生错误:', error);
    throw error;
  }
};

/**
 * 清除当前用户信息并从localStorage移除记录
 * @returns Promise<void>
 */
export const clearCurrentUserInfo = async (): Promise<void> => {
  try {
    const currentUserId = localStorage.getItem('currentUserId');
    if (currentUserId) {
      await clearUserInfo(currentUserId);
      localStorage.removeItem('currentUserId');
    }
  } catch (error) {
    console.error('清除当前用户信息失败:', error);
    throw error;
  }
}; 