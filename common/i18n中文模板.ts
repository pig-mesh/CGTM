export default {
   ${functionName}: {
        index: '#',
        import${className}Tip: '导入${tableComment}',
#foreach($field in $fieldList)
        ${field.attrName}: '#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end',
#end
#foreach($field in $fieldList)
        input$str.pascalCase(${field.attrName})Tip: '请输入#if(${field.fieldComment})${field.fieldComment}#else ${field.attrName}#end',
#end
    }
}
