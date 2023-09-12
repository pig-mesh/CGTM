import request from "/@/utils/request"

export function fetchList(query?: Object) {
  return request({
    url: '/${moduleName}/${functionName}/page',
    method: 'get',
    params: query
  })
}

export function addObj(obj?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'post',
    data: obj
  })
}

export function getObj(id?: string) {
  return request({
    url: '/${moduleName}/${functionName}/' + id,
    method: 'get'
  })
}

export function delObjs(ids?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'delete',
    data: ids
  })
}

export function putObj(obj?: Object) {
  return request({
    url: '/${moduleName}/${functionName}',
    method: 'put',
    data: obj
  })
}

#if($ChildClassName)
export function delChildObj(ids?: Object) {
  return request({
    url: '/${moduleName}/${functionName}/child',
    method: 'delete',
    data: ids
  })
}
#end