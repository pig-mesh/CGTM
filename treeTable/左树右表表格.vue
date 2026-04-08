#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPkField = $field)
#break
#end
#end
<template>
  <div class="layout-padding">
    <splitpanes>
      <pane size="15">
        <div class="layout-padding-auto layout-padding-view">
          <el-scrollbar>
            <div class="mb8" style="display: flex; align-items: center; justify-content: space-between;">
              <span style="font-weight: 500;">左侧节点</span>
              <div>
                <el-tooltip :content="currentTreeId ? '新增子节点' : '新增根节点'" placement="top">
                  <el-button link type="primary" icon="FolderAdd" @click="handleAddTree" />
                </el-tooltip>
              </div>
            </div>
            <el-tree
              ref="treeRef"
              :data="treeData"
              :props="treeProps"
              node-key="${childPkField.attrName}"
              highlight-current
              default-expand-all
              :expand-on-click-node="false"
              @node-click="handleTreeNodeClick"
            >
              <template #default="{ node, data }">
                <span style="flex: 1; display: flex; align-items: center; justify-content: space-between;">
                  <span>{{ node.label }}</span>
                  <span v-if="currentTreeId === data.${childPkField.attrName}" style="margin-left: 8px;">
                    <el-tooltip content="编辑" placement="top">
                      <el-button link type="primary" icon="Edit" size="small" @click.stop="handleEditTree" />
                    </el-tooltip>
                    <el-tooltip content="删除" placement="top">
                      <el-button link type="danger" icon="Delete" size="small" @click.stop="handleDeleteTree" />
                    </el-tooltip>
                  </span>
                </span>
              </template>
            </el-tree>
          </el-scrollbar>
        </div>
      </pane>
      <pane class="ml8">
        <div class="layout-padding-auto layout-padding-view">
#if($queryList)
            <el-row v-show="showSearch">
              <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
#foreach($field in $queryList)
#if($field.attrName != ${childField})
#if($field.queryFormType == 'select')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
                  <el-select v-model="state.queryForm.${field.attrName}" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end">
#if($field.fieldDict)
                    <el-option 
                      :label="item.label" 
                      :value="item.value" 
                      v-for="(item, index) in ${field.fieldDict}" 
                      :key="index"
                    />
#else
                    <el-option label="请选择" value="0" />
#end
                  </el-select>
                </el-form-item>
#elseif($field.queryFormType == 'date')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
                  <el-date-picker
                    type="date"
                    placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"
                    v-model="state.queryForm.${field.attrName}"
                    :value-format="dateStr"
                  />
                </el-form-item>
#elseif($field.queryFormType == 'daterange')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}Range">
                  <el-date-picker
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    v-model="state.queryForm.${field.attrName}Range"
                    :value-format="dateStr"
                  />
                </el-form-item>
#elseif($field.queryFormType == 'datetime')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
                  <el-date-picker
                    type="datetime"
                    placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"
                    v-model="state.queryForm.${field.attrName}"
                    :value-format="dateTimeStr"
                  />
                </el-form-item>
#elseif($field.queryFormType == 'datetimerange')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}Range">
                  <el-date-picker
                    type="datetimerange"
                    range-separator="至"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    v-model="state.queryForm.${field.attrName}Range"
                    :value-format="dateTimeStr"
                  />
                </el-form-item>
#elseif($field.formType == 'radio')
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
                  <el-radio-group v-model="state.queryForm.${field.attrName}">
#if($field.fieldDict)
                    <el-radio 
                      :label="item.value" 
                      v-for="(item, index) in ${field.fieldDict}" 
                      border 
                      :key="index"
                    >
                      {{ item.label }}
                    </el-radio>
#else
                    <el-radio label="${field.fieldComment}" border>
                      ${field.fieldComment}
                    </el-radio>
#end
                  </el-radio-group>
                </el-form-item>
