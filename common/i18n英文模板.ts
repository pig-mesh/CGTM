export default {
   ${functionName}: {
        index: '#',
        import${className}Tip: 'import ${ClassName}',
#foreach($field in $fieldList)
        ${field.attrName}: '${field.attrName}',
#end
#foreach($field in $fieldList)
        input$str.pascalCase(${field.attrName})Tip: 'input ${field.attrName}',
#end
    }
}
