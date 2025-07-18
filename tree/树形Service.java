package ${package}.${moduleName}.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import ${package}.${moduleName}.entity.${ClassName}Entity;

import java.util.List;

/**
 * ${tableComment} Service接口
 *
 * @author ${author}
 * @date ${datetime}
 */
public interface ${ClassName}Service extends IService<${ClassName}Entity> {

    /**
     * 构建树形结构数据
     * @param wrapper 查询条件
     * @return 树形结构数据
     */
    List<${ClassName}Entity> buildTree(LambdaQueryWrapper<${ClassName}Entity> wrapper);

    /**
     * 获取所有父级节点
     * @return 父级节点列表
     */
    List<${ClassName}Entity> getParentNodes();

    /**
     * 根据父ID获取子节点
     * @param parentId 父级ID
     * @return 子节点列表
     */
    List<${ClassName}Entity> getChildrenByParentId(${pk.attrType} parentId);

    /**
     * 递归删除节点及其子节点
     * @param ids 要删除的节点ID列表
     * @return 删除结果
     */
    boolean removeBatchByIds(List<${pk.attrType}> ids);
} 