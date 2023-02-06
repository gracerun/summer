package cn.happyselect.sys.service;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.happyselect.sys.bean.ShortMessageDtoInterface;
import cn.happyselect.sys.bean.dto.ModifyPasswordDto;
import cn.happyselect.sys.bean.dto.RolePrivilegeTreeQueryDto;
import cn.happyselect.sys.bean.dto.RoleUrlQueryDto;
import cn.happyselect.sys.bean.vo.MenuTreeVo;
import cn.happyselect.sys.bean.vo.RolePrivilegeTreeVo;
import cn.happyselect.sys.bean.vo.UserContextVo;
import cn.happyselect.sys.constant.RoleCodeConstant;
import cn.happyselect.sys.constant.SMSTypeConstant;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.entity.User;
import cn.happyselect.sys.enums.PwdModifyType;
import cn.happyselect.sys.mapper.RolePrivilegeMapper;
import cn.happyselect.sys.util.PasswordUtil;
import cn.happyselect.sys.util.UserHolder;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 首页服务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-10
 */
@Service
@Slf4j
public class CurrentUserBizService {

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private RolePrivilegeMapper rolePrivilegeMapper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CaptchaBizService captchaBizService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired(required = false)
    private List<UserContextInterface> userContextInterfaceList;

    /**
     * 查询首页菜单
     *
     * @return
     */
    public List<MenuTreeVo> indexMenu() {
        UserContext userContext = UserHolder.getCurrentUser();
        Collection<String> roles = userContext.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            List<MenuTreeVo> menuList = rolePrivilegeService.selectMenuTree(roles);
            return menuList;
        } else {
            return null;
        }
    }

    /**
     * 查询按钮权限
     *
     * @return
     */
    public List<String> functionPrivilege() {
        UserContext currentUserContext = UserHolder.getCurrentUser();
        Collection<String> roles = currentUserContext.getRoles();

        RoleUrlQueryDto roleUrlPrivilegeBean = new RoleUrlQueryDto();
        roleUrlPrivilegeBean.setRoleCodes(roles);
        roleUrlPrivilegeBean.setStatus(StatusConstant.TRUE);
        List<String> urlPrivileges = rolePrivilegeMapper.selectFunctionUrl(roleUrlPrivilegeBean);
        return urlPrivileges;
    }

    /**
     * 用户信息
     *
     * @return
     */
    public UserContextVo info() {
        UserContext currentUserContext = UserHolder.getCurrentUser();
        UserContextVo userContextVo = new UserContextVo();
        BeanUtil.copyProperties(currentUserContext, userContextVo, CopyOptions.create().ignoreNullValue());
        return userContextVo;
    }

    /**
     * 更改用户密码
     *
     * @param updatePasswordBean
     * @return
     */
    @Transactional
    public void modifyPassword(ModifyPasswordDto updatePasswordBean) {
        UserContext userContext = UserHolder.getCurrentUser();
        User t = userService.selectByUserId(userContext.getUserId());
        Assert.notNull(t, "此用户不存在");
        Assert.hasText(updatePasswordBean.getPassword(), "请输入新密码");
        if (PwdModifyType.OLD_PASSWORD.name().equals(updatePasswordBean.getPwdModifyType())) {
            Assert.hasText(updatePasswordBean.getOldPassword(), "请输入旧密码");
            Assert.isTrue(PasswordUtil.matches(updatePasswordBean.getOldPassword(), t.getPassword()), "旧密码错误");
        } else if (PwdModifyType.SMS_CODE.name().equals(updatePasswordBean.getPwdModifyType())) {
            Assert.hasText(updatePasswordBean.getSmsCode(), "请输入短信验证码");
            boolean matches = captchaBizService.validShortMsg(new ShortMessageDtoInterface() {
                @Override
                public String getPhone() {
                    return userContext.getPhone();
                }

                @Override
                public String getMessageType() {
                    return SMSTypeConstant.MODIFY_PASSWORD;
                }

                @Override
                public String getClientType() {
                    return userContext.getClientType().name();
                }
            }, updatePasswordBean.getSmsCode());
            Assert.isTrue(matches, "短信验证码错误");
        } else {
            throw new RuntimeException("密码修改方式错误");
        }
        userService.resetPassword(t.getUserId(), updatePasswordBean.getPassword());
        /**
         * @see SaveToSessionResponseWrapper.saveContext()
         */
        UserContext newUserContext = userContext.createNewUserContext();
        newUserContext.setExpireStatus(false);
        SecurityContextHolder.getContext().setAuthentication(newUserContext);
    }

    /**
     * 用户上下文数据刷新接口
     *
     * @return
     */
    public void refreshUserContext(User t) {
        UserContext currentUserContext = UserHolder.getCurrentUser();
        if (Objects.nonNull(currentUserContext) && currentUserContext.getUserId().equals(t.getUserId())) {
            log.info("refreshUserContext start {}", currentUserContext);
            UserContext newUserContext = currentUserContext.createNewUserContext();
            BeanUtil.copyProperties(t, newUserContext, CopyOptions.create().ignoreNullValue());
            newUserContext.setSystemType(SystemType.resolve(t.getSystemType()));
            newUserContext.setUserType(UserType.resolve(t.getUserType()));
            if (UserType.EMPLOYEE.name().equalsIgnoreCase(t.getUserType())) {
                User parentUser = userService.selectByUserId(t.getParentUserId());
                newUserContext.setBusinessNo(parentUser.getBusinessNo());
            }

            if (!CollectionUtils.isEmpty(userContextInterfaceList)) {
                for (UserContextInterface e : userContextInterfaceList) {
                    e.load(newUserContext);
                }
            }

            SecurityContextHolder.getContext().setAuthentication(newUserContext);
            log.info("refreshUserContext end {}", newUserContext);
        }
    }

    public Collection<Role> queryRoleList() {
        UserContext userContext = UserHolder.getCurrentUser();
        List<Role> roles;
        if (UserType.ADMIN == userContext.getUserType()) {
            roles = roleService.selectByUserId(userContext.getUserId(), Arrays.asList(RoleCodeConstant.AGENT, RoleCodeConstant.CUSTOMER));
        } else {
            roles = roleService.selectByUserId(userContext.getParentUserId(), Arrays.asList(RoleCodeConstant.AGENT, RoleCodeConstant.CUSTOMER));
        }
        return roles;
    }

    public List<RolePrivilegeTreeVo> queryRolePrivilegeTree() {
        Collection<String> roleCodes = UserHolder.getCurrentUser().getRoles();
        RolePrivilegeTreeQueryDto dto = new RolePrivilegeTreeQueryDto();
        dto.setRoleCodes(roleCodes);
        dto.setStatus(StatusConstant.TRUE);
        List<RolePrivilegeTreeVo> rolePrivilegeManageTree = rolePrivilegeMapper.findRolePrivilegeManageTree(dto);
        return rolePrivilegeManageTree;
    }
}
