package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.ModifyPasswordDto;
import cn.happyselect.sys.bean.vo.MenuTreeVo;
import cn.happyselect.sys.bean.vo.RolePrivilegeTreeVo;
import cn.happyselect.sys.bean.vo.UserContextVo;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.service.CurrentUserBizService;
import cn.happyselect.sys.service.MessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 当前用户控制器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-10
 */
@Slf4j
@Api(value = "当前用户", tags = "当前用户")
@RestController

@RequestMapping("/current/user")
public class CurrentUserController {

    @Autowired
    private CurrentUserBizService currentUserBizService;

    @Autowired
    private MessageServiceImpl messageService;

    /**
     * 查询菜单数据
     *
     * @return
     */
    @ApiOperation(value = "查询菜单数据", notes = "查询菜单数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/menu")
    public List<MenuTreeVo> indexMenu() {
        return currentUserBizService.indexMenu();
    }

    /**
     * 查询按钮权限
     *
     * @return
     */
    @ApiOperation(value = "查询按钮权限", notes = "查询按钮权限", httpMethod = "POST", produces = "application/json")
    @PostMapping("/function")
    public List<String> functionPrivilege() {
        return currentUserBizService.functionPrivilege();
    }

    /**
     * 查询用户信息
     *
     * @return
     */
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息", httpMethod = "POST", produces = "application/json")
    @PostMapping("/info")
    public UserContextVo info() {
        return currentUserBizService.info();
    }

    /**
     * 更新用户密码
     *
     * @param updatePasswordDto
     * @return
     */
    @ApiOperation(value = "更新用户密码", notes = "更新用户密码", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update/password")
    public void updatePassword(@Valid @RequestBody ModifyPasswordDto updatePasswordDto) {
        currentUserBizService.modifyPassword(updatePasswordDto);
    }

    /**
     * 查询员工可分配的角色数据
     *
     * @return
     */
    @ApiOperation(value = "查询员工可分配的角色数据", notes = "查询员工可分配的角色数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/role/list")
    public Collection<Role> queryRoleList() {
        return currentUserBizService.queryRoleList();
    }

    /**
     * 查询角色可分配的菜单数据
     *
     * @return
     */
    @ApiOperation(value = "查询角色可分配的菜单数据", notes = "查询角色可分配的菜单数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/rolePrivilege/tree")
    public List<RolePrivilegeTreeVo> queryRolePrivilegeTree() {
        return currentUserBizService.queryRolePrivilegeTree();
    }

    /**
     * 条件分页查询消息表
     *
     * @param queryDto 条件信息
     * @return
     */
//    @ApiOperation(value = "分页查询当前用户消息表", notes = "按条件分页查询当前用户消息表", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/message/page")
//    public IPage<Message> messagePage(@Valid @RequestBody MessageDto queryDto) {
//        QueryItem toUserId = new QueryItem();
//        toUserId.setComparer(Comparer.EQU);
//        toUserId.setPropValue(new String[]{UserHolder.getCurrentUser().getUserId()});
//        queryDto.setToUserId(toUserId);
//        return messageService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

}
