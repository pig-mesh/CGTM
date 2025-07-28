<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
#if($queryList)
      <!-- 查询表单区域 -->
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
#elseif($field.queryFormType == 'datetime')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker 
              type="datetime" 
              placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" 
              v-model="state.queryForm.${field.attrName}"
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
          <el-form-item>
            <el-button icon="search" type="primary" @click="getDataList">
              查询
            </el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-row>
#end

      <!-- 操作按钮区域 -->
      <el-row>
        <div class="mb8" style="width: 100%">
          <el-button 
            icon="folder-add" 
            type="primary" 
            class="ml10" 
            @click="formDialogRef.openDialog()"
            v-auth="'${moduleName}_${functionName}_add'"
          >
            新增
          </el-button>
          <el-button 
            plain 
            icon="upload-filled" 
            type="primary" 
            class="ml10" 
            @click="excelUploadRef.show()" 
            v-auth="'${moduleName}_${functionName}_add'"
          >
            导入
          </el-button>
          <el-button 
            plain 
            :disabled="multiple" 
            icon="Delete" 
            type="primary"
            v-auth="'${moduleName}_${functionName}_del'" 
            @click="handleDelete(selectObjs)"
          >
            删除
          </el-button>
          <right-toolbar 
            v-model:showSearch="showSearch" 
            :export="'${moduleName}_${functionName}_export'"
            @exportExcel="exportExcel" 
            class="ml10 mr20" 
            style="float: right;"
            @queryTable="getDataList"
          />
        </div>
      </el-row>

      <!-- 数据表格区域 -->
      <el-table 
        :data="state.dataList" 
        v-loading="state.loading" 
        border 
        :cell-style="tableStyle.cellStyle" 
        :header-cell-style="tableStyle.headerCellStyle"
        @selection-change="selectionChangHandle"
        @sort-change="sortChangeHandle"
      >
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column type="index" label="#" width="40" />
#foreach($field in $gridList)
#if($field.fieldDict)
        <el-table-column prop="${field.attrName}" label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" show-overflow-tooltip>
          <template #default="scope">
            <dict-tag :options="$field.fieldDict" :value="scope.row.${field.attrName}" />
          </template>
        </el-table-column>
#elseif($field.formType == 'upload-img')
        <el-table-column prop="${field.attrName}" label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" width="120">
          <template #default="scope">
            <el-image 
              v-if="scope.row.${field.attrName}"
              :src="scope.row.${field.attrName}" 
              :preview-src-list="[scope.row.${field.attrName}]"
              fit="cover"
              class="w-20 h-20 rounded"
              :preview-teleported="true"
            />
            <span v-else class="text-gray-400">暂无图片</span>
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
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              icon="edit-pen" 
              text 
              type="primary" 
              v-auth="'${moduleName}_${functionName}_edit'"
              @click="formDialogRef.openDialog(scope.row.${pk.attrName})"
            >
              编辑
            </el-button>
            <el-button 
              icon="delete" 
              text 
              type="primary" 
              v-auth="'${moduleName}_${functionName}_del'" 
              @click="handleDelete([scope.row.${pk.attrName}])"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <pagination 
        @size-change="sizeChangeHandle" 
        @current-change="currentChangeHandle" 
        v-bind="state.pagination" 
      />
    </div>

    <!-- 编辑、新增弹窗 -->
    <form-dialog ref="formDialogRef" @refresh="getDataList(false)" />

    <!-- 导入excel弹窗 (需要在 upms-biz/resources/file 下维护模板) -->
    <upload-excel
      ref="excelUploadRef"
      title="导入"
      url="/${moduleName}/${functionName}/import"
      temp-url="/admin/sys-file/local/file/${functionName}.xlsx"
      @refreshDataList="getDataList"
    />
  </div>
</template>

<script setup lang="ts" name="system${ClassName}">
// ========== 导入声明 ==========
import { BasicTableProps, useTable } from "/@/hooks/table";
import { fetchList, delObjs } from "/@/api/${moduleName}/${functionName}";
import { useMessage, useMessageBox } from "/@/hooks/message";
import { useDict } from '/@/hooks/dict';

// ========== 组件声明 ==========
// 异步加载表单弹窗组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));

// ========== 字典数据 ==========
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
// 加载字典数据
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

// ========== 组件引用 ==========
const formDialogRef = ref();          // 表单弹窗引用
const excelUploadRef = ref();         // Excel上传弹窗引用
const queryRef = ref();               // 查询表单引用

// ========== 响应式数据 ==========
const showSearch = ref(true);         // 是否显示搜索区域
const selectObjs = ref([]) as any;    // 表格多选数据
const multiple = ref(true);           // 是否多选

// ========== 表格状态 ==========
const state: BasicTableProps = reactive<BasicTableProps>({
  queryForm: {},    // 查询参数
  pageList: fetchList // 分页查询方法
});

// ========== Hook引用 ==========
// 表格相关Hook
const {
  getDataList,
  currentChangeHandle,
  sizeChangeHandle,
  sortChangeHandle,
  downBlobFile,
  tableStyle
} = useTable(state);

// ========== 方法定义 ==========
/**
 * 重置查询条件
 */
const resetQuery = () => {
  // 清空搜索条件
  queryRef.value?.resetFields();
  // 清空多选
  selectObjs.value = [];
  // 重新查询
  getDataList();
};

/**
 * 导出Excel文件
 */
const exportExcel = () => {
  downBlobFile(
    '/${moduleName}/${functionName}/export',
    Object.assign(state.queryForm, { ids: selectObjs }),
    '${functionName}.xlsx'
  );
};

/**
 * 表格多选事件处理
 * @param objs 选中的数据行
 */
const selectionChangHandle = (objs: { $pk.attrName: string }[]) => {
  selectObjs.value = objs.map(({ $pk.attrName }) => $pk.attrName);
  multiple.value = !objs.length;
};

/**
 * 删除数据处理
 * @param ids 要删除的数据ID数组
 */
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
