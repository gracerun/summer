package cn.happyselect.sys.service;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.happyselect.sys.util.UserHolder;
import cn.happyselect.sys.annotation.PrivilegeSync;
import cn.happyselect.sys.bean.dto.RoleCreateDto;
import cn.happyselect.sys.bean.dto.RolePrivilegeUpdateDto;
import cn.happyselect.sys.bean.dto.RoleUpdateDto;
import cn.happyselect.sys.constant.RoleLevelConstant;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.mapper.RoleMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 角色service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月30日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class RoleServiceImpl {

    @Resource
    private RoleMapper roleMapper;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private RolePrivilegeBizService rolePrivilegeBizService;

    @Transactional
    public Role createRole(RoleCreateDto pojo) {
        Role t = new Role();
        BeanUtil.copyProperties(pojo, t, CopyOptions.create().ignoreNullValue());
        t.setRoleCode(IdWorker.getIdStr());
        t.setLevel(RoleLevelConstant.LEVEL_1);
        UserContext userContext = UserHolder.getCurrentUser();
        if (UserType.ADMIN == userContext.getUserType()) {
            t.setUserId(userContext.getUserId());
        } else {
            t.setUserId(userContext.getParentUserId());
        }
        t.setSystemType(userContext.getSystemType().name());
        roleMapper.insert(t);

        if (StringUtils.hasText(pojo.getAddMenuCodes())
                || StringUtils.hasText(pojo.getAddMenuFunctionCodes())) {
            //创建角色权限
            RolePrivilegeUpdateDto dto = new RolePrivilegeUpdateDto();
            dto.setRoleCode(t.getRoleCode());
            dto.setAddMenuCodes(pojo.getAddMenuCodes());
            dto.setAddMenuFunctionCodes(pojo.getAddMenuFunctionCodes());
            rolePrivilegeBizService.updateRolePrivilege(dto);
        }
        return t;
    }

    @PrivilegeSync
    public Role updateRole(RoleUpdateDto pojo) {
        Role t = roleService.selectRole(pojo.getRoleCode());
        Assert.notNull(t, "此角色不存在");
        UserHolder.validUserIdRelation(t.getUserId());

        if (StringUtils.hasText(pojo.getRoleName())) {
            t.setRoleName(pojo.getRoleName());
        }
        if (StringUtils.hasText(pojo.getStatus())) {
            t.setStatus(pojo.getStatus());
        }
        roleMapper.updateById(t);
        return t;
    }

    @Transactional
    @PrivilegeSync
    public void deleteRole(String roleCode) {
        Assert.hasText(roleCode, "角色编号不能为空");

        Role t = roleService.selectRole(roleCode);
        Assert.notNull(t, "此角色不存在");
        UserHolder.validUserIdRelation(t.getUserId());
        Collection<String> rootRoleCodes = roleService.selectAllAdminRole(null);

        if (!CollectionUtils.isEmpty(rootRoleCodes)
                && rootRoleCodes.contains(roleCode)) {
            throw new RuntimeException("管理员角色不允许删除");
        }

        //删除用户关联的角色
        userRoleService.delete(t.getRoleCode());
        //删除角色关联的权限
        rolePrivilegeService.delete(t.getRoleCode());
        //删除角色
        roleMapper.deleteById(t.getId());
    }

    public List<Role> selectByUserId(String userId, Collection<String> ignoreRoleCodes) {
        Assert.hasText(userId, "用户ID不能为空");
        LambdaQueryWrapper<Role> query = new LambdaQueryWrapper<>();
        query.eq(Role::getUserId, userId);
        if (!CollectionUtils.isEmpty(ignoreRoleCodes)) {
            query.notIn(Role::getRoleCode, ignoreRoleCodes);
        }
        query.eq(Role::getStatus, StatusConstant.TRUE);
        return roleMapper.selectList(query);
    }

    public Collection<String> selectAllAdminRole(SystemType systemType) {
        return selectRoleCodeByLevel(RoleLevelConstant.LEVEL_0, systemType);
    }

    /**
     * 根据角色等级查询角色
     *
     * @param level
     * @return
     */
    private Collection<String> selectRoleCodeByLevel(Integer level, SystemType systemType) {
        Assert.notNull(level, "角色等级不能为空");
        LambdaQueryWrapper<Role> query = new LambdaQueryWrapper<>();
        if (Objects.nonNull(systemType)) {
            query.eq(Role::getSystemType, systemType.name());
        }
        query.eq(Role::getStatus, StatusConstant.TRUE);
        query.eq(Role::getLevel, level);
        List<Role> roles = roleMapper.selectList(query);
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        } else {
            return roles.stream().map(Role::getRoleCode).collect(Collectors.toList());
        }
    }

    /**
     * 根据角色编号查询角色
     *
     * @param roleCode
     * @return
     */
    public Role selectRole(String roleCode) {
        Assert.hasText(roleCode, "角色编码不能为空");
        LambdaQueryWrapper<Role> query = new LambdaQueryWrapper<>();
        query.eq(Role::getRoleCode, roleCode);
        return roleMapper.selectOne(query);
    }

}