#else
                <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
                  <el-input 
                    placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" 
                    v-model="state.queryForm.${field.attrName}" 
                  />
                </el-form-item>
#end
#end
#end
                <el-form-item>
                  <el-button icon="search" type="primary" @click="getDataList">
                    查询
                  </el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>
            </el-row>
#end

            <el-row>
              <div class="mb8" style="width: 100%">
                <el-button 
                  icon="folder-add" 
                  type="primary" 
                  class="ml10" 
                  @click="handleAddRow"
                  v-auth="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_add'"
                >
                  新增
                </el-button>
                <el-button 
                  plain 
                  icon="upload-filled" 
                  type="primary" 
                  class="ml10" 
                  @click="excelUploadRef.show()" 
                  v-auth="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_add'"
                >
                  导入
                </el-button>
                <el-button 
                  plain 
                  :disabled="multiple" 
                  icon="Delete" 
                  type="primary"
                  v-auth="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_del'" 
                  @click="handleDelete(selectObjs)"
                >
                  删除
                </el-button>
                <right-toolbar 
                  v-model:showSearch="showSearch" 
                  :export="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_export'"
                  @exportExcel="exportExcel" 
                  class="ml10 mr20" 
                  style="float: right;"
                  @queryTable="getDataList"
                />
              </div>
            </el-row>

            <el-table 
              :data="state.dataList" 
              v-loading="state.loading" 
              border 
              :cell-style="tableStyle.cellStyle" 
              :header-cell-style="tableStyle.headerCellStyle"
              @selection-change="selectionChangeHandle"
              @sort-change="sortChangeHandle"
            >
              <el-table-column type="selection" width="40" align="center" />
              <el-table-column type="index" label="#" width="40" />
#foreach($field in $gridList)
#if($field.fieldDict)
              <el-table-column prop="${field.attrName}" label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" show-overflow-tooltip>
                <template #default="scope">
                  <dict-tag :options="${field.fieldDict}" :value="scope.row.${field.attrName}" />
                </template>
              </el-table-column>
#elseif(${field.formType} == 'upload-img')
              <el-table-column prop="${field.attrName}" label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end">
                <template #default="{ row }">
                  <upload-img disabled v-model:imageUrl="row.${field.attrName}"></upload-img>
                </template>
              </el-table-column>
#else
              <el-table-column
                prop="${field.attrName}"
                label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"
#if(${field.gridSort} == '1')
                sortable="custom"
#end
                show-overflow-tooltip
              />
#end
#end
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button 
                    icon="edit-pen" 
                    text 
                    type="primary" 
                    v-auth="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_edit'"
                    @click="handleEditRow(scope.row)"
                  >
                    编辑
                  </el-button>
                  <el-button 
                    icon="delete" 
                    text 
                    type="primary" 
                    v-auth="'$str.lowerCase($moduleName)_$str.lowerCase($functionName)_del'" 
                    @click="handleDelete([scope.row.${pk.attrName}])"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              @size-change="sizeChangeHandle"
              @current-change="currentChangeHandle"
              v-bind="state.pagination"
            />
          </div>
      </pane>
    </splitpanes>

    <tree-form ref="treeFormDialogRef" @refresh="reloadTree" />
    <form-dialog ref="formDialogRef" @refresh="getDataList(false)" />

    <upload-excel
      ref="excelUploadRef"
      title="导入"
      url="/${moduleName}/${functionName}/import"
      temp-url="/admin/sys-file/local/file/${functionName}.xlsx"
      @refreshDataList="getDataList"
    />
  </div>
</template>

<script setup lang="ts" name="system${ClassName}TreeTable">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs, fetchTreeList, delTreeObjs } from "/@/api/${moduleName}/${functionName}";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';

const FormDialog = defineAsyncComponent(() => import('./form.vue'));
const TreeForm = defineAsyncComponent(() => import('./tree-form.vue'));

