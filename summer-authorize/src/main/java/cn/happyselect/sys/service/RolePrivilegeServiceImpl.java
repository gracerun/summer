package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.sys.annotation.PrivilegeSync;
import cn.happyselect.sys.bean.dto.RolePrivilegeTreeQueryDto;
import cn.happyselect.sys.bean.vo.MenuTreeVo;
import cn.happyselect.sys.constant.MenuNodeTypeConstant;
import cn.happyselect.sys.constant.PrivilegeTypeConstant;
import cn.happyselect.sys.entity.RolePrivilege;
import cn.happyselect.sys.mapper.RolePrivilegeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * 角色权限关系service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class RolePrivilegeServiceImpl {

    @Resource
    private RolePrivilegeMapper rolePrivilegeMapper;

    /**
     * 给管理员添加权限
     *
     * @param rolePrivilege
     */
    @PrivilegeSync
    public void addAdminPrivilege(RolePrivilege rolePrivilege, Collection<String> roleCodes) {
        Assert.notEmpty(roleCodes, "角色编码不能为空");
        Assert.hasText(rolePrivilege.getPrivilegeCode(), "授权资源编号不能为空");
        Assert.hasText(rolePrivilege.getType(), "授权资源类型不能为空");

        for (String roleCode : roleCodes) {
            RolePrivilege rp = new RolePrivilege();
            rp.setRoleCode(roleCode);
            rp.setPrivilegeCode(rolePrivilege.getPrivilegeCode());
            rp.setType(rolePrivilege.getType());
            rolePrivilegeMapper.insert(rp);
        }
    }

    @PrivilegeSync
    public int delete(String roleCode, String type, Collection<String> privilegeCodes) {
        if (!StringUtils.hasText(roleCode)
                && CollectionUtils.isEmpty(privilegeCodes)) {
            log.warn("删除角色权限参数为空");
            return 0;
        }

        LambdaQueryWrapper<RolePrivilege> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleCode)) {
            wrapper.eq(RolePrivilege::getRoleCode, roleCode);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(RolePrivilege::getType, type);
        }
        if (!CollectionUtils.isEmpty(privilegeCodes)) {
            wrapper.in(RolePrivilege::getPrivilegeCode, privilegeCodes);
        }
        int result = rolePrivilegeMapper.delete(wrapper);
        log.info("rolePrivilegeMapper.delete result:{}", result);
        return result;
    }

    @PrivilegeSync
    public int delete(String type, Collection<String> privilegeCodes) {
        return delete(null, type, privilegeCodes);
    }

    @PrivilegeSync
    public int delete(String roleCode) {
        return delete(roleCode, null, null);
    }

    public List<RolePrivilege> selectList(Collection<String> roleCodes) {
        LambdaQueryWrapper<RolePrivilege> query = new LambdaQueryWrapper<>();
        query.in(RolePrivilege::getRoleCode, roleCodes);
        return rolePrivilegeMapper.selectList(query);
    }

    /**
     * 查询首页菜单树
     *
     * @param roles
     * @return
     */
    public List<MenuTreeVo> selectMenuTree(Collection<String> roles) {
        Assert.notEmpty(roles, "角色参数为空");

        RolePrivilegeTreeQueryDto privilegeTreeQueryBean = new RolePrivilegeTreeQueryDto();
        privilegeTreeQueryBean.setRoleCodes(roles);
        privilegeTreeQueryBean.setStatus(StatusConstant.TRUE);
        privilegeTreeQueryBean.setTypes(Arrays.asList(PrivilegeTypeConstant.MENU));

        List<MenuTreeVo> list = rolePrivilegeMapper.selectIndexMenu(privilegeTreeQueryBean);

        ArrayList<MenuTreeVo> menuList = new ArrayList<>(32);

        Map<String, MenuTreeVo> map = new HashMap<>(32);

        for (MenuTreeVo menuTreeBean : list) {
            if (MenuNodeTypeConstant.TRUE.equals(menuTreeBean.getNodeType())) {
                map.put(menuTreeBean.getMenuCode(), menuTreeBean);
                menuList.add(menuTreeBean);
            }
        }

        ListIterator<MenuTreeVo> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            MenuTreeVo menuTreeBean = listIterator.next();
            if (!MenuNodeTypeConstant.TRUE.equals(menuTreeBean.getNodeType())) {
                MenuTreeVo parentMenu = map.get(menuTreeBean.getParentCode());
                if (parentMenu != null) {
                    parentMenu.getChild().add(menuTreeBean);
                } else {
                    log.error("{} not found parentMenu", menuTreeBean);
                }
            }
        }

        return menuList;
    }
}
