import request from "/@/utils/request"\n\nexport function fetchList(query?: Object) {\n  return request({\n    url: '/${moduleName}/${functionName}/page',\n    method: 'get',\n    params: query\n  })\n}\n\nexport function addObj(obj?: Object) {\n  return request({\n    url: '/${moduleName}/${functionName}',\n    method: 'post',\n    data: obj\n  })\n}\n\nexport function getObj(id?: string) {\n  return request({\n    url: '/${moduleName}/${functionName}/' + id,\n    method: 'get'\n  })\n}\n\nexport function delObjs(ids?: Object) {\n  return request({\n    url: '/${moduleName}/${functionName}',\n    method: 'delete',\n    data: ids\n  })\n}\n\nexport function putObj(obj?: Object) {\n  return request({\n    url: '/${moduleName}/${functionName}',\n    method: 'put',\n    data: obj\n  })\n}\n\n#if($ChildClassName)\nexport function delChildObj(ids?: Object) {\n  return request({\n    url: '/${moduleName}/${functionName}/child',\n    method: 'delete',\n    data: ids\n  })\n}\n#end
