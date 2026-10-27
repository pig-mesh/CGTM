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
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { fetchList, getObj, delObjs, type ${ClassName}Record } from '@/api/${moduleName}/${functionName}'
import { useSession } from '@/lib/common/session'
import { cleanTableFilters, countTableFilters } from '@/lib/common/table-filters'
import { useConfirm } from '@/lib/common/use-confirm'
import { useOperation } from '@/lib/common/use-operation'
import { usePageSelection } from '@/lib/hooks/use-page-selection'
#set($queryInput = false)
#set($queryDict = false)
#set($queryDate = false)
#set($queryRange = false)
#foreach($field in $queryList)
#if($field.queryFormType == 'select' && $field.fieldDict)
#set($queryDict = true)
#elseif($field.queryFormType == 'date' || $field.queryFormType == 'datetime')
#set($queryDate = true)
#elseif($field.queryFormType == 'daterange' || $field.queryFormType == 'datetimerange')
#set($queryDate = true)
#set($queryRange = true)
#else
#set($queryInput = true)
#end
#end
#if($queryInput)
import { Input } from '@/components/ui/input'
#end
#if($queryDict)
import { DictSelect } from '@/components/common/dict-select'
#end
#if($queryDate)
import { SearchDateInput } from '@/components/common/search-date-input'
#end
#set($hasDict = false)
#foreach($field in $gridList)
#if($field.fieldDict)
#set($hasDict = true)
#end
#end
#if($hasDict)
import { useDict } from '@/lib/common/use-dict'
import { DictTag } from '@/components/common/dict-tag'
#end
import { PageControls, RecordsTable } from '@/components/common/management-ui'
import { SystemActionButton, SystemActionGroup } from '@/components/common/system-action'
import { TableToolbar } from '@/components/common/table-toolbar'
import { ${ClassName}Form } from './form'

#if($hasDict)
function DictionaryValue({ type, value }: { type: string; value: string | number | (string | number)[] }) {
  const query = useDict(type)
  return <DictTag options={query.data || []} value={value} />
}
#end
#if($queryRange)
// 日期范围：起止都选了才拼出后端的 xxxRange，逗号分隔由 Spring 绑定为 String[]
function withRange(search: Record<string, string>, key: string, part: 'Begin' | 'End', value: string) {
  const next = { ...search, [key + part]: value }
  const begin = next[key + 'Begin']
  const end = next[key + 'End']
  if (begin && end) next[key + 'Range'] = begin + ',' + end
  else delete next[key + 'Range']
  return next
}
const toDateTime = (value: string) => value ? value.replace('T', ' ') + (value.length === 16 ? ':00' : '') : ''

