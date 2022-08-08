package ${packageStr};

import lombok.SneakyThrows;
import ${entityPackage}.${entityName};
import ${servicePackage}.${serviceName};
import ${dtoPackage}.${dtoClassName};
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summer.base.bean.ResponseBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.google.common.base.Charsets;
import com.alibaba.excel.EasyExcel;
import javax.servlet.http.HttpServletResponse;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;

/**
 * ${entityDesc}控制层
 *
 * @author ${author}
 * @date ${time}
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Api(value = "${entityDesc}",tags = "${entityDesc}")
@RestController
@RequestMapping("/${requestMapping}")
public class ${className} {

    @Autowired
    private ${serviceName} ${lowerServiceName};

    /**
     * 条件分页查询${entityDesc}
     *
     * @param queryDto 条件信息
     * @return
     */
    @ApiOperation(value = "分页查询${entityDesc}", notes = "按条件分页查询${entityDesc}", httpMethod = "POST", produces = "application/json")
    @PostMapping("/findByPage")
    public ResponseBean<IPage<${entityName}>> findByPage(@Valid @RequestBody ${entityName}Dto queryDto) {
        return ${lowerServiceName}.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
    }

    /**
     * 通过id查询${entityDesc}
     *
     * @return
     */
    @ApiOperation(value = "通过id查询${entityDesc}", notes = "通过id查询${entityDesc}", httpMethod = "POST", produces = "application/json")
    @PostMapping("/findById/{id}")
    public ResponseBean<${entityName}> findById(@PathVariable Long id) {
        return ${lowerServiceName}.findById(id);
    }

    /**
     * 新增${entityDesc}
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增${entityDesc}数据", notes = "新增${entityDesc}数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public ResponseBean<${entityName}> create(@Valid @RequestBody ${entityName} pojo) {
        return ${lowerServiceName}.create(pojo);
    }

    /**
     * 更新${entityDesc}
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新${entityDesc}数据", notes = "更新${entityDesc}数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public ResponseBean<${entityName}> update(@RequestBody ${entityName} pojo) {
        return ${lowerServiceName}.updateEntity(pojo);
    }

    /**
    * 导出Excel
    * @param queryDto
    * @throws Exception
    */
    @SneakyThrows
    @ApiOperation(value = "导出Excel", notes = "导出Excel", httpMethod = "GET", produces = "application/json")
    @PostMapping("/exportExcel")
    public void exportExcel(@Valid @RequestBody ${entityName}Dto queryDto, HttpServletResponse response) {
        if (queryDto == null) {
            queryDto = new ${entityName}Dto();
        }
        ResponseBean<List<${entityName}>> responseBean = ${lowerServiceName}.findBy(queryDto);
        if (responseBean.isSuccess() && responseBean.getData() != null && responseBean.getData().size() > 0) {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding(Charsets.UTF_8.name());
            String fileName = URLEncoder.encode("${entityDesc}表", Charsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            EasyExcel.write(response.getOutputStream(), ${entityName}.class).sheet("${entityDesc}表").doWrite(responseBean.getData());
        }
    }

    /**
    * 导出Excel模板
    */
    @SneakyThrows
    @ApiOperation(value = "导出Excel模板", notes = "导出Excel模板", httpMethod = "GET", produces = "application/json")
    @GetMapping("/exportExcelTmp")
    public void exportUser(HttpServletResponse response) {
    List<${entityName}> list = new ArrayList<>();
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(Charsets.UTF_8.name());
        String fileName = URLEncoder.encode("${entityDesc}模板", Charsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ${entityName}.class).sheet("${entityDesc}模板").doWrite(list);
    }

}
