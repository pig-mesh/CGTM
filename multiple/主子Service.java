#foreach($field in $childFieldList)
#if($field.primaryPk == '1')
#set($childPk = $field)
#end
#end
package ${package}.${moduleName}.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import ${entityClassName};

public interface ${ClassName}Service extends MPJDeepService<${ClassName}Entity> {
    Boolean saveDeep(${ClassName}Entity ${className});

    Boolean updateDeep(${ClassName}Entity ${className});

    Boolean removeDeep(${pk.attrType}[] ids);

    Boolean removeChild(${childPk.attrType}[] ids);
}
