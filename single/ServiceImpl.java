package ${package}.${moduleName}.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package}.${moduleName}.entity.${ClassName}Entity;
import ${package}.${moduleName}.mapper.${ClassName}Mapper;
import ${package}.${moduleName}.service.${ClassName}Service;
import org.springframework.stereotype.Service;

/**
 * ${tableComment}
 *
 * @author ${author}
 * @date ${datetime}
 */
@Service
public class ${ClassName}ServiceImpl extends ServiceImpl<${ClassName}Mapper, ${ClassName}Entity> implements ${ClassName}Service {

}
