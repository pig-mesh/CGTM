package ${package}.${moduleName}.controller;

#if($queryList)
import cn.hutool.core.util.StrUtil;
#end
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.date.DateUtil;
#set($hasDateRange = false)
#set($hasDateTimeRange = false)
#foreach($field in $queryList)
#if($field.queryFormType == 'date-range')
#set($hasDateRange = true)
#end
#if($field.queryFormType == 'datetime-range')
#set($hasDateTimeRange = true)
#end
#end
#if($hasDateRange)
import java.time.LocalDate;
#end
#if($hasDateTimeRange)
import java.time.LocalDateTime;
#end
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package}.common.core.util.R;
import ${package}.common.log.annotation.SysLog;
#if($opensource)
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
#else
import ${package}.common.excel.annotation.ResponseExcel;
import ${package}.common.excel.annotation.RequestExcel;
#end
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.service.${ClassName}Service;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
#if($isSpringBoot3)
import ${package}.common.security.annotation.HasPermission;
import org.springdoc.core.annotations.ParameterObject;
#else
import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.api.annotations.ParameterObject;
#end
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/${functionName}" )
@Tag(description = "${functionName}" , name = "${tableComment}管理" )
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ${ClassName}Controller {

    private final  ${ClassName}Service ${className}Service;

    /**
     * 分页查询
     * @param page 分页对象
     * @param ${className} ${tableComment}
     * @return
     */
    @Operation(summary = "分页查询" , description = "分页查询" )
    @GetMapping("/page" )
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_view")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_view')" )
    #end
    public R get${ClassName}Page(@ParameterObject Page page, @ParameterObject ${ClassName}Entity ${className}) {
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
#foreach ($field in $queryList)
#set($getAttrName=$str.getProperty($field.attrName))
#set($var="${className}.$getAttrName()")
#if($field.attrType == 'String')
#set($expression="StrUtil.isNotBlank")
#else
#set($expression="Objects.nonNull")
#end
#if($field.queryFormType == 'date-range')
		// ${field.fieldComment}范围查询
		if (ArrayUtil.isNotEmpty(${className}.get${field.attrName.substring(0,1).toUpperCase()}${field.attrName.substring(1)}Range())) {
			String[] range = ${className}.get${field.attrName.substring(0,1).toUpperCase()}${field.attrName.substring(1)}Range();
			if (StrUtil.isNotBlank(range[0])) {
				wrapper.ge(${ClassName}Entity::$getAttrName, LocalDate.parse(range[0]));
			}
			if (StrUtil.isNotBlank(range[1])) {
				wrapper.le(${ClassName}Entity::$getAttrName, LocalDate.parse(range[1]));
			}
		}
#elseif($field.queryFormType == 'datetime-range')
		// ${field.fieldComment}范围查询
		if (ArrayUtil.isNotEmpty(${className}.get${field.attrName.substring(0,1).toUpperCase()}${field.attrName.substring(1)}Range())) {
			String[] range = ${className}.get${field.attrName.substring(0,1).toUpperCase()}${field.attrName.substring(1)}Range();
			if (StrUtil.isNotBlank(range[0])) {
				wrapper.ge(${ClassName}Entity::$getAttrName, LocalDateTime.parse(range[0]));
			}
			if (StrUtil.isNotBlank(range[1])) {
				wrapper.le(${ClassName}Entity::$getAttrName, LocalDateTime.parse(range[1]));
			}
		}
#elseif($field.queryType == '=')
		wrapper.eq($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == 'like' )
		wrapper.like($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == '!-' )
		wrapper.ne($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == '>' )
		wrapper.gt($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == '<' )
		wrapper.lt($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == '>=' )
		wrapper.ge($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == '<=' )
		wrapper.le($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == 'left like' )
		wrapper.likeLeft($expression($var),${ClassName}Entity::$getAttrName,$var);
#elseif( $field.queryType == 'right like' )
		wrapper.likeRight($expression($var),${ClassName}Entity::$getAttrName,$var);
#end
#end
        return R.ok(${className}Service.page(page, wrapper));
    }


    /**
     * 通过条件查询${tableComment}
     * @param ${className} 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_view")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_view')" )
    #end
    public R getDetails(@ParameterObject ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.listDeep(Wrappers.query(${className})));
    }

    /**
     * 新增${tableComment}
     * @param ${className} ${tableComment}
     * @return R
     */
    @Operation(summary = "新增${tableComment}" , description = "新增${tableComment}" )
    @SysLog("新增${tableComment}" )
    @PostMapping
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_add")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_add')" )
    #end
    public R save(@RequestBody ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.saveDeep(${className}));
    }

    /**
     * 修改${tableComment}
     * @param ${className} ${tableComment}
     * @return R
     */
    @Operation(summary = "修改${tableComment}" , description = "修改${tableComment}" )
    @SysLog("修改${tableComment}" )
    @PutMapping
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_edit")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_edit')" )
    #end
    public R updateById(@RequestBody ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.updateDeep(${className}));
    }

    /**
     * 通过id删除${tableComment}
     * @param ids ${pk.attrName}列表
     * @return R
     */
    @Operation(summary = "通过id删除${tableComment}" , description = "通过id删除${tableComment}" )
    @SysLog("通过id删除${tableComment}" )
    @DeleteMapping
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_del")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_del')" )
    #end
    public R removeById(@RequestBody ${pk.attrType}[] ids) {
        return R.ok(${className}Service.removeDeep(ids));
    }

    /**
     * 通过id删除${tableComment}子表数据
     * @param ids ${pk.attrName}列表
     * @return R
     */
    @Operation(summary = "通过id删除${tableComment}子表数据" , description = "通过id删除${tableComment}子表数据" )
    @SysLog("通过id删除${tableComment}子表数据" )
    @DeleteMapping("/child")
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_del")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_del')" )
    #end
    public R removeChild(@RequestBody ${pk.attrType}[] ids) {
        return R.ok(${className}Service.removeChild(ids));
    }

    /**
     * 导出excel 表格
     * @param ${className} 查询条件
   	 * @param ids 导出指定ID
     * @return excel 文件流
     */
    @ResponseExcel
    @GetMapping("/export")
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_export")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_export')" )
    #end
    public List<${ClassName}Entity> exportExcel(${ClassName}Entity ${className},${pk.attrType}[] ids) {
        return ${className}Service.list(Wrappers.lambdaQuery(${className}).in(ArrayUtil.isNotEmpty(ids), ${ClassName}Entity::$str.getProperty($pk.attrName), ids));
    }

    /**
     * 导入excel 表
     * @param ${className}List 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    #if($isSpringBoot3)
    @HasPermission("${moduleName}_${functionName}_export")
    #else
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_export')" )
    #end
    public R importExcel(@RequestExcel List<${ClassName}Entity> ${className}List, BindingResult bindingResult) {
        return R.ok(${className}Service.saveBatch(${className}List));
    }
}
