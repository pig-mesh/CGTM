package ${package}.${moduleName}.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
#if($isTenant)
import ${package}.common.core.util.TenantTable;
#end
#foreach($import in $importList)
import $import;
#end
import com.alibaba.excel.annotation.ExcelIgnore;
import com.github.yulichang.annotation.EntityMapping;
import java.util.List;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Data
#if($isTenant)
@TenantTable
#end
@TableName("${tableName}")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "${tableComment}")
public class ${ClassName}Entity extends Model<${ClassName}Entity> {

#foreach ($field in $fieldList)
#if(${field.fieldComment})#set($comment=${field.fieldComment})#else #set($comment=${field.attrName})#end

	/**
	* $comment
	*/
#if($field.primaryPk == '1')
    @TableId(type = IdType.ASSIGN_ID)
#end
#if($field.autoFill == 'INSERT')
	@TableField(fill = FieldFill.INSERT)
#elseif($field.autoFill == 'INSERT_UPDATE')
	@TableField(fill = FieldFill.INSERT_UPDATE)
#elseif($field.autoFill == 'UPDATE')
	@TableField(fill = FieldFill.UPDATE)
#end
#if($field.fieldName == 'del_flag')
    @TableLogic
	@TableField(fill = FieldFill.INSERT)
#end
    @Schema(description="$comment"#if($field.hidden),hidden=$field.hidden#end)
#if($field.formType == 'checkbox')
    private ${field.attrType}[] $field.attrName;
#else
    private $field.attrType $field.attrName;
#end    
#end
    @ExcelIgnore
    @TableField(exist = false)
    @EntityMapping(thisField = "$mainField", joinField = "$childField")
    private List<${ChildClassName}Entity> ${childClassName}List;
}
