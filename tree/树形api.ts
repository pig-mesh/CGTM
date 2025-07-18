import request from "/@/utils/request"

// ========== 树形表格CRUD接口 ==========

/**
 * 获取树形列表数据
 * @param query - 查询参数对象
 * @returns Promise<树形数据>
 */
export function fetchTreeList(query?: Object) {
  return request({
    url: '/${moduleName}/${functionName}/tree',
    method: 'get',
    params: query
  })
}

/**
 * 新增数据
 * @param obj - 要新增的数据对象
 * @returns Promise<boolean> - 操作结果
 */
export function addObj(obj?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'post',
    data: obj
  })
}

/**
 * 获取详情数据
 * @param obj - 查询参数对象（包含ID等）
 * @returns Promise<数据详情>
 */
export function getObj(obj?: Object) {
  return request({
    url: '/${moduleName}/${functionName}/details',
    method: 'get',
    params: obj
  })
}

/**
 * 批量删除数据（递归删除子节点）
 * @param ids - 要删除的ID数组
 * @returns Promise<操作结果>
 */
export function delObjs(ids?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新数据
 * @param obj - 要更新的数据对象
 * @returns Promise<操作结果>
 */
export function putObj(obj?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'put',
    data: obj
  })
}

/**
 * 获取所有父级节点
 * @returns Promise<父级节点列表>
 */
export function getParentNodes() {
  return request({
    url: '/${moduleName}/${functionName}/parent',
    method: 'get'
  })
}

/**
 * 根据父ID获取子节点
 * @param parentId - 父级ID
 * @returns Promise<子节点列表>
 */
export function getChildrenByParentId(parentId: string | number) {
  return request({
    url: '/${moduleName}/${functionName}/children',
    method: 'get',
    params: { parentId }
  })
}

// ========== 工具函数 ==========

/**
 * 验证字段值唯一性
 * @param rule - 验证规则对象
 * @param value - 要验证的值
 * @param callback - 验证回调函数
 * @param isEdit - 是否为编辑模式
 * 
 * @example
 * // 在表单验证规则中使用
 * fieldName: [
 *   {
 *     validator: (rule, value, callback) => {
 *       validateExist(rule, value, callback, form.${pk.attrName} !== '');
 *     },
 *     trigger: 'blur',
 *   },
 * ]
 */
export function validateExist(rule: any, value: any, callback: any, isEdit: boolean) {
  // 编辑模式下跳过验证
  if (isEdit) {
    return callback();
  }

  // 查询是否存在相同值
  getObj({ [rule.field]: value }).then((response) => {
    const result = response.data;
    if (result !== null && result.length > 0) {
      callback(new Error('数据已经存在'));
    } else {
      callback();
    }
  });
}