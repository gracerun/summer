package ${packageStr};

import com.summer.base.service.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import ${entityPackage}.${entityName};
import ${daoType};

/**
 * ${entityDesc}service服务类
 *
 * @author ${author}
 * @date ${time}
 * @version 1.0
 * @since 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ${entityName}Service extends BaseServiceImpl<${entityName}Mapper, ${entityName}> {

    @Resource
    private ${entityName}Mapper ${lowerEntityName}Mapper;
}
