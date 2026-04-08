#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPkField = $field)
#break
#end
#end
#set($relationFieldLabel = $childField)
#foreach($field in $fieldList)
#if($field.attrName == $childField)
#if($field.fieldComment)
#set($relationFieldLabel = $field.fieldComment)
#else
#set($relationFieldLabel = $field.attrName)
#end
#break
#end
#end
#set($fieldDict=[])
#foreach($field in $formList)
#if($field.fieldDict)
#set($void=$fieldDict.add($field.fieldDict))
#end
#end
<template>
  <el-dialog :title="form.${pk.attrName} ? '编辑' : '新增'" v-model="visible"
    :close-on-click-modal="false" draggable>
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" label-width="90px" v-loading="loading">
      <el-row :gutter="24">
#if($formLayout == 1)
        <el-col :span="24" class="mb20">
#elseif($formLayout == 2)
        <el-col :span="12" class="mb20">
#end
          <el-form-item label="${relationFieldLabel}" prop="${childField}">
            <el-tree-select
              v-model="form.${childField}"
              :data="treeNodes"
              :props="treeSelectProps"
              check-strictly
              :render-after-expand="false"
              placeholder="请选择${relationFieldLabel}"
              style="width: 100%"
              clearable
              filterable
            />
          </el-form-item>
        </el-col>
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${childField})
#if($formLayout == 1)
        <el-col :span="24" class="mb20">
#elseif($formLayout == 2)
        <el-col :span="12" class="mb20">
#end
#if($field.formType == 'text')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"/>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'textarea')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-input type="textarea" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"/>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'select')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-select v-model="form.${field.attrName}" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end">
#if($field.fieldDict)
              <el-option :value="item.value" :label="item.label" v-for="(item, index) in ${field.fieldDict}" :key="index"></el-option>
#else
              <el-option label="请选择" value="0"></el-option>
#end
            </el-select>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'radio')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
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
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
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
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker type="date" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" v-model="form.${field.attrName}" :value-format="dateStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'datetime')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker type="datetime" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" v-model="form.${field.attrName}" :value-format="dateTimeStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'daterange')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" v-model="form.${field.attrName}" :value-format="dateStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'datetimerange')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" v-model="form.${field.attrName}" :value-format="dateTimeStr"></el-date-picker>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'number')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-input-number :min="1" :max="1000" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"></el-input-number>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'upload-file')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <upload-file v-model="form.${field.attrName}"></upload-file>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'upload-img')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <upload-img v-model:imageUrl="form.${field.attrName}"></upload-img>
          </el-form-item>
        </el-col>
#elseif($field.formType == 'editor')
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <editor v-if="visible" v-model:get-html="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"></editor>
          </el-form-item>
        </el-col>
#else
          <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end" prop="${field.attrName}">
            <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else${field.attrName}#end"/>
          </el-form-item>
        </el-col>
#end
#end
#end
      </el-row>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="${ClassName}Dialog">
import { useMessage } from "/@/hooks/message";
import { addObj, fetchTreeList, getObj, putObj, validateExist } from '/@/api/${moduleName}/${functionName}';
#if($fieldDict && $fieldDict.size() > 0)
import { useDict } from '/@/hooks/dict';
#end
#foreach($field in $formList)
#if($field.formValidator && $field.formValidator != 'duplicate')
import { rule } from '/@/utils/validate';
#break
#end
#end

interface TreeNode {
  [key: string]: any;
  children?: TreeNode[];
}

interface FormData {
  ${pk.attrName}?: string | number;
  ${childField}?: string | number | null;
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${childField})
#if($field.formType == 'number')
  ${field.attrName}: number;
#elseif($field.formType == 'checkbox' || $field.formType == 'daterange' || $field.formType == 'datetimerange')
  ${field.attrName}: any[];
#else
  ${field.attrName}: string;
#end
#end
#end
}

const emit = defineEmits(['refresh']);
const dataFormRef = ref();
const visible = ref(false);
const loading = ref(false);
const treeNodes = ref<TreeNode[]>([]);
const currentTreeId = ref<string | number | null>(null);

const treeSelectProps = {
  children: 'children',
  label: '${nameField}',
  value: '${childPkField.attrName}',
  checkStrictly: true
};

const form = reactive<FormData>({
  ${pk.attrName}: '', // 主键
  ${childField}: null,
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${childField})
#if($field.formType == 'number')
  ${field.attrName}: 0, // ${field.fieldComment}
#elseif($field.formType == 'checkbox' || $field.formType == 'daterange' || $field.formType == 'datetimerange')
  ${field.attrName}: [], // ${field.fieldComment}
#else
  ${field.attrName}: '', // ${field.fieldComment}
#end
#end
#end
});

#if($fieldDict && $fieldDict.size() > 0)
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

const dataRules = ref({
  ${childField}: [
    {
      required: true,
        validator: (rule: any, value: any, callback: any) => {
        if (value === null || value === undefined || value === '') {
          callback(new Error('请选择${relationFieldLabel}'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ],
#foreach($field in $formList)
#if($field.attrName != ${childField} && $field.formRequired == '1' && $field.formValidator == 'duplicate')
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        validateExist(rule, value, callback, form.${pk.attrName} !== '');
      },
      trigger: 'blur',
    }
  ],
#elseif($field.attrName != ${childField} && $field.formRequired == '1' && $field.formValidator)
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' },
    { validator: rule.${field.formValidator}, trigger: 'blur' }
  ],
#elseif($field.attrName != ${childField} && $field.formRequired == '1')
  ${field.attrName}: [
    { required: true, message: '${field.fieldComment}不能为空', trigger: 'blur' }
  ],
#elseif($field.attrName != ${childField} && $field.formValidator)
  ${field.attrName}: [
    { validator: rule.${field.formValidator}, trigger: 'blur' }
  ],
#end
#end
});

const get${ClassName}Data = async (id: string) => {
  try {
    loading.value = true;
    const { data } = await getObj({ ${pk.attrName}: id });
    if (data && data.length > 0) {
      Object.assign(form, data[0]);
    }
  } catch (error) {
    useMessage().error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

const loadTreeNodes = async () => {
  const { data } = await fetchTreeList();
  treeNodes.value = data || [];
};

const openDialog = async (id?: string, treeId?: string | number) => {
  visible.value = true;
  currentTreeId.value = treeId ?? null;
  form.${pk.attrName} = '';

  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  await loadTreeNodes();
  form.${childField} = treeId ?? null;

  if (id) {
    form.${pk.attrName} = id;
    await get${ClassName}Data(id);
  }
};

const onSubmit = async () => {
  loading.value = true;

  const valid = await dataFormRef.value.validate().catch(() => {});
  if (!valid) {
    loading.value = false;
    return false;
  }

  try {
    form.${childField} = form.${childField} ?? null;
    form.${pk.attrName} ? await putObj(form) : await addObj(form);
    useMessage().success(form.${pk.attrName} ? '修改成功' : '添加成功');
    visible.value = false;
    emit('refresh');
  } catch (error: any) {
    useMessage().error(error.msg || '操作失败');
  } finally {
    loading.value = false;
  }
};

defineExpose({
  openDialog
});
</script>
