package cn.happyselect.sys.service;

import cn.happyselect.base.enums.SystemType;
import cn.happyselect.sys.annotation.PrivilegeSync;
import cn.happyselect.sys.constant.PrivilegeTypeConstant;
import cn.happyselect.sys.entity.MenuFunction;
import cn.happyselect.sys.entity.RolePrivilege;
import cn.happyselect.sys.mapper.MenuFunctionMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 菜单功能service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class MenuFunctionServiceImpl {

    @Resource
    private MenuFunctionMapper menuFunctionMapper;

    @Autowired
    private MenuFunctionServiceImpl menuFunctionService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Transactional
    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public MenuFunction addMenuFunction(MenuFunction menuFunction) {
        Assert.hasText(menuFunction.getFunctionName(), "功能名称不能为空");
        Assert.hasText(menuFunction.getUrl(), "url不能为空");
        Assert.hasText(menuFunction.getMenuCode(), "关联菜单编号不能为空");

        MenuFunction t = new MenuFunction();
        BeanUtil.copyProperties(menuFunction, t, CopyOptions.create().ignoreNullValue());
        t.setFunctionCode(IdWorker.getIdStr());
        menuFunctionMapper.insert(t);

        Collection roleCodes = roleService.selectAllAdminRole(SystemType.OPER);
        RolePrivilege rolePrivilege = new RolePrivilege();
        rolePrivilege.setPrivilegeCode(t.getFunctionCode());
        rolePrivilege.setType(PrivilegeTypeConstant.FUNCTION);
        rolePrivilegeService.addAdminPrivilege(rolePrivilege, roleCodes);
        return t;
    }

    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public MenuFunction updateMenuFunction(MenuFunction menuFunction) {
        Assert.hasText(menuFunction.getFunctionCode(), "功能编号不能为空");
        Assert.hasText(menuFunction.getFunctionName(), "功能名称不能为空");
        Assert.hasText(menuFunction.getUrl(), "url不能为空");
        Assert.hasText(menuFunction.getMenuCode(), "关联菜单编号不能为空");

        MenuFunction t = menuFunctionService.selectByFunctionCode(menuFunction.getFunctionCode());
        Assert.notNull(t, "此菜单功能不存在");

        BeanUtil.copyProperties(menuFunction, t, CopyOptions.create().ignoreNullValue());
        t.setLastUpdateTime(new Date());
        menuFunctionMapper.updateById(t);
        return t;
    }

    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public void deleteMenuFunction(String functionCode) {
        Assert.hasText(functionCode, "功能编号不能为空");
        MenuFunction t = menuFunctionService.selectByFunctionCode(functionCode);
        Assert.notNull(t, "此菜单功能不存在");
        menuFunctionMapper.deleteById(t.getId());
    }

    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public void deleteMenuFunctionByMenuCode(String menuCode) {
        Assert.hasText(menuCode, "菜单编号不能为空");
        LambdaQueryWrapper<MenuFunction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuFunction::getMenuCode, menuCode);
        int result = menuFunctionMapper.delete(wrapper);
        log.info("menuFunctionMapper.delete result:{}", result);
    }

    /**
     * 根据菜单功能编号查询菜单按钮
     *
     * @param functionCode
     * @return
     */
    public MenuFunction selectByFunctionCode(String functionCode) {
        LambdaQueryWrapper<MenuFunction> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(MenuFunction::getFunctionCode, functionCode);
        return menuFunctionMapper.selectOne(menuWrapper);
    }

    /**
     * 根据菜单编号查询菜单按钮
     *
     * @param menuCode
     * @return
     */
    public List<MenuFunction> selectByMenuCode(String menuCode) {
        LambdaQueryWrapper<MenuFunction> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(MenuFunction::getMenuCode, menuCode);
        return menuFunctionMapper.selectList(menuWrapper);
    }
}
