<template>
    <el-dialog :title="form.${pk.attrName} ? '编辑' : '新增'" v-model="visible" :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px">
#foreach($key in $resultMap.keySet())
#set($itemList = $resultMap.get($key))
<el-row :gutter="24">
#foreach($field in $itemList)
  <el-col :span="$field.span">
#if($field.type == 'input')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-input v-model="form.${field.options.name}" placeholder="${field.options.placeholder}"/>
        </el-form-item>
#elseif($field.type == 'number')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-input-number :min="${field.options.min}" :max="${field.options.max}" v-model="form.${field.options.name}" placeholder="${field.options.placeholder}"></el-input-number>
        </el-form-item>
#elseif($field.type == 'textarea')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-input type="textarea" :rows="${field.options.rows}" v-model="form.${field.options.name}" placeholder="${field.options.placeholder}"/>
        </el-form-item>
#elseif($field.type == 'select')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <el-select v-model="form.${field.options.name}" placeholder="${field.options.placeholder}">
       #if($field.options.optionItemsDictType)
                <el-option :value="item.value" :label="item.label" v-for="(item, index) in ${field.options.optionItemsDictType}" :key="index"></el-option>
       #else
                <el-option label="请选择">0</el-option>
       #end
            </el-select>
        </el-form-item>
#elseif($field.type == 'radio')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <el-radio-group v-model="form.${field.options.name}">
       #if($field.options.optionItemsDictType)
             <el-radio :label="item.value" v-for="(item, index) in ${field.options.optionItemsDictType}" border :key="index">{{ item.label }}
              </el-radio>
       #end
            </el-radio-group>
        </el-form-item>
#elseif($field.type == 'checkbox')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <el-checkbox-group v-model="form.${field.options.name}">
       #if($field.options.optionItemsDictType)
                <el-checkbox :label="item.value" :name="item.label" v-for="(item, index) in ${field.options.optionItemsDictType}" :key="index"></el-checkbox>
       #else
                <el-checkbox label="启用" name="type"></el-checkbox>
                <el-checkbox label="禁用" name="type"></el-checkbox>
       #end
            </<el-checkbox-group>
        </el-form-item>
#elseif($field.type == 'date')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <el-date-picker type="date" placeholder="${field.options.placeholder}" v-model="form.${field.options.name}" :value-format="dateStr"></el-date-picker>
        </el-form-item>
#elseif($field.type == 'time')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <el-time-picker placeholder="${field.options.placeholder}" v-model="form.${field.options.name}" :value-format="dateTimeStr"></el-date-picker>
        </el-form-item>
#elseif($field.type == 'file-upload')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <upload-file  v-model="form.${field.attrName}" limit="${field.options.limit}" fileMaxSize="${field.options.fileMaxSize}"></upload-file>
        </el-form-item>
#elseif($field.type == 'picture-upload')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <upload-img v-model:imageUrl="form.${field.options.name}" limit="${field.options.limit}" fileMaxSize="${field.options.fileMaxSize}"></upload-img>
        </el-form-item>
#elseif($field.type == 'rich-editor')
          <el-form-item label="${field.options.label}" prop="${field.options.name}">
            <editor v-model:get-html="form.${field.options.name}" placeholder="${field.options.placeholder}"></editor>
          </el-form-item>
#elseif($field.type == 'switch')
          <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-switch v-model="form.${field.options.name}" />
          </el-form-item>
#elseif($field.type == 'rate')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-rate v-model="form.${field.options.name}" />
      </el-form-item>
#elseif($field.type == 'slider')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-slider v-model="form.${field.options.name}" />
      </el-form-item>
#elseif($field.type == 'color')
        <el-form-item label="${field.options.label}" prop="${field.options.name}">
          <el-color-picker v-model="form.${field.options.name}" />
      </el-form-item>
#elseif($field.type == 'static-text' || $field.type == 'html-text')
        <span>{{form.${field.options.name}}}</span>          
#elseif($field.type == 'divider')
      <el-divider />
#else
      <el-form-item label="${field.options.label}" prop="${field.options.name}">
        <el-input v-model="form.${field.options.name}" placeholder="${field.options.placeholder}"/>
      </el-form-item>
#end
  </el-col>
#end
</el-row>
#end
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false" formDialogRef>取消</el-button>
          <el-button type="primary" @click="onSubmit" formDialogRef>确认</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script setup lang="ts" name="${ClassName}Dialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj } from '/@/api/${moduleName}/${functionName}'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
// 定义字典
#set($fieldDict=[])
#foreach($key in $resultMap.keySet())
#set($itemList = $resultMap.get($key))
#foreach($field in $itemList)
   #if($field.options.optionItemsDictType)
        #set($void=$fieldDict.add($field.options.optionItemsDictType))
    #end
#end
#end
#if($fieldDict)
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict))
#end

// 提交表单数据
const form = reactive({
		${pk.attrName}:"",
#foreach($key in $resultMap.keySet())
#set($itemList = $resultMap.get($key))
#foreach($field in $itemList)
    ${field.options.name}: "",
#end
#end
});

// 定义校验规则
const dataRules = ref({
#foreach($key in $resultMap.keySet())
#set($itemList = $resultMap.get($key))
#foreach($field in $itemList)
#if($field.options.required && $field.options.validation)
    ${field.options.name}: [{required: true, message: '${field.options.label}不能为空', trigger: 'blur'}, {{ validator: rule.${field.options.validation}, trigger: 'blur' }],
#elseif($field.options.required)
    ${field.options.name}: [{required: true, message: '${field.options.label}不能为空', trigger: 'blur'}],
#elseif($field.options.validation)
   ${field.options.name}: [{ validator: rule.${field.options.validation}, trigger: 'blur' }],
#end
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
    get${className}Data(id)
  }
};

// 提交
const onSubmit = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false
    }

    // 更新
    if (form.${pk.attrName}) {
      putObj(form).then(() => {
        useMessage().success('修改成功')
        visible.value = false // 关闭弹窗
        emit('refresh')
      }).catch((err: any) => {
        useMessage().error(err.msg)
      })
    } else {
      addObj(form).then(() => {
        useMessage().success('添加成功')
        visible.value = false // 关闭弹窗
        emit('refresh')
      }).catch((err: any) => {
        useMessage().error(err.msg)
      })
    }
  })
}

// 初始化表单数据
const get${className}Data = (id: string) => {
  // 获取数据
  getObj(id).then((res: any) => {
    Object.assign(form, res.data)
  })
};

// 暴露变量
defineExpose({
  openDialog
});
</script>