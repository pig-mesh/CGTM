package ${package}.${moduleName}.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.entity.${ClassName}Entity;

import java.util.List;

/**
 * ${tableComment} Service接口
 *
 * @author ${author}
 * @date ${datetime}
 */
#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPkField = $field)
#break
#end
#end
public interface ${ClassName}Service extends IService<${ClassName}Entity> {

    /**
     * 构建树形结构数据
     * @param wrapper 查询条件
     * @return 树形结构数据
     */
    List<Tree<${childPkField.attrType}>> buildTree(LambdaQueryWrapper<${ChildClassName}Entity> wrapper);

    /**
     * 获取树节点详情
     * @param ${childClassName} 树节点查询对象
     * @return 树节点详情列表
     */
    List<${ChildClassName}Entity> getTreeDetails(${ChildClassName}Entity ${childClassName});

    /**
     * 保存树节点
     * @param ${childClassName} 树节点对象
     * @return 保存结果
     */
    Boolean saveTree(${ChildClassName}Entity ${childClassName});

    /**
     * 更新树节点
     * @param ${childClassName} 树节点对象
     * @return 更新结果
     */
    Boolean updateTree(${ChildClassName}Entity ${childClassName});

    /**
     * 批量删除树节点
     * @param ids 树节点ID列表
     * @return 删除结果
     */
    Boolean removeTreeByIds(${childPkField.attrType}[] ids);
}
