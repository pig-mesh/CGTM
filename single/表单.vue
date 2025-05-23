<template>
    <el-dialog :title="form.${pk.attrName} ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName})
#if($formLayout == 1)
    <el-col :span="24" class="mb20">
#elseif($formLayout == 2)
    <el-col :span="12" class="mb20">
#end
#if($field.formType == 'text')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
        <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end"/>
      </el-form-item>
      </el-col>
#elseif($field.formType == 'textarea')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
        <el-input type="textarea" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end"/>
      </el-form-item>
      </el-col>
#elseif($field.formType == 'select')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
          <el-select v-model="form.${field.attrName}" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end">
     #if($field.fieldDict)
            <el-option :value="item.value" :label="item.label" v-for="(item, index) in ${field.fieldDict}" :key="index"></el-option>
       #end
     #if(!$field.fieldDict)
            <el-option label="请选择">0</el-option>
       #end
          </el-select>
        </el-form-item>
      </el-col>
#elseif($field.formType == 'radio')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
            <el-radio-group v-model="form.${field.attrName}">
     #if($field.fieldDict)
             <el-radio :label="item.value" v-for="(item, index) in ${field.fieldDict}" border :key="index">{{ item.label }}
            </el-radio>
       #else
           <el-radio label="${field.fieldComment}" border>${field.fieldComment}</el-radio>
       #end
            </el-radio-group>
        </el-form-item>
      </el-col>
#elseif($field.formType == 'checkbox')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
            <el-checkbox-group v-model="form.${field.attrName}">
     #if($field.fieldDict)
						<el-checkbox :label="item.value" v-for="(item, index) in ${field.fieldDict}" :key="index">{{ item.label }}</el-checkbox>
       #end
     #if(!$field.fieldDict)
                <el-checkbox label="启用" name="type"></el-checkbox>
                <el-checkbox label="禁用" name="type"></el-checkbox>
       #end
            </el-checkbox-group>
        </el-form-item>
      </el-col>
#elseif($field.formType == 'date')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
      <el-date-picker type="date" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" v-model="form.${field.attrName}" :value-format="dateStr"></el-date-picker>
      </el-form-item>
      </el-col>
#elseif($field.formType == 'datetime')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
            <el-date-picker type="datetime" placeholder="请选择#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" v-model="form.${field.attrName}" :value-format="dateTimeStr"></el-date-picker>
      </el-form-item>
      </el-col>

#elseif($field.formType == 'number')
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
        <el-input-number :min="1" :max="1000" v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end"></el-input-number>
      </el-form-item>
    </el-col>
#elseif($field.formType == 'upload-file')
  <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
    <upload-file v-model="form.${field.attrName}"></upload-file>
  </el-form-item>
  </el-col>
#elseif($field.formType == 'upload-img')
  <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
    <upload-img v-model:imageUrl="form.${field.attrName}"></upload-img>
  </el-form-item>
  </el-col>
#elseif($field.formType == 'editor')
  <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${field.attrName}">
    <editor v-if="visible" v-model:get-html="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end"></editor>
  </el-form-item>
  </el-col>
#end

#if(!$field.formType)
      <el-form-item label="#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end" prop="${column.attrName}">
        <el-input v-model="form.${field.attrName}" placeholder="请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end"/>
      </el-form-item>
    </el-col>
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
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/${moduleName}/${functionName}'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典
#set($fieldDict=[])
#foreach($field in $gridList)
	#if($field.fieldDict)
		#set($void=$fieldDict.add($field.fieldDict))
	#end
#end
#if($fieldDict)
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict))
#end

// 提交表单数据
const form = reactive({
#if(!$formList.contains(${pk.attrName}))
		${pk.attrName}:'',
#end
#foreach($field in $formList)
#if($field.formType == 'number')
		${field.attrName}: 0,
#elseif($field.formType == 'checkbox')
    ${field.attrName}: [],
#else
	  ${field.attrName}: '',
#end
#end
});

// 定义校验规则
const dataRules = ref({
#foreach($field in $formList)
#if($field.formRequired == '1' && $field.formValidator == 'duplicate')
    ${field.attrName}: [{required: true, message: '${field.fieldComment}不能为空', trigger: 'blur'}, {
      validator: (rule: any, value: any, callback: any) => {
        validateExist(rule, value, callback, form.${pk.attrName} !== '');
      },
      trigger: 'blur',
    }],
#elseif($field.formRequired == '1' && $field.formValidator)
    ${field.attrName}: [{required: true, message: '${field.fieldComment}不能为空', trigger: 'blur'}, { validator: rule.${field.formValidator}, trigger: 'blur' }],
#elseif($field.formRequired == '1')
    ${field.attrName}: [{required: true, message: '${field.fieldComment}不能为空', trigger: 'blur'}],
#elseif($field.formValidator)
    ${field.attrName}: [{ validator: rule.${field.formValidator}, trigger: 'blur' }],
#end
#end
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.${pk.attrName} = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取${className}信息
  if (id) {
    form.${pk.attrName} = id
    get${ClassName}Data(id)
  }
};

// 提交
const onSubmit = async () => {
	loading.value = true; // 防止重复提交
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) {
		loading.value = false;
		return false;
	}

	try {
		form.${pk.attrName} ? await putObj(form) : await addObj(form);
		useMessage().success(form.${pk.attrName} ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const get${ClassName}Data = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({${pk.attrName}: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};

// 暴露变量
defineExpose({
  openDialog
});
</script>
