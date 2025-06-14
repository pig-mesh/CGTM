/**
 * ${tableComment} Module - English Language Pack
 */
export default {
   ${functionName}: {
        // Basic Text
        index: '#',
        import${className}Tip: 'Import ${ClassName}',
#foreach($field in $fieldList)
        // Field Names
        ${field.attrName}: '${field.attrName}',
#end
#foreach($field in $fieldList)
        // Input Tips
        input$str.pascalCase(${field.attrName})Tip: 'Please input ${field.attrName}',
#end
    }
}
