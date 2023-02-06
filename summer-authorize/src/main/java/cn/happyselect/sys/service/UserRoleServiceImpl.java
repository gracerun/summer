package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.happyselect.sys.util.UserHolder;
import cn.happyselect.sys.bean.dto.UserRoleUpdateDto;
import cn.happyselect.sys.bean.vo.UserRoleTreeVo;
import cn.happyselect.sys.constant.RoleCodeConstant;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.entity.User;
import cn.happyselect.sys.entity.UserRole;
import cn.happyselect.sys.mapper.RoleMapper;
import cn.happyselect.sys.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户角色关系service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class UserRoleServiceImpl {

    @Resource
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 创建用户角色关系
     *
     * @param userId
     * @param roleCodes
     * @return
     */
    public void createUserRole(String userId, List<String> roleCodes) {
        Assert.hasText(userId, "用户ID不能为空");
        if (!CollectionUtils.isEmpty(roleCodes)) {
            for (String roleCode : roleCodes) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleCode(roleCode);
                userRoleMapper.insert(userRole);
            }
        }
    }

    /**
     * 更新指定用户的角色关系
     *
     * @param dto
     * @return
     */
    @Transactional
    public void updateUserRole(UserRoleUpdateDto dto) {
        Assert.hasText(dto.getUserId(), "用户ID不能为空");

        Assert.isTrue(StringUtils.hasText(dto.getDeleteRoleCodes())
                        || StringUtils.hasText(dto.getAddRoleCodes()),
                "删除用户角色或添加用户角色参数不能为空");

        String userId = dto.getUserId();
        String addRoleCodes = dto.getAddRoleCodes();
        String deleteRoleCodes = dto.getDeleteRoleCodes();

        User t = userService.selectByUserId(userId);
        Assert.notNull(t, "此用户不存在");
        List<Role> roles = getManagerRoles(t);

        //查询已分配的角色
        List<Role> checkedRoles = roleMapper.selectByUserId(userId);

        Set<String> roleCodeEffective = Stream.concat(roles.stream(), checkedRoles.stream()).map(r -> r.getRoleCode()).collect(Collectors.toSet());

        if (StringUtils.hasText(deleteRoleCodes)) {
            List<String> list = Arrays.asList(deleteRoleCodes.split(","));
            Assert.isTrue(roleCodeEffective.containsAll(list), "角色数据错误");
            userRoleService.delete(dto.getUserId(), list);
        }

        if (StringUtils.hasText(addRoleCodes)) {
            List<String> list = Arrays.asList(addRoleCodes.split(","));
            Assert.isTrue(roleCodeEffective.containsAll(list), "角色数据错误");

            for (String roleCode : list) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleCode(roleCode);
                userRoleMapper.insert(userRole);
            }
        }
    }

    public Collection<UserRoleTreeVo> findUserRoleTree(String userId) {
        Assert.hasText(userId, "用户ID不能为空");

        User t = userService.selectByUserId(userId);
        Assert.notNull(t, "此用户不存在");
        List<Role> roles = getManagerRoles(t);

        //查询已分配的角色
        List<Role> checkedRoles = roleMapper.selectByUserId(t.getUserId());

        Map<String, UserRoleTreeVo> result = new HashMap<>(roles.size() + checkedRoles.size());

        roles.stream().forEach(r -> {
            UserRoleTreeVo vo = new UserRoleTreeVo();
            vo.setRoleCode(r.getRoleCode());
            vo.setRoleName(r.getRoleName());
            result.put(r.getRoleCode(), vo);
        });

        checkedRoles.stream().forEach(r -> {
            UserRoleTreeVo vo = result.get(r.getRoleCode());
            if (vo == null) {
                vo = new UserRoleTreeVo();
                vo.setRoleCode(r.getRoleCode());
                vo.setRoleName(r.getRoleName());
                vo.setChecked(StatusConstant.TRUE);
                result.put(r.getRoleCode(), vo);
            } else {
                vo.setChecked(StatusConstant.TRUE);
            }
            if (RoleCodeConstant.AGENT.equals(vo.getRoleCode())
                    || RoleCodeConstant.CUSTOMER.equals(vo.getRoleCode())) {
                vo.setChkDisabled(StatusConstant.TRUE);
            }

            if (RoleCodeConstant.ADMIN.equals(vo.getRoleCode())
                    && SystemType.OPER.name().equalsIgnoreCase(t.getSystemType())
                    && UserType.ADMIN.name().equalsIgnoreCase(t.getUserType())) {
                vo.setChkDisabled(StatusConstant.TRUE);
            }

        });
        return result.values();
    }

    /**
     * 查询可管理角色
     *
     * @param t
     * @return
     */
    public List<Role> getManagerRoles(User t) {
        UserHolder.validUserIdRelation(t.getUserId(), t.getParentUserId());
        List<Role> roles;
        if (UserType.ADMIN == UserType.resolve(t.getUserType())) {
            roles = roleService.selectByUserId(t.getUserId(), Arrays.asList(RoleCodeConstant.AGENT, RoleCodeConstant.CUSTOMER));
        } else {
            roles = roleService.selectByUserId(t.getParentUserId(), Arrays.asList(RoleCodeConstant.AGENT, RoleCodeConstant.CUSTOMER));
        }
        return roles;
    }

    public int delete(String userId, Collection<String> roleCodes) {
        Assert.hasText(userId, "用户ID不能为空");
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        if (!CollectionUtils.isEmpty(roleCodes)) {
            wrapper.in(UserRole::getRoleCode, roleCodes);
        }
        int result = userRoleMapper.delete(wrapper);
        log.info("userRoleMapper.delete result:{}", result);
        return result;
    }

    public int delete(String roleCode) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getRoleCode, roleCode);
        int result = userRoleMapper.delete(wrapper);
        log.info("userRoleMapper.delete result:{}", result);
        return result;
    }

}
