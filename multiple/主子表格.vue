<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
#if($queryList)
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
#foreach($field in $queryList)
#if($field.queryFormType == 'select')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-select v-model="state.queryForm.${field.attrName}" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end">
#if($field.fieldDict)
              <el-option 
                :label="item.label" 
                :value="item.value" 
                v-for="(item, index) in ${field.fieldDict}" 
                :key="index">
              </el-option>
#else
              <el-option label="请选择">0</el-option>
#end
            </el-select>
          </el-form-item>
#elseif($field.queryFormType == 'date')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker 
              type="date" 
              placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" 
              :value-format="dateStr"
              v-model="state.queryForm.${field.attrName}">
            </el-date-picker>
          </el-form-item>
#elseif($field.queryFormType == 'datetime')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker 
              type="datetime" 
              placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" 
              :value-format="dateTimeStr"
              v-model="state.queryForm.${field.attrName}">
            </el-date-picker>
          </el-form-item>
#elseif($field.formType == 'radio')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-radio-group v-model="state.queryForm.${field.attrName}">
#if($field.fieldDict)
              <el-radio 
                :label="item.value" 
                v-for="(item, index) in ${field.fieldDict}" 
                border 
                :key="index">
                {{ item.label }}
              </el-radio>
#else
              <el-radio label="${field.fieldComment}" border>${field.fieldComment}</el-radio>
#end
            </el-radio-group>
          </el-form-item>
#else
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-input 
              placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" 
              v-model="state.queryForm.${field.attrName}"
              style="max-width: 180px" 
            />
          </el-form-item>
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
            @click="formDialogRef.openDialog()"
            v-auth="'${moduleName}_${functionName}_add'">
            新增
          </el-button>
          <el-button 
            plain 
            :disabled="multiple" 
            icon="Delete" 
            type="primary"
            v-auth="'${moduleName}_${functionName}_del'" 
            @click="handleDelete(selectObjs)">
            删除
          </el-button>
          <right-toolbar 
            v-model:showSearch="showSearch" 
            :export="'${moduleName}_${functionName}_export'"
            @exportExcel="exportExcel" 
            class="ml10 mr20" 
            style="float: right;"
            @queryTable="getDataList">
          </right-toolbar>
        </div>
      </el-row>
      <el-table 
        :data="state.dataList" 
        v-loading="state.loading" 
        border 
        :cell-style="tableStyle.cellStyle" 
        :header-cell-style="tableStyle.headerCellStyle"
        @selection-change="selectionChangeHandle" 
        @sort-change="sortChangeHandle">
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column type="index" label="#" width="40" />
#foreach($field in $gridList)
#if($field.fieldDict)
        <el-table-column prop="${field.attrName}" label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" show-overflow-tooltip>
          <template #default="scope">
            <dict-tag :options="$field.fieldDict" :value="scope.row.${field.attrName}"></dict-tag>
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
#if($ChildClassName)
            <el-button 
              text 
              type="primary" 
              icon="view" 
              v-auth="'${moduleName}_${functionName}_view'" 
              @click="formDialogRef.openDialog(scope.row.${pk.attrName}, true)">
              详情
            </el-button>
#end
            <el-button 
              icon="edit-pen" 
              text 
              type="primary" 
              v-auth="'${moduleName}_${functionName}_edit'"
              @click="formDialogRef.openDialog(scope.row.${pk.attrName})">
              编辑
            </el-button>
            <el-button 
              icon="delete" 
              text 
              type="primary" 
              v-auth="'${moduleName}_${functionName}_del'" 
              @click="handleDelete([scope.row.${pk.attrName}])">
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

    <!-- 编辑、新增 -->
    <form-dialog ref="formDialogRef" @refresh="getDataList(false)" />
  </div>
</template>

<script setup lang="ts" name="system${ClassName}">
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs } from "/@/api/${moduleName}/${functionName}";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));

// 定义查询字典
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
#if($fieldDict)
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

// 定义变量内容
const formDialogRef = ref();
// 搜索变量
const queryRef = ref();
const showSearch = ref(true);
// 多选变量
const selectObjs = ref([]) as any;
const multiple = ref(true);

const state: BasicTableProps = reactive<BasicTableProps>({
  queryForm: {},
  pageList: fetchList
});

// table hook
const {
  getDataList,
  currentChangeHandle,
  sizeChangeHandle,
  sortChangeHandle,
  downBlobFile,
  tableStyle
} = useTable(state);

// 清空搜索条件
const resetQuery = () => {
  // 清空搜索条件
  queryRef.value?.resetFields();
  // 清空多选
  selectObjs.value = [];
  getDataList();
};

// 导出excel
const exportExcel = () => {
  downBlobFile('/${moduleName}/${functionName}/export', Object.assign(state.queryForm, { ids: selectObjs }), '${functionName}.xlsx');
};

// 多选事件
const selectionChangeHandle = (objs: { $pk.attrName: string }[]) => {
  selectObjs.value = objs.map(({ $pk.attrName }) => $pk.attrName);
  multiple.value = !objs.length;
};

// 删除操作
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
</script>
