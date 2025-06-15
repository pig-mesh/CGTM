<template>
  <el-drawer :title="form.${pk.attrName} ? (detail ? '详情' : '编辑') : '添加'" v-model="visible" size="50%">
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" :disabled="detail" v-loading="loading">
      <el-row :gutter="24">
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName})
#if($formLayout == 1)
        <el-col :span="24" class="mb20">
#elseif($formLayout == 2)
        <el-col :span="12" class="mb20">
#end
#if($field.formType == 'text')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end"/>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'textarea')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-input type="textarea" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end"/>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'select')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-select v-model="form.${field.attrName}" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end">
#if($field.fieldDict)
              <el-option :value="item.value" :label="item.label" v-for="(item, index) in ${field.fieldDict}" :key="index"></el-option>
#else
              <el-option label="请选择" value="0"></el-option>
#end
            </el-select>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'radio')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-radio-group v-model="form.${field.attrName}">
#if($field.fieldDict)
              <el-radio :label="item.value" v-for="(item, index) in ${field.fieldDict}" border :key="index">{{ item.label }}</el-radio>
#else
              <el-radio label="${field.fieldComment}" border>${field.fieldComment}</el-radio>
#end
            </el-radio-group>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'checkbox')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-checkbox-group v-model="form.${field.attrName}">
#if($field.fieldDict)
              <el-checkbox :label="item.value" v-for="(item, index) in ${field.fieldDict}" :key="index">{{ item.label }}</el-checkbox>
#else
              <el-checkbox label="启用" name="type"></el-checkbox>
              <el-checkbox label="禁用" name="type"></el-checkbox>
#end
            </el-checkbox-group>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'date')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-date-picker type="date" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" v-model="form.${field.attrName}" :value-format="dateStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'datetime')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-date-picker type="datetime" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" v-model="form.${field.attrName}" :value-format="dateTimeStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'number')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-input-number :min="1" :max="1000" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end"></el-input-number>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'upload-file')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <upload-file v-model="form.${field.attrName}"></upload-file>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'upload-img')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <upload-img v-model:imageUrl="form.${field.attrName}"></upload-img>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'editor')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <editor v-model:get-html="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end"></editor>
          </el-form-item>
        </el-col>
#else
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end"/>
          </el-form-item>
        </el-col>
#end
#end
#end
      </el-row>
      <el-row :gutter="24">
        <sc-form-table
          v-model="form.${childClassName}List"
          :addTemplate="childTemp"
          @delete="deleteChild"
          placeholder="暂无数据"
        >
#set($ignoreColumnList = ["create_by","create_time","update_by","update_time","del_flag","tenant_id"])
#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#elseif($ignoreColumnList.contains(${field.fieldName}))
#elseif($field.attrName == $childField)
#else  
          <el-table-column label="#if(${field.fieldComment})${field.fieldComment}#else${field.fieldName}#end" prop="${field.attrName}">
            <template #default="{ row, $index }">
              <el-form-item :prop="`${childClassName}List.${$index}.${field.attrName}`" :rules="[{ required: true, trigger: 'blur' }]">
                <el-input v-model="row.${field.attrName}"/>
              </el-form-item>
            </template>
          </el-table-column>
#end
#end
        </sc-form-table>
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
      </span>
    </template>
  </el-drawer>
</template>

<script setup lang="ts" name="${ClassName}Dialog">
// ========== 1. 导入语句 ==========
import { useDict } from '/@/hooks/dict';
import { rule } from '/@/utils/validate';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, delChildObj, validateExist } from '/@/api/${moduleName}/${functionName}';

// ========== 2. 组件定义 ==========
// 异步加载表格组件
const scFormTable = defineAsyncComponent(() => import('/@/components/FormTable/index.vue'));

// 定义组件事件
const emit = defineEmits(['refresh']);

// ========== 3. 响应式数据定义 ==========
// 基础响应式变量
const dataFormRef = ref(); // 表单引用
const visible = ref(false); // 抽屉显示状态
const loading = ref(false); // 加载状态
const detail = ref(false); // 是否为详情模式

// 表单数据对象
const form = reactive({
#if(!$formList.contains(${pk.attrName}))
  ${pk.attrName}: '', // 主键
#end
#foreach($field in $formList)
#if($field.formType == 'number')
  ${field.attrName}: 0, // ${field.fieldComment}
#elseif($field.formType == 'checkbox')
  ${field.attrName}: [], // ${field.fieldComment}
#else
  ${field.attrName}: '', // ${field.fieldComment}
#end
#end
  ${childClassName}List: [], // 子表数据列表
});

// 子表数据模板
const childTemp = reactive({
#foreach($field in $childFieldList)
  ${field.attrName}: '', // ${field.fieldComment}
#end
});

// ========== 4. 字典数据处理 ==========
#set($fieldDict=[])
#foreach($field in $gridList)
#if($field.fieldDict)
#set($void=$fieldDict.add($field.fieldDict))
#end
#end
#if($fieldDict && $fieldDict.size() > 0)
// 加载字典数据
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

// ========== 5. 表单校验规则 ==========
const dataRules = ref({
#foreach($field in $formList)
#if($field.formRequired == '1' && $field.formValidator == 'duplicate')
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        // 重复性校验（编辑时跳过）
        validateExist(rule, value, callback, form.${pk.attrName} !== '');
      },
      trigger: 'blur',
    }
  ],
#elseif($field.formRequired == '1' && $field.formValidator)
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' },
    { validator: rule.${field.formValidator}, trigger: 'blur' }
  ],
#elseif($field.formRequired == '1')
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' }
  ],
#elseif($field.formValidator)
  ${field.attrName}: [
    { validator: rule.${field.formValidator}, trigger: 'blur' }
  ],
#end
#end
});

// ========== 6. 方法定义 ==========
// 获取主子表详情数据
const get${ClassName}Data = async (id: string) => {
  try {
    loading.value = true;
    const { data } = await getObj({ ${pk.attrName}: id });
    // 直接将第一条数据赋值给表单
    Object.assign(form, data[0]);
  } catch (error) {
    useMessage().error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 打开抽屉方法
const openDialog = (id: string, isDetail: boolean) => {
  visible.value = true;
  detail.value = isDetail;
  form.${pk.attrName} = '';

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
    form.${childClassName}List = [];
  });

  // 获取${ClassName}信息
  if (id) {
    form.${pk.attrName} = id;
    get${ClassName}Data(id);
  }
};

// 提交表单方法
const onSubmit = async () => {
  loading.value = true; // 防止重复提交
  
  // 表单校验
  const valid = await dataFormRef.value.validate().catch(() => {});
  if (!valid) {
    loading.value = false;
    return false;
  }

  try {
    // 根据是否有ID判断是新增还是修改
    form.${pk.attrName} ? await putObj(form) : await addObj(form);
    useMessage().success(form.${pk.attrName} ? '修改成功' : '添加成功');
    visible.value = false;
    emit('refresh'); // 通知父组件刷新列表
  } catch (err: any) {
    useMessage().error(err.msg);
  } finally {
    loading.value = false;
  }
};

#foreach ($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPkName=$field.attrName)
#end
#end
// 删除子表数据方法
const deleteChild = async (obj: { $childPkName: string }) => {
  if (obj.$childPkName) {
    try {
      await delChildObj([obj.$childPkName]);
      useMessage().success('删除成功');
    } catch (err: any) {
      useMessage().error(err.msg);
    }
  }
};

// ========== 7. 对外暴露 ==========
// 暴露方法给父组件
defineExpose({
  openDialog
});
</script>
