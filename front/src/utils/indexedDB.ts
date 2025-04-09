/**
 * IndexedDB工具类 - 用于存储和检索用户信息
 */

// 用户信息接口
export interface UserInfo {
  email: string; // 使用email作为主键
  username?: string;
  name?: string;
  phoneNumber?: string;
  gender?: string;
  bio?: string;
  birthday?: string;
  avatar?: string;
  [key: string]: any; // 允许其他属性
}

// 数据库配置
const DB_NAME = 'learningPlatformDB';
const DB_VERSION = 3; // 再次增加版本号，修改主键为email
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
      const oldVersion = event.oldVersion;

      console.log(`数据库升级，旧版本: ${oldVersion}, 新版本: ${DB_VERSION}`);

      // 如果是新建数据库
      if (oldVersion < 1) {
        // 如果对象仓库不存在，则创建
        if (!db.objectStoreNames.contains(USER_STORE)) {
          console.log('创建用户信息存储库，使用email作为主键');
          db.createObjectStore(USER_STORE, { keyPath: 'email' });
        }
      }

      // 如果从版本2升级到版本3，需要修改主键
      if (oldVersion === 2 && DB_VERSION >= 3) {
        console.log('从版本2升级到版本3，修改主键为email');

        // 如果存在旧的存储库，删除它
        if (db.objectStoreNames.contains(USER_STORE)) {
          db.deleteObjectStore(USER_STORE);
        }

        // 创建新的存储库，使用email作为主键
        db.createObjectStore(USER_STORE, { keyPath: 'email' });
      }

      // 从版本1升级到版本2，进行数据迁移
      if (oldVersion === 1 && DB_VERSION >= 2) {
        console.log('开始数据迁移，合并数字ID和邮箱ID的用户数据');

        // 获取用户信息存储库
        const store = event.target.transaction.objectStore(USER_STORE);

        // 获取所有数据
        const getAllRequest = store.getAll();

        getAllRequest.onsuccess = () => {
          const allData = getAllRequest.result;
          console.log('获取到所有用户数据:', allData);

          // 如果有数据
          if (allData && allData.length > 0) {
            // 分组数据：数字ID和字符串ID
            const numericIdData = allData.filter(item => typeof item.id === 'number');
            const stringIdData = allData.filter(item => typeof item.id === 'string');

            console.log('数字ID数据:', numericIdData);
            console.log('字符串ID数据:', stringIdData);

            // 处理数字ID的数据，将其转换为字符串ID
            numericIdData.forEach(item => {
              // 如果有邮箱字段或用户名字段，使用它们作为新ID
              if (item.email) {
                const newId = item.email;

                // 查找是否已存在相同ID的字符串数据
                const existingData = stringIdData.find(d => d.id === newId);

                if (existingData) {
                  // 如果存在，合并数据
                  console.log(`合并数据，数字ID: ${item.id}, 字符串ID: ${newId}`);

                  // 删除原数字ID数据
                  store.delete(item.id);
                } else {
                  // 如果不存在，创建新数据
                  console.log(`创建新数据，将数字ID: ${item.id} 转换为字符串ID: ${newId}`);

                  // 创建新对象，使用邮箱作为ID
                  const newData = { ...item, id: newId };

                  // 存储新数据
                  store.put(newData);

                  // 删除原数字ID数据
                  store.delete(item.id);
                }
              } else if (item.username && item.username.includes('@')) {
                // 如果没有email字段，但username是邮箱格式
                const newId = item.username;

                // 查找是否已存在相同ID的字符串数据
                const existingData = stringIdData.find(d => d.id === newId);

                if (existingData) {
                  // 如果存在，合并数据
                  console.log(`合并数据，数字ID: ${item.id}, 字符串ID: ${newId}`);

                  // 删除原数字ID数据
                  store.delete(item.id);
                } else {
                  // 如果不存在，创建新数据
                  console.log(`创建新数据，将数字ID: ${item.id} 转换为字符串ID: ${newId}`);

                  // 创建新对象，使用邮箱作为ID
                  const newData = { ...item, id: newId };

                  // 存储新数据
                  store.put(newData);

                  // 删除原数字ID数据
                  store.delete(item.id);
                }
              }
            });
          }
        };

        getAllRequest.onerror = (error) => {
          console.error('获取数据失败:', error);
        };
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
    // 确保有email字段作为主键
    if (!userInfo.email) {
      throw new Error('保存用户信息失败：缺少email字段（主键）');
    }

    // 如果还有id字段，删除它，因为我们现在使用email作为主键
    const finalUserInfo = { ...userInfo };
    if ('id' in finalUserInfo) {
      delete finalUserInfo.id;
    }

    console.log('保存到IndexedDB的数据（使用email作为主键）:', finalUserInfo);

    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readwrite');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.put(finalUserInfo);

      request.onsuccess = () => {
        console.log('用户信息保存成功，email:', finalUserInfo.email);
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
 * @param email 用户邮箱（主键）
 * @returns Promise<UserInfo | null>
 */
export const getUserInfo = async (email: string): Promise<UserInfo | null> => {
  try {
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readonly');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.get(email);

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
 * @param email 用户邮箱（主键）
 * @returns Promise<void>
 */
export const clearUserInfo = async (email: string): Promise<void> => {
  try {
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readwrite');
    const store = transaction.objectStore(USER_STORE);

    return new Promise((resolve, reject) => {
      const request = store.delete(email);

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

/**
 * 清理数据库中的重复数据
 * 将数字ID的数据转换为字符串ID（邮箱）
 * @returns Promise<void>
 */
export const cleanupDuplicateData = async (): Promise<void> => {
  try {
    console.log('开始清理重复数据');
    const db = await openDB();
    const transaction = db.transaction(USER_STORE, 'readwrite');
    const store = transaction.objectStore(USER_STORE);

    // 获取所有数据
    const getAllRequest = store.getAll();

    return new Promise((resolve, reject) => {
      getAllRequest.onsuccess = async () => {
        const allData = getAllRequest.result;
        console.log('获取到所有用户数据:', allData);

        // 如果有数据
        if (allData && allData.length > 0) {
          // 分组数据：数字ID和字符串ID
          const numericIdData = allData.filter(item => typeof item.id === 'number');
          const stringIdData = allData.filter(item => typeof item.id === 'string');

          console.log('数字ID数据:', numericIdData);
          console.log('字符串ID数据:', stringIdData);

          // 处理数字ID的数据
          for (const item of numericIdData) {
            // 如果有邮箱字段或用户名字段，使用它们作为新ID
            if (item.email) {
              const newId = item.email;

              // 查找是否已存在相同ID的字符串数据
              const existingData = stringIdData.find(d => d.id === newId);

              if (existingData) {
                // 如果存在，合并数据
                console.log(`合并数据，数字ID: ${item.id}, 字符串ID: ${newId}`);

                // 删除原数字ID数据
                await new Promise<void>((delResolve) => {
                  const delRequest = store.delete(item.id);
                  delRequest.onsuccess = () => delResolve();
                  delRequest.onerror = (e) => {
                    console.error('删除数据失败:', e);
                    delResolve();
                  };
                });
              } else {
                // 如果不存在，创建新数据
                console.log(`创建新数据，将数字ID: ${item.id} 转换为字符串ID: ${newId}`);

                // 创建新对象，使用邮箱作为ID
                const newData = { ...item, id: newId };

                // 存储新数据
                await new Promise<void>((putResolve) => {
                  const putRequest = store.put(newData);
                  putRequest.onsuccess = () => putResolve();
                  putRequest.onerror = (e) => {
                    console.error('保存数据失败:', e);
                    putResolve();
                  };
                });

                // 删除原数字ID数据
                await new Promise<void>((delResolve) => {
                  const delRequest = store.delete(item.id);
                  delRequest.onsuccess = () => delResolve();
                  delRequest.onerror = (e) => {
                    console.error('删除数据失败:', e);
                    delResolve();
                  };
                });
              }
            } else if (item.username && item.username.includes('@')) {
              // 如果没有email字段，但username是邮箱格式
              const newId = item.username;

              // 查找是否已存在相同ID的字符串数据
              const existingData = stringIdData.find(d => d.id === newId);

              if (existingData) {
                // 如果存在，合并数据
                console.log(`合并数据，数字ID: ${item.id}, 字符串ID: ${newId}`);

                // 删除原数字ID数据
                await new Promise<void>((delResolve) => {
                  const delRequest = store.delete(item.id);
                  delRequest.onsuccess = () => delResolve();
                  delRequest.onerror = (e) => {
                    console.error('删除数据失败:', e);
                    delResolve();
                  };
                });
              } else {
                // 如果不存在，创建新数据
                console.log(`创建新数据，将数字ID: ${item.id} 转换为字符串ID: ${newId}`);

                // 创建新对象，使用邮箱作为ID
                const newData = { ...item, id: newId };

                // 存储新数据
                await new Promise<void>((putResolve) => {
                  const putRequest = store.put(newData);
                  putRequest.onsuccess = () => putResolve();
                  putRequest.onerror = (e) => {
                    console.error('保存数据失败:', e);
                    putResolve();
                  };
                });

                // 删除原数字ID数据
                await new Promise<void>((delResolve) => {
                  const delRequest = store.delete(item.id);
                  delRequest.onsuccess = () => delResolve();
                  delRequest.onerror = (e) => {
                    console.error('删除数据失败:', e);
                    delResolve();
                  };
                });
              }
            }
          }
        }

        console.log('数据清理完成');
        db.close();
        resolve();
      };

      getAllRequest.onerror = (error) => {
        console.error('获取数据失败:', error);
        db.close();
        reject(error);
      };
    });
  } catch (error) {
    console.error('清理重复数据失败:', error);
    throw error;
  }
};