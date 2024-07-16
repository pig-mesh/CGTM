package ${package}.${moduleName}.controller;

#if($queryList)
import cn.hutool.core.util.StrUtil;
#end
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
#if($opensource)
import ${package}.common.core.util.R;
import ${package}.log.annotation.SysLog;
import com.pig4cloud.plugin.excel.annotation.ResponseExcel;
#else
import ${package}.common.core.util.R;
import ${package}.common.log.annotation.SysLog;
import ${package}.common.excel.annotation.ResponseExcel;
#end
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.service.${ClassName}Service;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
#if($isSpringBoot3)
import org.springdoc.core.annotations.ParameterObject;
#else
import org.springdoc.api.annotations.ParameterObject;
#end
import org.springframework.http.HttpHeaders;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_view')" )
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
#if($field.queryType == '=')
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_view')" )
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_add')" )
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_edit')" )
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_del')" )
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_del')" )
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
    @PreAuthorize("@pms.hasPermission('${moduleName}_${functionName}_export')" )
    public List<${ClassName}Entity> export(${ClassName}Entity ${className},${pk.attrType}[] ids) {
        return ${className}Service.list(Wrappers.lambdaQuery(${className}).in(ArrayUtil.isNotEmpty(ids), ${ClassName}Entity::$str.getProperty($pk.attrName), ids));
    }
}
