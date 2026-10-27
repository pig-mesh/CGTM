#set($hasOrg = false)
#foreach($field in $childFieldList)
#if($field.formType.startsWith('org-'))
#set($hasOrg = true)
#end
#end
package ${entityPackage};

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
#if($isChildTenant)
import ${package}.common.core.util.TenantTable;
#end
import lombok.Data;
import lombok.EqualsAndHashCode;
#if($hasOrg)
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import ${package}.common.core.util.OrgRef;
import java.util.List;
#end
#set($childImports = [])
#foreach($field in $childFieldList)
#if($field.packageName && !$field.packageName.startsWith('java.lang.') && !$childImports.contains($field.packageName))
#set($void = $childImports.add($field.packageName))
#end
#end
#foreach($import in $childImports)
import $import;
#end

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Data
#if($isChildTenant)
@TenantTable
#end
@TableName(value = "${childTableName}"#if($hasOrg), autoResultMap = true#end)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "${childTableName}")
public class ${ChildClassName}Entity extends Model<${ChildClassName}Entity> {

#foreach ($field in $childFieldList)
#if(${field.fieldComment})#set($comment=${field.fieldComment})#else #set($comment=${field.attrName})#end
	/**
	* $comment
	*/
#if($field.primaryPk == '1')
	@TableId(type = IdType.ASSIGN_ID)
#end
#if($field.formType.startsWith('org-'))
	@TableField(typeHandler = JacksonTypeHandler.class#if($field.autoFill == 'INSERT' || $field.autoFill == 'INSERT_UPDATE' || $field.autoFill == 'UPDATE'), fill = FieldFill.${field.autoFill}#end)
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
#else
   private $field.attrType $field.attrName;
#end 
#end
}
