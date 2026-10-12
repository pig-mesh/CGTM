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
#foreach($field in $queryList)
#if($field.queryFormType == 'select' && $field.fieldDict)
#set($queryDict = true)
#elseif($field.queryFormType == 'date' || $field.queryFormType == 'datetime')
#set($queryDate = true)
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
          {can('add') && <SystemActionButton action='add' disabled={operation.busy} onClick={() => setForm({})}>新增</SystemActionButton>}
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
#if($field.queryFormType == 'select' && $field.fieldDict)
          <DictSelect type='${field.fieldDict}' label='${field.fieldComment}' placeholder='${field.fieldComment}' all value={search.${field.attrName} ?? ''} onChange={(value) => setSearch({ ...search, ${field.attrName}: value })} />
#elseif($field.queryFormType == 'date' || $field.queryFormType == 'datetime')
          <SearchDateInput label='${field.fieldComment}' type='#if($field.queryFormType == "datetime")datetime-local#{else}date#end' value={(search.${field.attrName} ?? '').replace(' ', 'T')} onChange={(event) => setSearch({ ...search, ${field.attrName}: event.target.value.replace('T', ' ') })} />
#else
          <Input className='w-48' aria-label='${field.fieldComment}' placeholder='${field.fieldComment}' value={search.${field.attrName} ?? ''} onChange={(event) => setSearch({ ...search, ${field.attrName}: event.target.value })} />
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
          { label: '${field.fieldComment}', width: 180, cell: (row) => #if($field.fieldDict)<DictionaryValue type='${field.fieldDict}' value={row.${field.attrName} ?? ''} />#else String(row.${field.attrName} ?? '')#end },
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
