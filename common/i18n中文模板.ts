/**
 * ${tableComment}模块 - 中文语言包
 */
export default {
   ${functionName}: {
        // 基础文本
        index: '#',
        import${className}Tip: '导入${tableComment}',
#foreach($field in $fieldList)
        // 字段名称
        ${field.attrName}: '#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end',
#end
#foreach($field in $fieldList)
        // 输入提示
        input$str.pascalCase(${field.attrName})Tip: '请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end',
#end
    }
}
