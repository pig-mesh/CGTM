package ${package}.${moduleName}.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

#if($isTenant)
import com.pig4cloud.pigx.common.data.tenant.TenantEntity;
#end

#foreach($pkg in $importList)
import $pkg;
#end

import java.time.LocalDateTime;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Data
@TableName("${tableName}")
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Schema(description = "${tableComment}")
#if($isTenant)
public class ${ClassName}Entity extends TenantEntity {
#else
public class ${ClassName}Entity extends Model<${ClassName}Entity> {
#end

#foreach ($field in $fieldList)
#if($field.primaryPk)
	/**
	 * $field.fieldComment
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@Schema(description = "$field.fieldComment")
	private $field.attrType $field.attrName;

#end
#end
#foreach ($field in $fieldList)
#if(!$field.primaryPk && !$field.baseField)
	/**
	 * $field.fieldComment
	 */
#if($field.fieldComment == '父级ID')
	@Schema(description = "$field.fieldComment")
	private $field.attrType parentId;

#elseif($field.attrName == 'sort')
	@Schema(description = "排序")
	private Integer sort;

#else
#if($field.autoFill == 'INSERT')
	@TableField(fill = FieldFill.INSERT)
#elseif($field.autoFill == 'INSERT_UPDATE')
	@TableField(fill = FieldFill.INSERT_UPDATE)
#elseif($field.autoFill == 'UPDATE')
	@TableField(fill = FieldFill.UPDATE)
#end
	@Schema(description = "$field.fieldComment"#if($field.hidden), hidden = $field.hidden#end)
	private $field.attrType $field.attrName;

#end
#end
#end

	/**
	 * 子节点列表
	 */
	@TableField(exist = false)
	@Schema(description = "子节点列表", hidden = true)
	private java.util.List<${ClassName}Entity> children;

	/**
	 * 是否有子节点
	 */
	@TableField(exist = false)
	@Schema(description = "是否有子节点", hidden = true)
	private Boolean hasChildren;

} 