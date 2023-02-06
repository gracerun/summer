package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.RolePrivilegeUpdateDto;
import cn.happyselect.sys.bean.vo.RolePrivilegeTreeVo;
import cn.happyselect.sys.service.RolePrivilegeBizService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色权限关系控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月07日
 * @since 1.8
 */
@Slf4j
@Api(value = "角色权限关系", tags = "角色权限关系")
@RestController

@RequestMapping("/rolePrivilege")
public class RolePrivilegeController {

    @Autowired
    private RolePrivilegeBizService rolePrivilegeBizService;

    /**
     * 更新角色权限表数据
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新角色权限表数据", notes = "更新角色权限表数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public void updateRolePrivilege(@RequestBody RolePrivilegeUpdateDto pojo) {
        rolePrivilegeBizService.updateRolePrivilege(pojo);
    }

    /**
     * 查询角色权限管理数据
     *
     * @param roleCode
     * @return
     */
    @ApiOperation(value = "查询角色权限管理数据", notes = "查询角色权限管理数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/tree/{roleCode}")
    public List<RolePrivilegeTreeVo> findRolePrivilegeManageTree(@PathVariable String roleCode) {
        return rolePrivilegeBizService.findRolePrivilegeManageTree(roleCode);
    }

    /**
     * 查询指定角色权限数据
     *
     * @param roleCodes
     * @return
     */
    @ApiOperation(value = "查询指定角色权限数据", notes = "查询指定角色权限数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/findByRoleCode")
    public List<RolePrivilegeTreeVo> findByRoleCode(@RequestBody List<String> roleCodes) {
        return rolePrivilegeBizService.findByRoleCode(roleCodes);
    }

}