#end
export default function ${ClassName}Page() {
  const permissions = useSession((s) => s.user?.permissions || [])
  const can = (action: string) => permissions.includes('${permissionPrefix}_' + action)
  const confirm = useConfirm()
  const operation = useOperation()
  const [current, setCurrent] = useState(1)
  const [size, setSize] = useState(20)
  const [search, setSearch] = useState<Record<string, string>>({})
  const [filters, setFilters] = useState<Record<string, string>>({})
  const [showSearch, setShowSearch] = useState(true)
  const [selected, setSelected] = usePageSelection([current, size, filters])
  const [form, setForm] = useState<${ClassName}Record>()
  const query = useQuery({
    queryKey: ['${moduleName}', '${functionName}', current, size, filters],
    queryFn: () => fetchList(cleanTableFilters({ current, size, ...filters })),
  })
  async function refresh() {
    setSelected([])
    await query.refetch()
  }
  async function remove(ids: string[]) {
    if (!(await confirm({ description: '确认删除 ' + ids.length + ' 条${tableComment}记录？', destructive: true }))) return
    await operation.run(async () => {
      await delObjs(ids)
      setSelected([])
      if (current > 1 && ids.length === query.data?.records.length) setCurrent(current - 1)
      else await refresh()
    }, '删除成功')
  }
  return (
    <div className='flex min-w-0 flex-col gap-3 p-3 sm:p-4'>
      <TableToolbar
        showSearch={showSearch} onShowSearch={setShowSearch}
        pending={query.isFetching} onRefresh={refresh}
        selectedCount={selected.length} onClearSelection={() => setSelected([])}
        filterCount={countTableFilters(filters)}
        actions={<>
          {can('add') && <SystemActionButton action='add' disabled={operation.busy} onClick={() => setForm({#foreach($field in $formList)#if($field.formType == 'switch')#switchValues($field) ${field.attrName}: $switchOff,#end#end })}>新增</SystemActionButton>}
          {can('del') && <SystemActionButton action='delete' variant='outline' disabled={operation.busy || query.isFetching || !!query.error || !selected.length} onClick={() => remove(selected)}>批量删除</SystemActionButton>}
        </>}
        search={<form className='flex flex-wrap gap-2' onSubmit={(event) => {
          event.preventDefault()
          setCurrent(1)
          setSelected([])
          setFilters({ ...search })
          if (current === 1 && JSON.stringify(filters) === JSON.stringify(search)) void refresh()
        }}>
#foreach($field in $queryList)
#if($field.formType.startsWith('org-'))#set($queryKey = "${field.attrName}Keyword")#else#set($queryKey = $field.attrName)#end
#if($field.queryFormType == 'select' && $field.fieldDict)
          <DictSelect type='${field.fieldDict}' label='${field.fieldComment}' placeholder='${field.fieldComment}' all value={search.${field.attrName} ?? ''} onChange={(value) => setSearch({ ...search, ${field.attrName}: value })} />
#elseif($field.queryFormType == 'daterange' || $field.queryFormType == 'datetimerange')
#set($rangeType = "#if($field.queryFormType == 'datetimerange')datetime-local#{else}date#end")
          <SearchDateInput label='${field.fieldComment}起' type='${rangeType}' value={(search.${field.attrName}Begin ?? '').replace(' ', 'T')} onChange={(event) => setSearch(withRange(search, '${field.attrName}', 'Begin', #if($field.queryFormType == 'datetimerange')toDateTime(event.target.value)#{else}event.target.value#end))} />
          <SearchDateInput label='${field.fieldComment}止' type='${rangeType}' value={(search.${field.attrName}End ?? '').replace(' ', 'T')} onChange={(event) => setSearch(withRange(search, '${field.attrName}', 'End', #if($field.queryFormType == 'datetimerange')toDateTime(event.target.value)#{else}event.target.value#end))} />
#elseif($field.queryFormType == 'date' || $field.queryFormType == 'datetime')
          <SearchDateInput label='${field.fieldComment}' type='#if($field.queryFormType == "datetime")datetime-local#{else}date#end' value={(search.${field.attrName} ?? '').replace(' ', 'T')} onChange={(event) => setSearch({ ...search, ${field.attrName}: event.target.value.replace('T', ' ') })} />
#else
          <Input className='w-48' aria-label='${field.fieldComment}' placeholder='${field.fieldComment}' value={search.${queryKey} ?? ''} onChange={(event) => setSearch({ ...search, ${queryKey}: event.target.value })} />
#end
#end
          <SystemActionButton action='search' type='submit' variant='outline'>查询</SystemActionButton>
          <SystemActionButton action='reset' type='button' variant='ghost' onClick={() => { setSearch({}); setFilters({}); setCurrent(1); setSelected([]); if (current === 1 && !Object.keys(filters).length) void refresh() }}>重置</SystemActionButton>
        </form>}
      />
      <RecordsTable rows={query.data?.records || []} rowKey={(row) => String(row.${pk.attrName})}
        selected={selected} onSelect={can('del') ? setSelected : undefined}
        pending={query.isPending} error={query.error} onRetry={refresh}
        selectionDisabled={query.isFetching || operation.busy}
        columns={[
#foreach($field in $gridList)
#switchValues($field)
          { label: '${field.fieldComment}', width: 180, cell: (row) => #if($field.formType.startsWith('org-'))(row.${field.attrName} ?? []).map((item) => item.name).join('、')#elseif($field.fieldDict)<DictionaryValue type='${field.fieldDict}' value={row.${field.attrName} ?? ''} />#elseif($field.formType == 'switch')(String(row.${field.attrName}) === String($switchOn) ? '是' : '否')#else String(row.${field.attrName} ?? '')#end },
#end
          { label: '操作', width: 180, truncate: false, cell: (row) => <SystemActionGroup>
            {can('edit') && <SystemActionButton action='edit' compact size='sm' variant='ghost' disabled={operation.busy} onClick={() => operation.run(async () => {
              const records = await getObj({ ${pk.attrName}: row.${pk.attrName} })
              if (!records?.length) throw new Error('记录已不存在，请刷新列表')
              setForm(records[0])
            })}>编辑</SystemActionButton>}
            {can('del') && <SystemActionButton action='delete' compact size='sm' variant='ghost' disabled={operation.busy} onClick={() => remove([String(row.${pk.attrName})])}>删除</SystemActionButton>}
          </SystemActionGroup> },
        ]}
      />
      <PageControls current={current} size={size} total={query.data?.total || 0}
        disabled={query.isFetching} pending={query.isPending || query.isFetching || !!query.error}
        onChange={setCurrent} onSizeChange={(value) => { setSize(value); setCurrent(1) }} />
      {form && <${ClassName}Form initial={form} onClose={() => setForm(undefined)} onSaved={async () => { setForm(undefined); await refresh() }} />}
    </div>
  )
}
