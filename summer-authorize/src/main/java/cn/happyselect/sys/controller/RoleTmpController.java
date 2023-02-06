package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.RoleTmpCreateDto;
import cn.happyselect.sys.bean.dto.RoleTmpUpdateDto;
import cn.happyselect.sys.entity.RoleTmp;
import cn.happyselect.sys.service.RoleTmpServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 角色模板控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月07日
 * @since 1.8
 */
@Slf4j
@Api(value = "角色模板", tags = "角色模板")
@RestController

@RequestMapping("/roleTmp")
public class RoleTmpController {

    @Autowired
    private RoleTmpServiceImpl roleTmpService;

    /**
     * 条件分页查询角色模板
     *
     * @param queryDto 条件信息
     * @return
     */
//    @Translation
//    @ApiOperation(value = "分页查询角色模板", notes = "按条件分页查询角色模板", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<RoleTmp> findByPage(@Valid @RequestBody RoleTmpDto queryDto) {
//        return roleTmpService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 新增角色模板
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增角色模板数据", notes = "新增角色模板数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public RoleTmp create(@Valid @RequestBody RoleTmpCreateDto pojo) {
        return roleTmpService.createRoleTmp(pojo);
    }

    /**
     * 更新角色模板
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新角色模板数据", notes = "更新角色模板数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public RoleTmp update(@RequestBody RoleTmpUpdateDto pojo) {
        return roleTmpService.updateRoleTmp(pojo);
    }

    /**
     * 删除角色模板数据
     *
     * @param roleCode
     * @return
     */
    @ApiOperation(value = "物理删除角色模板数据", notes = "物理删除角色模板数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/delete/{roleCode}")
    public void delete(@PathVariable String roleCode) {
        roleTmpService.deleteRoleTmp(roleCode);
    }

}
