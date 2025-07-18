<template>
  <el-dialog :title="form.${pk.attrName} ? '编辑' : '新增'" v-model="visible"
    :close-on-click-modal="false" draggable destroy-on-close>
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
      <el-row :gutter="24">
        <!-- 父级节点选择 -->
#if($formLayout == 1)
        <el-col :span="24" class="mb20">
#elseif($formLayout == 2)
        <el-col :span="12" class="mb20">
#end
          <el-form-item label="父级节点" prop="${parentField}">
            <el-tree-select
              v-model="form.${parentField}"
              :data="parentNodes"
              :props="treeSelectProps"
              check-strictly
              :render-after-expand="false"
              placeholder="请选择父级节点"
              style="width: 100%"
              clearable
            />
          </el-form-item>
        </el-col>
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${parentField})
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

<script setup lang="ts" name="${ClassName}TreeDialog">
// ========== 导入语句 ==========
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, getParentNodes } from '/@/api/${moduleName}/${functionName}';
#if($fieldDict && $fieldDict.size() > 0)
import { useDict } from '/@/hooks/dict';
#end
#foreach($field in $formList)
#if($field.formValidator && $field.formValidator != 'duplicate')
import { rule } from '/@/utils/validate';
#break
#end
#end

// ========== 类型定义 ==========
interface TreeNode {
  ${pk.attrName}: string | number | null;
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${parentField})
#set($nameField = $field)
#break
#end
#end
#if($nameField)
  ${nameField.attrName}: string;
#end
  children?: TreeNode[];
}

interface FormData {
#if(!$formList.contains(${pk.attrName}))
  ${pk.attrName}?: string;
#end
  ${parentField}?: string | number | null;
#foreach($field in $formList)
#if($field.attrName != ${parentField})
#if($field.formType == 'number')
  ${field.attrName}: number;
#elseif($field.formType == 'checkbox')
  ${field.attrName}: any[];
#else
  ${field.attrName}: string;
#end
#end
#end
}

// ========== 组件定义 ==========
const emit = defineEmits(['refresh']);

// ========== 响应式数据定义 ==========
const dataFormRef = ref(); // 表单引用
const visible = ref(false); // 弹窗显示状态
const loading = ref(false); // 加载状态
const parentNodes = ref<TreeNode[]>([]); // 父级节点数据

// 树形选择器配置
const treeSelectProps = {
  children: 'children',
#foreach($field in $formList)
#if($field.attrName != ${pk.attrName} && $field.attrName != ${parentField})
  label: '${field.attrName}',
#break
#end
#end
  value: '${pk.attrName}',
  checkStrictly: true
};

// 表单数据对象
const form = reactive<FormData>({
#if(!$formList.contains(${pk.attrName}))
  ${pk.attrName}: '', // 主键
#end
  ${parentField}: null, // 父级ID
#foreach($field in $formList)
#if($field.attrName != ${parentField})
#if($field.formType == 'number')
  ${field.attrName}: 0, // ${field.fieldComment}
#elseif($field.formType == 'checkbox')
  ${field.attrName}: [], // ${field.fieldComment}
#else
  ${field.attrName}: '', // ${field.fieldComment}
#end
#end
#end
});

// ========== 字典数据处理 ==========
#set($fieldDict=[])
#foreach($field in $formList)
#if($field.fieldDict)
#set($void=$fieldDict.add($field.fieldDict))
#end
#end
#if($fieldDict && $fieldDict.size() > 0)
// 加载字典数据
const { $dict.format($fieldDict) } = useDict($dict.quotation($fieldDict));
#end

// ========== 表单校验规则 ==========
const dataRules = ref({
  ${parentField}: [
    { 
      required: true, 
      validator: (rule: any, value: any, callback: any) => {
        if (value === null || value === undefined || value === '') {
          callback(new Error('请选择父级节点'));
        } else {
          callback();
        }
      },
      trigger: 'change' 
    }
  ],
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

// ========== 方法定义 ==========
/**
 * 获取详情数据
 */
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

/**
 * 获取父级节点数据
 */
const getParentNodesList = async () => {
  try {
    const { data } = await getParentNodes();
    // 添加根节点选项
    parentNodes.value = [
      { ${pk.attrName}: 0, ${nameField.attrName}: '根节点', children: [] },
      ...(data || [])
    ];
  } catch (error) {
    console.error('获取父级节点失败:', error);
    parentNodes.value = [{ ${pk.attrName}: 0, ${nameField.attrName}: '根节点', children: [] }];
  }
};

/**
 * 打开弹窗方法
 * @param id 编辑时的数据ID
 * @param parentId 新增时的父级ID
 */
const openDialog = async (id?: string, parentId?: string | number) => {
  form.${pk.attrName} = '';
  visible.value = true;
  form.${parentField} = parentId || '0';

  // 初始化父级节点数据
  const { data } = await getParentNodes();
  parentNodes.value = [{ ${pk.attrName}: '0', ${nameField.attrName}: '根节点', children: data }];

  // 重置表单验证
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  if (id) {
    form.${pk.attrName} = id;
    await get${ClassName}Data(id);
  }
};

/**
 * 提交表单方法
 */
const onSubmit = async () => {
  // 防止重复提交
  if (loading.value) return;
  loading.value = true;
  
  try {
    const valid = await dataFormRef.value.validate().catch(() => {});
		if (!valid) {
			loading.value = false;
			return false;
    }

    // 处理父级ID，如果选择根节点(null)则设为null，其他情况保持原值
    if (form.${pk.attrName}) {
      await putObj(form);
      useMessage().success('修改成功');
    } else {
      await addObj(form);
      useMessage().success('添加成功');
    }
    
    visible.value = false;
    emit('refresh'); // 通知父组件刷新列表
  } catch (err: any) {
    useMessage().error(err.msg || '操作失败');
  } finally {
    loading.value = false;
  }
};

// ========== 对外暴露 ==========
defineExpose({
  openDialog
});
</script> 