#set($fieldDict=[])
#foreach($field in $queryList)
#if($field.fieldDict)
#set($void=$fieldDict.add($field.fieldDict))
#end
#end
#foreach($field in $gridList)
#if($field.fieldDict)
#set($void=$fieldDict.add($field.fieldDict))
#end
#end
#if($fieldDict && $fieldDict.size() > 0)
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

interface TreeNode {
  [key: string]: any;
  children?: TreeNode[];
}

const formDialogRef = ref();
const treeFormDialogRef = ref();
const excelUploadRef = ref();
const queryRef = ref();
const treeRef = ref();

const showSearch = ref(true);
const selectObjs = ref([]) as any;
const multiple = ref(true);
const currentTreeId = ref<string | number | null>(null);
const treeData = ref<TreeNode[]>([]);

const treeProps = {
  children: 'children',
  label: '${nameField}'
};

const state: BasicTableProps = reactive<BasicTableProps>({
  queryForm: {},
  pageList: fetchList
});

const {
  getDataList,
  currentChangeHandle,
  sizeChangeHandle,
  sortChangeHandle,
  downBlobFile,
  tableStyle
} = useTable(state);

const reloadTree = async () => {
  const { data } = await fetchTreeList();
  treeData.value = data || [];
  nextTick(() => {
    treeRef.value?.setCurrentKey(currentTreeId.value);
  });
};

const resetQuery = () => {
  queryRef.value?.resetFields();
  selectObjs.value = [];
  if (currentTreeId.value) {
    state.queryForm.${childField} = currentTreeId.value;
  } else {
    delete state.queryForm.${childField};
  }
  getDataList();
};

const exportExcel = () => {
  downBlobFile(
    '/${moduleName}/${functionName}/export',
    { ...state.queryForm, ids: selectObjs.value },
    '${functionName}.xlsx'
  );
};

const selectionChangeHandle = (objs: { ${pk.attrName}: string }[]) => {
  selectObjs.value = objs.map(({ ${pk.attrName} }) => ${pk.attrName});
  multiple.value = !objs.length;
};

const handleDelete = async (ids: string[]) => {
  try {
    await useMessageBox().confirm('此操作将永久删除');
  } catch {
    return;
  }

  try {
    await delObjs(ids);
    getDataList();
    useMessage().success('删除成功');
  } catch (err: any) {
    useMessage().error(err.msg);
  }
};

const handleTreeNodeClick = (data: TreeNode) => {
  currentTreeId.value = data.${childPkField.attrName};
  state.queryForm.${childField} = currentTreeId.value;
  treeRef.value?.setCurrentKey(currentTreeId.value);
  getDataList();
};

const clearTreeSelection = () => {
  currentTreeId.value = null;
  treeRef.value?.setCurrentKey(null);
  delete state.queryForm.${childField};
};

const handleAddTree = () => {
  treeFormDialogRef.value?.openDialog(undefined, currentTreeId.value ?? 0);
};

const handleEditTree = () => {
  if (!currentTreeId.value) {
    useMessage().warning('请先选择一个左侧节点');
    return;
  }
  treeFormDialogRef.value?.openDialog(currentTreeId.value);
};

const handleDeleteTree = async () => {
  if (!currentTreeId.value) {
    useMessage().warning('请先选择一个左侧节点');
    return;
  }

  try {
    await useMessageBox().confirm('此操作将永久删除左侧节点');
  } catch {
    return;
  }

  try {
    await delTreeObjs([currentTreeId.value]);
    clearTreeSelection();
    await reloadTree();
    getDataList();
    useMessage().success('删除成功');
  } catch (err: any) {
    useMessage().error(err.msg);
  }
};

const handleAddRow = () => {
  formDialogRef.value?.openDialog(undefined, currentTreeId.value ?? undefined);
};

const handleEditRow = (row: any) => {
  formDialogRef.value?.openDialog(row.${pk.attrName}, currentTreeId.value ?? undefined);
};

onMounted(async () => {
  await reloadTree();
  getDataList();
});
</script>
