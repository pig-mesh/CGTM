## 开关：按属性类型决定开/关的值，Boolean 用 true/false，整型用 1/0，其余用 '1'/'0'
#macro(switchValues $f)
#if($f.attrType == 'Boolean')
#set($switchOn = 'true')
#set($switchOff = 'false')
#elseif($f.attrType == 'Integer' || $f.attrType == 'Long' || $f.attrType == 'Short')
#set($switchOn = '1')
#set($switchOff = '0')
#else
#set($switchOn = "'1'")
#set($switchOff = "'0'")
#end
#end
#set($permissionPrefix = "${moduleName}_${functionName}")
#set($permissionPrefix = $permissionPrefix.toLowerCase())
#set($allFields = [])
#set($hasMainFields = false)
#foreach($field in $formList)
#if($field.attrName != $pk.attrName)
#set($hasMainFields = true)
#set($void = $allFields.add($field))
#end
#end
#if($ChildClassName)
#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPk = $field)
#end
#if($field.formItem == '1' && $field.attrName != $childField && $field.primaryPk != '1')
#set($void = $allFields.add($field))
#end
#end
#end
#set($types = [])
#set($needsInput = false)
#set($hasDictControls = false)
#set($needsSwitch = false)
#set($hasDuplicate = false)
#set($hasValidation = false)
#set($hasOrg = false)
#foreach($field in $allFields)
#set($void = $types.add($field.formType))
#if($field.formType.startsWith('org-'))
#set($hasOrg = true)
#end
#if($field.formType == 'select' || $field.formType == 'radio' || $field.formType == 'checkbox')
#if($field.fieldDict)
#set($hasDictControls = true)
#else
#set($needsInput = true)
#end
#end
#if($field.attrType == 'Boolean' || $field.formType == 'switch')
#set($needsSwitch = true)
#elseif(!$field.formType.startsWith('org-') && $field.formType != 'select' && $field.formType != 'radio' && $field.formType != 'checkbox' && $field.formType != 'textarea' && $field.formType != 'upload-img' && $field.formType != 'upload-file' && $field.formType != 'editor')
#set($needsInput = true)
#end
#if($field.formValidator == 'duplicate')
#set($hasDuplicate = true)
#elseif($field.formValidator)
#set($hasValidation = true)
#end
#end
import { useState } from 'react'
#if($hasOrg)
import { OrgSelect } from '@/components/common/org'
#end
import { addObj, putObj,#if($hasDuplicate) getObj,#end type ${ClassName}Record#if($ChildClassName), delChildObj#end } from '@/api/${moduleName}/${functionName}'
import { useOperation } from '@/lib/common/use-operation'
#if($ChildClassName)
import { useSession } from '@/lib/common/session'
import { useConfirm } from '@/lib/common/use-confirm'
#end
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
#if($needsInput)
import { Input } from '@/components/ui/input'
#end
#if($needsSwitch)
import { Switch } from '@/components/ui/switch'
#end
#if($types.contains('textarea'))
import { Textarea } from '@/components/ui/textarea'
#end
#if($hasDictControls)
import { DictSelect } from '@/components/common/dict-select'
#end
#if($types.contains('upload-img'))
import { ImageUpload } from '@/components/common/image-upload'
#end
#if($types.contains('upload-file'))
import { FilePicker } from '@/components/common/file-picker'
#end
#if($types.contains('editor'))
import { RichTextEditor } from '@/components/common/rich-text-editor'
#end
import { ManagedForm#if(!$allFields.isEmpty()), ManagedField#end } from '@/components/common/managed-form'
import { SystemActionButton } from '@/components/common/system-action'

#if($hasValidation)
const patterns: Record<string, RegExp> = {
  number: /^-?\d+(\.\d+)?$/,
  letter: /^[A-Za-z]+$/,
  letterAndNumber: /^[A-Za-z0-9]+$/,
  mobilePhone: /^1[3-9][0-9]{9}$/,
  letterStartNumberIncluded: /^[A-Za-z][A-Za-z0-9]*$/,
  noChinese: /^[^\u4e00-\u9fa5]+$/,
  chinese: /^[\u4e00-\u9fa5]+$/,
  email: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  url: /^(https?|ftp|file):\/\/\S+$/,
}
#end
#macro(control $field $row $update $name)
            <ManagedField<unknown> allowFalse name={$name} value={${row}.${field.attrName}} label='${field.fieldComment}'#if($field.formRequired == '1') required#end
#if($field.formValidator == 'duplicate' && $row == 'form')
              rules={{ validate: async (value) => {
                if (initial.${pk.attrName} != null || !String(value ?? '').trim()) return true
                return (await getObj({ ${field.attrName}: value })).length ? '数据已存在' : true
              } }}
#elseif($field.formValidator && $field.formValidator != 'duplicate')
              rules={{ validate: (value) => !String(value ?? '').trim() || patterns['${field.formValidator}']?.test(String(value)) || '字段格式不正确' }}
#end
            >
              {({ onChange, onOpenChange, controlProps }) => {
                const value = ${row}.${field.attrName}
                const change = (next: unknown) => { onChange(next); ${update}({ ...${row}, ${field.attrName}: next } as typeof ${row}) }
                // 保留 ManagedField 的失焦与错误提示关联。
                void onOpenChange
                void controlProps
#if($field.formType.startsWith('org-'))
#set($orgType = $field.formType.replace('org-', '').replace('-multiple', ''))
                return <OrgSelect valueType='item' type='${orgType}' multiple={#if($field.formType.endsWith('-multiple'))true#else false#end} value={value} onChange={change} triggerProps={controlProps} onOpenChange={onOpenChange} label='${field.fieldComment}' />
#elseif($field.formType == 'checkbox' && $field.fieldDict)
                return <DictSelect multiple type='$!{field.fieldDict}' value={Array.isArray(value) ? value.map(String) : []} onChange={change} triggerProps={controlProps} onOpenChange={onOpenChange} label='${field.fieldComment}' />
#elseif(($field.formType == 'select' || $field.formType == 'radio') && $field.fieldDict)
                return <DictSelect type='$!{field.fieldDict}' value={String(value ?? '')} onChange={(next) => change(#if($field.attrType == 'Integer' || $field.attrType == 'Double')next === '' ? null : Number(next)#else next#end)} triggerProps={controlProps} onOpenChange={onOpenChange} label='${field.fieldComment}' #if($field.formType == 'radio')presentation='toggle'#end />
#elseif($field.formType == 'checkbox')
                return <Input {...controlProps} value={Array.isArray(value) ? value.join(',') : ''} placeholder='多个值用逗号分隔' onChange={(event) => change(event.target.value.split(',').filter(Boolean))} />
#elseif($field.formType == 'textarea')
                return <Textarea {...controlProps} value={String(value ?? '')} onChange={(event) => change(event.target.value)} />
#elseif($field.formType == 'upload-img')
                return <ImageUpload value={String(value ?? '')} onChange={change} disabled={operation.busy} label='${field.fieldComment}' />
#elseif($field.formType == 'upload-file')
                return <FilePicker files={String(value ?? '').split(',').filter(Boolean).map((url) => ({ url, name: url.split('/').pop() }))} onChange={(files) => change(files.map((file) => file.url).join(','))} disabled={operation.busy} />
#elseif($field.formType == 'editor')
                return <RichTextEditor value={String(value ?? '')} onChange={change} disabled={operation.busy} label='${field.fieldComment}' />
#elseif($field.formType == 'switch')
#switchValues($field)
                return <Switch {...controlProps} checked={String(value) === String($switchOn)} onCheckedChange={(checked) => change(checked ? $switchOn : $switchOff)} />
#elseif($field.attrType == 'Boolean')
                return <Switch {...controlProps} checked={value === true} onCheckedChange={change} />
#else
                return <Input {...controlProps} type='#if($field.formType == "number")number#elseif($field.formType == "date")date#elseif($field.formType == "datetime")datetime-local#{else}text#end' value={String(value ?? '').replace(' ', '#if($field.formType == "datetime")T#else #end')} onChange={(event) => change(#if($field.formType == 'number' && $field.attrType != 'Long' && $field.attrType != 'BigDecimal')event.target.value === '' ? null : Number(event.target.value)#elseif($field.formType == 'datetime')event.target.value ? event.target.value.replace('T', ' ') + (event.target.value.length === 16 ? ':00' : '') : null#else event.target.value#end)} />
#end
              }}
            </ManagedField>
#end

export function ${ClassName}Form({ initial, onClose, onSaved }: {
  initial: ${ClassName}Record
  onClose: () => void
  onSaved: () => Promise<void>
}) {
  const [form#if($hasMainFields), setForm#end] = useState(initial)
  const operation = useOperation()
#if($hasDuplicate)
  void getObj
#end
#if($ChildClassName)
  const permissions = useSession((s) => s.user?.permissions || [])
  const confirm = useConfirm()
  const [children, setChildren] = useState(() => (initial.${childClassName}List || []).map((value) => ({ key: crypto.randomUUID(), value })))
#end
  return (
    <Dialog disablePointerDismissal open onOpenChange={(open) => { if (!open && !operation.busy) onClose() }}>
      <DialogContent className='max-h-[90svh] overflow-y-auto sm:max-w-4xl'>
        <DialogHeader>
          <DialogTitle>{initial.${pk.attrName} == null ? '新增' : '编辑'}${tableComment}</DialogTitle>
          <DialogDescription className='sr-only'>填写表单并保存</DialogDescription>
        </DialogHeader>
        <ManagedForm defaultValues={initial} disabled={operation.busy} onSubmit={(event) => {
          event.preventDefault()
          return operation.run(async () => {
            const data = { ...form#if($ChildClassName), ${childClassName}List: children.map((child) => child.value)#end }
            if (initial.${pk.attrName} == null) await addObj(data)
            else await putObj(data)
            await onSaved()
          }, '保存成功')
        }}>
          <div className='grid gap-4#if($formLayout == 2) sm:grid-cols-2#end'>
#foreach($field in $formList)
#if($field.attrName != $pk.attrName)
#set($fieldName = "'${field.attrName}'")
#control($field 'form' 'setForm' $fieldName)
#end
#end
          </div>
#if($ChildClassName)
          <div className='my-4 flex flex-col gap-4'>
            <SystemActionButton action='add' type='button' variant='outline' disabled={operation.busy} onClick={() => setChildren([...children, { key: crypto.randomUUID(), value: {} }])}>新增明细</SystemActionButton>
            {children.map((child) => {
              const updateChild = (value: typeof child.value) => setChildren((rows) => rows.map((row) => row.key === child.key ? { ...row, value } : row))
              return <fieldset key={child.key} className='grid gap-4 rounded-md border p-4 sm:grid-cols-2'>
                <legend>明细</legend>
#foreach($field in $childFieldList)
#if($field.formItem == '1' && $field.primaryPk != '1' && $field.attrName != $childField)
#set($fieldName = "'children.' + child.key + '.${field.attrName}'")
#control($field 'child.value' 'updateChild' $fieldName)
#end
#end
                {(child.value.${childPk.attrName} == null || permissions.includes('${permissionPrefix}_del')) && <SystemActionButton action='delete' type='button' variant='outline' disabled={operation.busy} onClick={async () => {
                  const id = child.value.${childPk.attrName}
                  if (id != null && !(await confirm({ description: '确认立即删除此明细？此操作不会随关闭表单撤销。', destructive: true }))) return
                  await operation.run(async () => {
                    if (id != null) await delChildObj([id])
                    setChildren((rows) => rows.filter((row) => row.key !== child.key))
                  })
                }}>删除明细</SystemActionButton>}
              </fieldset>
            })}
          </div>
#end
          <DialogFooter>
            <SystemActionButton action='close' type='button' variant='outline' disabled={operation.busy} onClick={onClose}>取消</SystemActionButton>
            <SystemActionButton action='save' type='submit' disabled={operation.busy}>保存</SystemActionButton>
          </DialogFooter>
        </ManagedForm>
      </DialogContent>
    </Dialog>
  )
}
