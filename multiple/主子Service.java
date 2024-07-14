package ${package}.${moduleName}.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import ${package}.${moduleName}.entity.${ClassName}Entity;

public interface ${ClassName}Service extends MPJDeepService<${ClassName}Entity> {
    Boolean saveDeep(${ClassName}Entity ${className});

    Boolean updateDeep(${ClassName}Entity ${className});

    Boolean removeDeep(Long[] ids);

    Boolean removeChild(Long[] ids);
}
