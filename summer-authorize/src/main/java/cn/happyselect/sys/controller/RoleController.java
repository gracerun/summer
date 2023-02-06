package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.RoleCreateDto;
import cn.happyselect.sys.bean.dto.RoleUpdateDto;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.service.RoleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 角色控制层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Slf4j
@Api(value = "角色", tags = "角色")
@RestController

@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleServiceImpl roleService;

    /**
     * 条件分页查询角色
     *
     * @param queryDto 条件信息
     * @return
     */
//    @Translation
//    @DataScope(field = "userId", comparer = Comparer.EQU, dataType = DataType.USER)
//    @ApiOperation(value = "分页查询角色", notes = "按条件分页查询角色", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<Role> findByPage(@Valid @RequestBody RoleDto queryDto) {
//        return roleService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 新增角色
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增角色数据", notes = "新增角色数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public Role create(@Valid @RequestBody RoleCreateDto pojo) {
        return roleService.createRole(pojo);
    }

    /**
     * 更新角色
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新角色数据", notes = "更新角色数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public Role update(@Valid @RequestBody RoleUpdateDto pojo) {
        return roleService.updateRole(pojo);
    }

    /**
     * 删除角色数据
     *
     * @param roleCode
     * @return
     */
    @ApiOperation(value = "物理删除角色数据", notes = "物理删除角色数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/delete/{roleCode}")
    public void delete(@PathVariable String roleCode) {
        roleService.deleteRole(roleCode);
    }

}
