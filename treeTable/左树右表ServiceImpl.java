package ${package}.${moduleName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.mapper.${ChildClassName}Mapper;
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * ${tableComment} Service实现类
 *
 * @author ${author}
 * @date ${datetime}
 */
#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPkField = $field)
#set($childPkGetter = $str.getProperty($childPkField.attrName))
#break
#end
#end
#set($parentGetter = $str.getProperty($parentField))
#set($childFieldGetter = $str.getProperty($childField))
#if($childPkField.attrType == 'Long')
#set($treeRootId = '0L')
#elseif($childPkField.attrType == 'String')
#set($treeRootId = '"0"')
#else
#set($treeRootId = '0')
#end
@Service
@RequiredArgsConstructor
public class ${ClassName}ServiceImpl extends ServiceImpl<${ClassName}Mapper, ${ClassName}Entity> implements ${ClassName}Service {

    private final ${ChildClassName}Mapper ${childClassName}Mapper;

    /**
     * 构建树形结构数据
     * @param wrapper 查询条件
     * @return 树形结构数据
     */
    @Override
    public List<Tree<${childPkField.attrType}>> buildTree(LambdaQueryWrapper<${ChildClassName}Entity> wrapper) {
        List<${ChildClassName}Entity> allList = ${childClassName}Mapper.selectList(wrapper);

        if (CollUtil.isEmpty(allList)) {
            return new ArrayList<>();
        }

        List<TreeNode<${childPkField.attrType}>> collect = allList.stream().map(getNodeFunction()).toList();
        return TreeUtil.build(collect, ${treeRootId});
    }

    /**
     * 获取树节点详情
     * @param ${childClassName} 树节点查询对象
     * @return 树节点详情列表
     */
    @Override
    public List<${ChildClassName}Entity> getTreeDetails(${ChildClassName}Entity ${childClassName}) {
        return ${childClassName}Mapper.selectList(Wrappers.query(${childClassName}));
    }

    /**
     * 保存树节点
     * @param ${childClassName} 树节点对象
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveTree(${ChildClassName}Entity ${childClassName}) {
        ${childClassName}.$str.setProperty($parentField)(validateParentRelation(null, ${childClassName}.$str.getProperty($parentField)()));
        return ${childClassName}Mapper.insert(${childClassName}) > 0;
    }

    /**
     * 更新树节点
     * @param ${childClassName} 树节点对象
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTree(${ChildClassName}Entity ${childClassName}) {
        ${childClassName}.$str.setProperty($parentField)(validateParentRelation(${childClassName}.$str.getProperty($childPkField.attrName)(), ${childClassName}.$str.getProperty($parentField)()));
        return ${childClassName}Mapper.updateById(${childClassName}) > 0;
    }

    /**
     * 批量删除树节点
     * @param ids 树节点ID列表
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeTreeByIds(${childPkField.attrType}[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return Boolean.TRUE;
        }

        for (${childPkField.attrType} id : ids) {
            if (hasChildNodes(id)) {
                throw new RuntimeException("当前节点存在子节点，不能删除");
            }
            if (hasMainRecords(id)) {
                throw new RuntimeException("当前节点已关联主表数据，不能删除");
            }
        }

        return ${childClassName}Mapper.deleteBatchIds(CollUtil.toList(ids)) > 0;
    }

    /**
     * 获取TreeNode转换函数
     * @return TreeNode转换函数
     */
    private Function<${ChildClassName}Entity, TreeNode<${childPkField.attrType}>> getNodeFunction() {
        return entity -> {
            TreeNode<${childPkField.attrType}> node = new TreeNode<>();
            node.setId(entity.$childPkGetter());
            node.setName(entity.$str.getProperty($nameField)());
            node.setParentId(normalizeParentId(entity.$parentGetter()));

            Map<String, Object> extra = new HashMap<>();
#foreach($field in $childFieldList)
            extra.put("${field.attrName}", entity.$str.getProperty($field.attrName)());
#end
            node.setExtra(extra);
            return node;
        };
    }

    /**
     * 规范化父节点ID，并校验父子关系
     * @param currentId 当前节点ID
     * @param parentId 父节点ID
     * @return 规范化后的父节点ID
     */
    private ${childPkField.attrType} validateParentRelation(${childPkField.attrType} currentId, ${childPkField.attrType} parentId) {
        ${childPkField.attrType} normalizedParentId = normalizeParentId(parentId);

        if (Objects.nonNull(currentId) && Objects.equals(currentId, normalizedParentId)) {
            throw new RuntimeException("不能选择当前节点作为父节点");
        }

        if (Objects.nonNull(currentId) && isDescendant(currentId, normalizedParentId)) {
            throw new RuntimeException("不能选择当前节点的后代节点作为父节点");
        }

        return normalizedParentId;
    }

    /**
     * 判断候选父节点是否是当前节点的后代节点
     * @param currentId 当前节点ID
     * @param candidateParentId 候选父节点ID
     * @return 是否为后代节点
     */
    private boolean isDescendant(${childPkField.attrType} currentId, ${childPkField.attrType} candidateParentId) {
        if (Objects.isNull(currentId) || Objects.isNull(candidateParentId) || Objects.equals(candidateParentId, ${treeRootId})) {
            return false;
        }

        Set<${childPkField.attrType}> visited = new HashSet<>();
        ${ChildClassName}Entity parent = ${childClassName}Mapper.selectById(candidateParentId);

        while (Objects.nonNull(parent) && visited.add(parent.$childPkGetter())) {
            if (Objects.equals(currentId, parent.$childPkGetter())) {
                return true;
            }

            ${childPkField.attrType} nextParentId = parent.$parentGetter();
            if (Objects.isNull(nextParentId) || Objects.equals(nextParentId, ${treeRootId})) {
                return false;
            }
            parent = ${childClassName}Mapper.selectById(nextParentId);
        }

        return false;
    }

    /**
     * 判断是否存在子节点
     * @param parentId 父节点ID
     * @return 是否存在子节点
     */
    private boolean hasChildNodes(${childPkField.attrType} parentId) {
        LambdaQueryWrapper<${ChildClassName}Entity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(${ChildClassName}Entity::$parentGetter, parentId);
        return ${childClassName}Mapper.selectCount(wrapper) > 0;
    }

    /**
     * 判断是否存在关联主表数据
     * @param treeId 树节点ID
     * @return 是否存在主表数据
     */
    private boolean hasMainRecords(${childPkField.attrType} treeId) {
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(${ClassName}Entity::$childFieldGetter, treeId);
        return baseMapper.selectCount(wrapper) > 0;
    }

    /**
     * 规范化父节点ID
     * @param parentId 父节点ID
     * @return 规范化后的父节点ID
     */
    private ${childPkField.attrType} normalizeParentId(${childPkField.attrType} parentId) {
        return Objects.nonNull(parentId) ? parentId : ${treeRootId};
    }
}
