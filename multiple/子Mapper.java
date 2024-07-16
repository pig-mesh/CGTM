package ${package}.${moduleName}.mapper;

import ${package}.common.data.datascope.PigxBaseMapper;
import ${package}.${moduleName}.entity.${ChildClassName}Entity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${ChildClassName}Mapper extends PigxBaseMapper<${ChildClassName}Entity> {

}
