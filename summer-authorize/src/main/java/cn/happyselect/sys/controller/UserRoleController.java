package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.UserRoleUpdateDto;
import cn.happyselect.sys.bean.vo.UserRoleTreeVo;
import cn.happyselect.sys.service.UserRoleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

/**
 * 用户角色关系控制层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Slf4j
@Api(value = "用户角色关系", tags = "用户角色关系")
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleServiceImpl userRoleBizService;

    /**
     * 更新用户角色关系
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "更新用户角色关系数据", notes = "更新用户角色关系数据", httpMethod = "POST", produces = "application/json")
    @PostMapping(value = "/update")
    public void updateUserRole(@Valid @RequestBody UserRoleUpdateDto dto) {
        userRoleBizService.updateUserRole(dto);
    }

    /**
     * 查询用户角色分配数据
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "查询用户角色分配数据", notes = "查询用户角色分配数据", httpMethod = "POST", produces = "application/json")
    @PostMapping(value = "/tree/{userId}")
    public Collection<UserRoleTreeVo> findUserRoleTree(@PathVariable String userId) {
        return userRoleBizService.findUserRoleTree(userId);
    }

}
