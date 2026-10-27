#set($hasOrg = false)
#foreach($field in $fieldList)
#if($field.formType.startsWith('org-'))
#set($hasOrg = true)
#end
#end
package ${entityPackage};

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
#if($hasOrg)
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import ${package}.common.core.util.OrgRef;
#end
#if($isTenant)
import ${package}.common.core.util.TenantTable;
#end
#foreach($import in $importList)
#if(!$import.startsWith('java.lang.'))
import $import;
#end
#end
import org.apache.fesod.sheet.annotation.ExcelIgnore;
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
@TableName(value = "${tableName}"#if($hasOrg), autoResultMap = true#end)
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
#if($field.formType.startsWith('org-'))
    @TableField(typeHandler = JacksonTypeHandler.class#if($field.autoFill == 'INSERT' || $field.autoFill == 'INSERT_UPDATE' || $field.autoFill == 'UPDATE'), fill = FieldFill.${field.autoFill}#end)
    @ExcelIgnore
#elseif($field.autoFill == 'INSERT')
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
#if($field.formType.startsWith('org-'))
    private List<OrgRef> $field.attrName;
#elseif($field.formType == 'checkbox')
    private ${field.attrType}[] $field.attrName;
#elseif($field.formType == 'daterange' || $field.formType == 'datetimerange')
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String[] $field.attrName;
#else
    private $field.attrType $field.attrName;
#end
#end
#foreach ($field in $queryList)
#if($field.queryFormType == 'daterange' || $field.queryFormType == 'datetimerange')
#if(${field.fieldComment})#set($comment=${field.fieldComment})#else #set($comment=${field.attrName})#end

	/**
	* $comment 查询范围
	*/
    @TableField(exist = false)
    @Schema(description="$comment 查询范围",hidden=true)
    private String[] ${field.attrName}Range;
#end
#end
    @ExcelIgnore
    @TableField(exist = false)
    @EntityMapping(thisField = "$mainField", joinField = "$childField")
    private List<${ChildClassName}Entity> ${childClassName}List;
}
