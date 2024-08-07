package ${package}.${moduleName}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import org.springframework.stereotype.Service;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.mapper.${ChildClassName}Mapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.Objects;

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
#set($getChildPkName=$str.getProperty(${pk.attrName}))
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
    public Boolean removeDeep(Long[] ids) {
        baseMapper.deleteBatchIds(CollUtil.toList(ids));
        ${childClassName}Mapper.delete(Wrappers.<${ChildClassName}Entity>lambdaQuery().in(${ChildClassName}Entity::$str.getProperty($childField), ids));
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeChild(Long[] ids) {
        ${childClassName}Mapper.deleteBatchIds(CollUtil.toList(ids));
        return Boolean.TRUE;
    }
}
