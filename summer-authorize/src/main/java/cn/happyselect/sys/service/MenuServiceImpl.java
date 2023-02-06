package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.sys.annotation.PrivilegeSync;
import cn.happyselect.sys.bean.dto.DragMenuDto;
import cn.happyselect.sys.constant.DragMenuConstant;
import cn.happyselect.sys.constant.IgnoreProperties;
import cn.happyselect.sys.constant.MenuNodeTypeConstant;
import cn.happyselect.sys.constant.PrivilegeTypeConstant;
import cn.happyselect.sys.entity.Menu;
import cn.happyselect.sys.entity.MenuFunction;
import cn.happyselect.sys.entity.RolePrivilege;
import cn.happyselect.sys.mapper.MenuMapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class MenuServiceImpl {

    private static final String ROOT_MENU_CODE = "root";

    @Resource
    private MenuMapper menuMapper;

    @Autowired
    private MenuServiceImpl menuService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private MenuFunctionServiceImpl menuFunctionService;

    /**
     * 查询子菜单
     *
     * @param menuCode
     * @return
     */
    public List<Menu> tree(String menuCode) {
        LambdaQueryWrapper<Menu> query = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(menuCode)) {
            query.eq(Menu::getParentCode, menuCode);
        } else {
            query.eq(Menu::getMenuCode, ROOT_MENU_CODE);
        }
        query.eq(Menu::getStatus, StatusConstant.TRUE);
        query.orderByAsc(Menu::getDisplayOrder);

        List<Menu> menus = menuMapper.selectList(query);
        return menus;
    }

    /**
     * 菜单新增接口
     *
     * @param menu
     * @return
     */
    @Transactional
    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public Menu createMenu(Menu menu) {
        Assert.hasText(menu.getMenuCode(), "菜单编号不能为空");
        Assert.hasText(menu.getParentCode(), "菜单上级编号不能为空");
        Assert.hasText(menu.getMenuName(), "菜单名称不能为空");

        Assert.isTrue(MenuNodeTypeConstant.TRUE.equals(menu.getNodeType()) || MenuNodeTypeConstant.FALSE.equals(menu.getNodeType()),
                "菜单节点类型参数错误");

        if (MenuNodeTypeConstant.FALSE.equals(menu.getNodeType())) {
            Assert.hasText(menu.getUrl(), "菜单url不能为空");
        }

        Menu m = menuService.selectMenuByMenuCode(menu.getMenuCode());
        Assert.isNull(m, "菜单编码重复");

        Menu parentMenu = menuService.selectMenuByMenuCode(menu.getParentCode());
        Assert.notNull(parentMenu, "上级菜单不存在");

        Menu t = new Menu();
        BeanUtil.copyProperties(menu, t, CopyOptions.create().ignoreNullValue().setIgnoreProperties(IgnoreProperties.IGNORE));
        t.setLevel(parentMenu.getLevel() + 1);
        t.setStatus(StatusConstant.TRUE);

        Integer maxOrder = menuMapper.selectMaxOrderByParentCode(menu.getParentCode());
        if (maxOrder == null) {
            maxOrder = 0;
        }

        t.setDisplayOrder(maxOrder + 1);
        menuMapper.insert(t);

        Collection roleCodes = roleService.selectAllAdminRole(SystemType.OPER);
        RolePrivilege rolePrivilege = new RolePrivilege();
        rolePrivilege.setPrivilegeCode(t.getMenuCode());
        rolePrivilege.setType(PrivilegeTypeConstant.MENU);
        rolePrivilegeService.addAdminPrivilege(rolePrivilege, roleCodes);
        return t;
    }

    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public Menu updateMenu(Menu menu) {
        Assert.hasText(menu.getMenuCode(), "菜单编号不能为空");
        Assert.hasText(menu.getMenuName(), "菜单名称不能为空");

        Menu t = menuService.selectMenuByMenuCode(menu.getMenuCode());
        Assert.notNull(t, "此菜单不存在");

        if (MenuNodeTypeConstant.FALSE.equals(t.getNodeType())) {
            Assert.hasText(menu.getUrl(), "菜单url不能为空");
        }

        t.setMenuName(menu.getMenuName());
        t.setStatus(menu.getStatus());
        t.setUrl(menu.getUrl());
        t.setLastUpdateTime(new Date());
        menuMapper.updateById(t);
        return t;
    }

    @Transactional
    @PrivilegeSync
    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public void deleteMenu(String menuCode) {
        Assert.hasText(menuCode, "菜单编号不能为空");

        Menu t = menuService.selectMenuByMenuCode(menuCode);
        Assert.notNull(t, "此菜单不存在");

        Assert.isTrue(!ROOT_MENU_CODE.equals(t.getMenuCode()), "请勿删除根菜单");

        if (MenuNodeTypeConstant.TRUE.equals(t.getNodeType())) {
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getParentCode, menuCode);
            menuWrapper.eq(Menu::getStatus, StatusConstant.TRUE);
            List<Menu> childList = menuMapper.selectList(menuWrapper);
            Assert.isTrue(CollectionUtils.isEmpty(childList), "此菜单包含子菜单,请先删除子菜单");
        }

        List<MenuFunction> list = menuFunctionService.selectByMenuCode(t.getMenuCode());
        List<String> mfList = list.stream().map(mf -> mf.getFunctionCode()).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(mfList)) {
            //删除角色按钮权限
            rolePrivilegeService.delete(PrivilegeTypeConstant.FUNCTION, mfList);
        }
        //删除角色菜单权限
        rolePrivilegeService.delete(PrivilegeTypeConstant.MENU, Arrays.asList(t.getMenuCode()));
        //删除菜单按钮
        menuFunctionService.deleteMenuFunctionByMenuCode(menuCode);
        //删除菜单
        menuMapper.deleteById(t.getId());
    }

    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public void dragMenu(DragMenuDto dragMenuDto) {
        Assert.hasText(dragMenuDto.getMenuCode(), "拖拽菜单不能为空");
        Assert.hasText(dragMenuDto.getTargetCode(), "被指定菜单不能为空");
        Assert.hasText(dragMenuDto.getMoveType(), "拖拽类型不能为空");

        Menu menu = menuService.selectMenuByMenuCode(dragMenuDto.getMenuCode());
        Menu targetMenu = menuService.selectMenuByMenuCode(dragMenuDto.getTargetCode());
        if (!Objects.equals(menu.getParentCode(), targetMenu.getParentCode())) {
            throw new RuntimeException("不在同一个父菜单无法拖拽");
        }

        Date date = new Date();

        if (menu.getDisplayOrder() < targetMenu.getDisplayOrder()) {
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getParentCode, menu.getParentCode());

            Integer targetIndex;
            if (DragMenuConstant.PREV.equals(dragMenuDto.getMoveType())) {
                targetIndex = targetMenu.getDisplayOrder() - 1;
            } else if (DragMenuConstant.NEXT.equals(dragMenuDto.getMoveType())) {
                targetIndex = targetMenu.getDisplayOrder();
            } else {
                throw new RuntimeException("拖拽类型错误");
            }
            menuWrapper.between(Menu::getDisplayOrder, menu.getDisplayOrder(), targetIndex);
            List<Menu> menus = menuMapper.selectList(menuWrapper);

            if (!CollectionUtils.isEmpty(menus)) {
                for (Menu t : menus) {
                    t.setDisplayOrder(t.getDisplayOrder() - 1);
                    if (t.getId().equals(menu.getId())) {
                        t.setDisplayOrder(targetIndex);
                    }
                    t.setLastUpdateTime(date);

                    menuMapper.updateById(t);
                }
            }
        }

        if (menu.getDisplayOrder() > targetMenu.getDisplayOrder()) {
            LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
            menuWrapper.eq(Menu::getParentCode, menu.getParentCode());

            Integer targetIndex;
            if (DragMenuConstant.PREV.equals(dragMenuDto.getMoveType())) {
                targetIndex = targetMenu.getDisplayOrder();
            } else if (DragMenuConstant.NEXT.equals(dragMenuDto.getMoveType())) {
                targetIndex = targetMenu.getDisplayOrder() + 1;
            } else {
                throw new RuntimeException("拖拽类型错误");
            }

            menuWrapper.between(Menu::getDisplayOrder, targetIndex, menu.getDisplayOrder());
            List<Menu> menus = menuMapper.selectList(menuWrapper);

            if (!CollectionUtils.isEmpty(menus)) {
                for (Menu t : menus) {
                    t.setDisplayOrder(t.getDisplayOrder() + 1);
                    if (t.getId().equals(menu.getId())) {
                        t.setDisplayOrder(targetIndex);
                    }
                    t.setLastUpdateTime(date);
                    menuMapper.updateById(t);
                }
            }
        }
    }

    /**
     * 根据菜单编号查询菜单
     *
     * @param menuCode
     * @return
     */
    public Menu selectMenuByMenuCode(String menuCode) {
        LambdaQueryWrapper<Menu> menuWrapper = new LambdaQueryWrapper<>();
        menuWrapper.eq(Menu::getMenuCode, menuCode);
        return menuMapper.selectOne(menuWrapper);
    }
}
