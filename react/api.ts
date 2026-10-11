import { api } from '@/lib/common/api'
import type { Id } from '@/lib/common/types'

export interface ${ClassName}Record {
#foreach($field in $fieldList)
#if($field.formType == 'checkbox')
  ${field.attrName}?: (string | number)[]
#elseif($field.attrType == 'Boolean')
  ${field.attrName}?: boolean
#elseif($field.attrType == 'Integer' || $field.attrType == 'Double' || $field.attrType == 'Float' || $field.attrType == 'Short')
  ${field.attrName}?: number | null
#else
  ${field.attrName}?: string | number | null
#end
#end
#if($ChildClassName)
  ${childClassName}List?: ${ChildClassName}Record[]
#end
}
#if($ChildClassName)
export interface ${ChildClassName}Record {
#foreach($field in $childFieldList)
#if($field.formType == 'checkbox')
  ${field.attrName}?: (string | number)[]
#elseif($field.attrType == 'Boolean')
  ${field.attrName}?: boolean
#elseif($field.attrType == 'Integer' || $field.attrType == 'Double' || $field.attrType == 'Float' || $field.attrType == 'Short')
  ${field.attrName}?: number | null
#else
  ${field.attrName}?: string | number | null
#end
#end
}
#end

export function fetchList(params: Record<string, unknown>) {
  return api<{ records: ${ClassName}Record[]; total: number }>('/${moduleName}/${functionName}/page', { params })
}
export function getObj(params: Record<string, unknown>) {
  return api<${ClassName}Record[]>('/${moduleName}/${functionName}/details', { params })
}
export function addObj(data: ${ClassName}Record) {
  return api<boolean>('/${moduleName}/${functionName}', { method: 'POST', data })
}
export function putObj(data: ${ClassName}Record) {
  return api<boolean>('/${moduleName}/${functionName}', { method: 'PUT', data })
}
export function delObjs(data: Id[]) {
  return api<boolean>('/${moduleName}/${functionName}', { method: 'DELETE', data })
}
#if($ChildClassName)
export function delChildObj(data: Id[]) {
  return api<boolean>('/${moduleName}/${functionName}/child', { method: 'DELETE', data })
}
#end
