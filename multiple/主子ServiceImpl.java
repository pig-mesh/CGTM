#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPk = $field)
#end
#end
package ${package}.${moduleName}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${entityClassName};
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import org.springframework.stereotype.Service;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ${entityPackage}.${ChildClassName}Entity;
import ${package}.${moduleName}.mapper.${ChildClassName}Mapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Service
@RequiredArgsConstructor
public class ${ClassName}ServiceImpl extends ServiceImpl<${ClassName}Mapper, ${ClassName}Entity> implements ${ClassName}Service {

  private final ${ChildClassName}Mapper ${childClassName}Mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveDeep(${ClassName}Entity ${className}) {
        baseMapper.insert(${className});
        for (${ChildClassName}Entity  ${childClassName} : ${className}.get${ChildClassName}List()) {
            ${childClassName}.$str.setProperty($childField)(${className}.$str.getProperty($mainField)());
            ${childClassName}Mapper.insert( ${childClassName});
        }

        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDeep(${ClassName}Entity ${className}) {
        baseMapper.updateById(${className});
        for (${ChildClassName}Entity  ${childClassName} : ${className}.get${ChildClassName}List()) {
#set($getChildPkName=$str.getProperty(${childPk.attrName}))
            if (Objects.isNull(${childClassName}.$getChildPkName())) {
                ${childClassName}.$str.setProperty($childField)(${className}.$str.getProperty($mainField)());
                ${childClassName}Mapper.insert(${childClassName});
            } else {
                ${childClassName}Mapper.updateById(${childClassName});
            }
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeDeep(${pk.attrType}[] ids) {
        List<${ClassName}Entity> records = baseMapper.selectBatchIds(CollUtil.toList(ids));
        if (CollUtil.isEmpty(records)) {
            return Boolean.TRUE;
        }
        ${childClassName}Mapper.delete(Wrappers.<${ChildClassName}Entity>lambdaQuery()
                .in(${ChildClassName}Entity::$str.getProperty($childField), records.stream()
                        .map(${ClassName}Entity::$str.getProperty($mainField)).collect(Collectors.toList())));
        baseMapper.deleteBatchIds(CollUtil.toList(ids));
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeChild(${childPk.attrType}[] ids) {
        ${childClassName}Mapper.deleteBatchIds(CollUtil.toList(ids));
        return Boolean.TRUE;
    }
}
