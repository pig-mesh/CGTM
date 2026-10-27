## 根节点 ID 按主键类型生成，与 Tree<${pk.attrType}> 类型一致
#if($pk.attrType == 'Long')
#set($treeRootId = '0L')
#elseif($pk.attrType == 'String')
#set($treeRootId = '"0"')
#else
#set($treeRootId = '0')
#end
package ${package}.${moduleName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${entityClassName};
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
    public List<Tree<${pk.attrType}>> buildTree(LambdaQueryWrapper<${ClassName}Entity> wrapper) {
        // 查询命中节点，再按层补齐祖先，保留完整树路径
        List<${ClassName}Entity> allList = list(wrapper);
        
        if (CollUtil.isEmpty(allList)) {
            return new ArrayList<>();
        }

        Map<${pk.attrType}, ${ClassName}Entity> nodes = new LinkedHashMap<>();
        allList.forEach(entity -> nodes.put(entity.$str.getProperty($pk.attrName)(), entity));
        List<${ClassName}Entity> level = allList;
        while (!level.isEmpty()) {
            List<${pk.attrType}> parentIds = level.stream()
                .map(${ClassName}Entity::$str.getProperty($parentField))
                .filter(id -> id != null && !Objects.equals(id, ${treeRootId}) && !nodes.containsKey(id))
                .distinct()
                .toList();
            if (parentIds.isEmpty()) {
                break;
            }
            level = listByIds(parentIds);
            level.forEach(entity -> nodes.put(entity.$str.getProperty($pk.attrName)(), entity));
        }

        // 转换为TreeNode，仅包含命中节点及其祖先
        List<TreeNode<${pk.attrType}>> collect = nodes.values().stream().map(getNodeFunction()).toList();

        // 使用TreeUtil构建树形结构，根节点ID为0
        return TreeUtil.build(collect, ${treeRootId});
    }

    /**
     * 获取TreeNode转换函数
     * @return TreeNode转换函数
     */
    @NotNull
    private Function<${ClassName}Entity, TreeNode<${pk.attrType}>> getNodeFunction() {
        return entity -> {
            TreeNode<${pk.attrType}> node = new TreeNode<>();
            node.setId(entity.$str.getProperty($pk.attrName)());
            node.setName(entity.$str.getProperty($nameField)());
            node.setParentId(entity.$str.getProperty($parentField)() != null ? entity.$str.getProperty($parentField)() : ${treeRootId});

            // 扩展属性
            Map<String, Object> extra = new HashMap<>();
#foreach($field in $fieldList)
            extra.put(${ClassName}Entity.Fields.${field.attrName}, entity.$str.getProperty($field.attrName)());
#end
            node.setExtra(extra);
            return node;
        };
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
        wrapper.eq(${ClassName}Entity::$str.getProperty($parentField), parentId);
        wrapper.select(${ClassName}Entity::$str.getProperty($pk.attrName));
        
        List<${ClassName}Entity> children = list(wrapper);
        
        for (${ClassName}Entity child : children) {
            ${pk.attrType} childId = child.$str.getProperty($pk.attrName)();
            childIds.add(childId);
            // 递归获取子节点的子节点
            childIds.addAll(getChildIdsRecursive(childId));
        }
        
        return childIds;
    }
} 