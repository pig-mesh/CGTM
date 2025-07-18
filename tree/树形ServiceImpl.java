package ${package}.${moduleName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ${tableComment} Service实现类
 *
 * @author ${author}
 * @date ${datetime}
 */
@Service
@RequiredArgsConstructor
public class ${ClassName}ServiceImpl extends ServiceImpl<${ClassName}Mapper, ${ClassName}Entity> implements ${ClassName}Service {

    /**
     * 构建树形结构数据
     * @param wrapper 查询条件
     * @return 树形结构数据
     */
    @Override
    public List<${ClassName}Entity> buildTree(LambdaQueryWrapper<${ClassName}Entity> wrapper) {
        // 查询所有数据
        List<${ClassName}Entity> allList = list(wrapper);
        
        if (CollUtil.isEmpty(allList)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<${pk.attrType}, List<${ClassName}Entity>> groupByParent = allList.stream()
                .collect(Collectors.groupingBy(item -> {
                    // 假设有parentId字段，如果没有请根据实际情况调整
                    return item.getParentId() != null ? item.getParentId() : 0L;
                }));

        // 构建树形结构
        return buildTreeRecursive(groupByParent, 0L);
    }

    /**
     * 递归构建树形结构
     * @param groupByParent 按父ID分组的数据
     * @param parentId 父ID
     * @return 树形结构数据
     */
    private List<${ClassName}Entity> buildTreeRecursive(Map<${pk.attrType}, List<${ClassName}Entity>> groupByParent, ${pk.attrType} parentId) {
        List<${ClassName}Entity> children = groupByParent.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return new ArrayList<>();
        }

        children.forEach(child -> {
            List<${ClassName}Entity> subChildren = buildTreeRecursive(groupByParent, child.get${str.capitalizeFirst($pk.attrName)}());
            child.setChildren(subChildren);
            child.setHasChildren(!CollUtil.isEmpty(subChildren));
        });

        return children;
    }

    /**
     * 获取所有父级节点
     * @return 父级节点列表
     */
    @Override
    public List<${ClassName}Entity> getParentNodes() {
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
        // 查询所有父级节点（parentId为null或0的节点）
        wrapper.and(w -> w.isNull(${ClassName}Entity::getParentId).or().eq(${ClassName}Entity::getParentId, 0));
        wrapper.orderByAsc(${ClassName}Entity::getSort); // 假设有sort字段用于排序
        return list(wrapper);
    }

    /**
     * 根据父ID获取子节点
     * @param parentId 父级ID
     * @return 子节点列表
     */
    @Override
    public List<${ClassName}Entity> getChildrenByParentId(${pk.attrType} parentId) {
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(${ClassName}Entity::getParentId, parentId);
        wrapper.orderByAsc(${ClassName}Entity::getSort); // 假设有sort字段用于排序
        return list(wrapper);
    }

    /**
     * 递归删除节点及其子节点
     * @param ids 要删除的节点ID列表
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatchByIds(List<${pk.attrType}> ids) {
        if (CollUtil.isEmpty(ids)) {
            return true;
        }

        // 获取所有要删除的ID（包括子节点）
        List<${pk.attrType}> allDeleteIds = new ArrayList<>(ids);
        
        for (${pk.attrType} id : ids) {
            List<${pk.attrType}> childIds = getChildIdsRecursive(id);
            allDeleteIds.addAll(childIds);
        }

        // 去重
        allDeleteIds = allDeleteIds.stream().distinct().collect(Collectors.toList());

        // 批量删除
        return super.removeBatchByIds(allDeleteIds);
    }

    /**
     * 递归获取所有子节点ID
     * @param parentId 父节点ID
     * @return 子节点ID列表
     */
    private List<${pk.attrType}> getChildIdsRecursive(${pk.attrType} parentId) {
        List<${pk.attrType}> childIds = new ArrayList<>();
        
        LambdaQueryWrapper<${ClassName}Entity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(${ClassName}Entity::getParentId, parentId);
        wrapper.select(${ClassName}Entity::get${str.capitalizeFirst($pk.attrName)});
        
        List<${ClassName}Entity> children = list(wrapper);
        
        for (${ClassName}Entity child : children) {
            ${pk.attrType} childId = child.get${str.capitalizeFirst($pk.attrName)}();
            childIds.add(childId);
            // 递归获取子节点的子节点
            childIds.addAll(getChildIdsRecursive(childId));
        }
        
        return childIds;
    }
} 