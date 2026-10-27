## 查询条件按需导入：字符串字段用 StrUtil，其余类型用 Objects；复选框与日期范围两者都不用
#set($needStrUtil = false)
#set($needObjects = false)
#foreach($field in $queryList)
#if($field.formType == 'checkbox' || $field.queryFormType == 'daterange' || $field.queryFormType == 'datetimerange')
#elseif($field.formType.startsWith('org-') || $field.attrType == 'String')
#set($needStrUtil = true)
#else
#set($needObjects = true)
#end
#end
#set($hasOrgQuery = false)
#foreach($field in $queryList)
#if($field.formType.startsWith('org-'))
#set($hasOrgQuery = true)
#end
#end
## 组织字段以 [{id,name}] JSON 存储，按 ${attrName}Keyword 对整列模糊查询，分页和导出共用
#macro(orgKeywordParams)#foreach($field in $queryList)#if($field.formType.startsWith('org-')), @RequestParam(required = false) String ${field.attrName}Keyword#end#end#end
#macro(orgKeywordDecl)#foreach($field in $queryList)#if($field.formType.startsWith('org-')), String ${field.attrName}Keyword#end#end#end
#macro(orgKeywordArgs)#foreach($field in $queryList)#if($field.formType.startsWith('org-')), ${field.attrName}Keyword#end#end#end
#macro(orgKeywordFilters)
#if($hasOrgQuery)
        // 组织字段对整列 JSON 做 LIKE：关键字为数字或 id/name 时会误命中，需精确匹配时改用数据库 JSON 函数
#end
#foreach($field in $queryList)
#if($field.formType.startsWith('org-'))
        wrapper.like(StrUtil.isNotBlank(${field.attrName}Keyword), ${ClassName}Entity::$str.getProperty($field.attrName), ${field.attrName}Keyword);
#end
#end
#end
package ${package}.${moduleName}.controller;

#if($queryList)
#if($needStrUtil)
import cn.hutool.core.util.StrUtil;
#end
#end
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ${package}.common.core.util.R;
import ${package}.common.log.annotation.SysLog;
#if($opensource)
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
import com.pig4cloud.plugin.excel.annotation.RequestExcel;
#else
import ${package}.common.excel.annotation.ResponseExcel;
import ${package}.common.excel.annotation.RequestExcel;
#end
import ${entityClassName};
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
#if($needObjects)
import java.util.Objects;
#end

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
     * 获取树形列表
     * @param ${className} ${tableComment}
     * @return
     */
    @Operation(summary = "获取树形列表" , description = "获取树形列表" )
    @GetMapping("/tree" )
    #if($isSpringBoot3)
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_view")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_view')" )
    #end
    public R get${ClassName}Tree(@ParameterObject ${ClassName}Entity ${className}#orgKeywordParams()) {
        return R.ok(${className}Service.buildTree(buildQueryWrapper(${className}#orgKeywordArgs())));
    }

    /**
     * 列表与导出共用的查询条件，保证导出结果与列表一致
     */
    private LambdaQueryWrapper<${ClassName}Entity> buildQueryWrapper(${ClassName}Entity ${className}#orgKeywordDecl()) {
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
#orgKeywordFilters()
#foreach ($field in $queryList)
#if(!$field.formType.startsWith('org-'))
#set($getAttrName=$str.getProperty($field.attrName))
#set($var="${className}.$getAttrName()")
#if($field.attrType == 'String')
#set($expression="StrUtil.isNotBlank")
#else
#set($expression="Objects.nonNull")
#end
#if($field.formType == 'checkbox')
		if (ArrayUtil.isNotEmpty($var)) {
			// 复选框按逗号分隔存储，选中的每个值都需包含；取值互为子串时会误命中
			for (${field.attrType} item : $var) {
				wrapper.like(${ClassName}Entity::$getAttrName, item);
			}
		}
#elseif($field.queryFormType == 'daterange' || $field.queryFormType == 'datetimerange')
		if (ArrayUtil.isNotEmpty(${className}.${getAttrName}Range())) {
            wrapper.between(${ClassName}Entity::$getAttrName, ${className}.${getAttrName}Range()[0], ${className}.${getAttrName}Range()[1]);
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
#end
        return wrapper;
    }

    /**
     * 通过条件查询${tableComment}
     * @param ${className} 查询条件
     * @return R  对象列表
     */
    @Operation(summary = "通过条件查询" , description = "通过条件查询对象" )
    @GetMapping("/details" )
    #if($isSpringBoot3)
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_view")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_view')" )
    #end
    public R getDetails(@ParameterObject ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.list(Wrappers.query(${className})));
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
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_add")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_add')" )
    #end
    public R save(@RequestBody ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.save(${className}));
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
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_edit")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_edit')" )
    #end
    public R updateById(@RequestBody ${ClassName}Entity ${className}) {
        return R.ok(${className}Service.updateById(${className}));
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
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_del")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_del')" )
    #end
    public R removeById(@RequestBody ${pk.attrType}[] ids) {
        return R.ok(${className}Service.removeBatchByIds(CollUtil.toList(ids)));
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
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_export")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_export')" )
    #end
    public List<${ClassName}Entity> exportExcel(${ClassName}Entity ${className},${pk.attrType}[] ids#orgKeywordParams()) {
        return ${className}Service.list(buildQueryWrapper(${className}#orgKeywordArgs()).in(ArrayUtil.isNotEmpty(ids), ${ClassName}Entity::$str.getProperty($pk.attrName), ids));
    }

    /**
     * 导入excel 表
     * @param ${className}List 对象实体列表
     * @param bindingResult 错误信息列表
     * @return ok fail
     */
    @PostMapping("/import")
    #if($isSpringBoot3)
    @HasPermission("$str.lowerCase($moduleName)_$str.lowerCase($functionName)_export")
    #else
    @PreAuthorize("@pms.hasPermission('$str.lowerCase($moduleName)_$str.lowerCase($functionName)_export')" )
    #end
    public R importExcel(@RequestExcel List<${ClassName}Entity> ${className}List, BindingResult bindingResult) {
        return R.ok(${className}Service.saveBatch(${className}List));
    }
} 
