package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.DragMenuDto;
import cn.happyselect.sys.entity.Menu;
import cn.happyselect.sys.service.MenuServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜单控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月07日
 * @since 1.8
 */
@Slf4j
@Api(value = "菜单", tags = "菜单")
@RestController

@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuServiceImpl menuBizService;

    /**
     * 新增菜单
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增菜单数据", notes = "新增菜单数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public Menu create(@Valid @RequestBody Menu pojo) {
        return menuBizService.createMenu(pojo);
    }

    /**
     * 更新菜单
     *
     * @param menu
     * @return
     */
    @ApiOperation(value = "更新菜单数据", notes = "更新菜单数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public Menu update(@Valid @RequestBody Menu menu) {
        return menuBizService.updateMenu(menu);
    }

    /**
     * 查询菜单树,异步加载模式
     *
     * @param menuCode
     * @return
     */
    @ApiOperation(value = "查询菜单树,异步加载模式", notes = "查询子菜单,异步加载模式", httpMethod = "GET", produces = "application/json")
    @GetMapping("/tree")
    public List<Menu> tree(String menuCode) {
        return menuBizService.tree(menuCode);
    }

    /**
     * 删除菜单
     *
     * @param menuCode
     * @return
     */
    @ApiOperation(value = "删除菜单", notes = "删除菜单", httpMethod = "POST", produces = "application/json")
    @PostMapping("/delete/{menuCode}")
    public void delete(@PathVariable String menuCode) {
        menuBizService.deleteMenu(menuCode);
    }

    /**
     * 修改菜单顺序
     *
     * @param dragMenuDto
     * @return
     */
    @ApiOperation(value = "修改菜单顺序", notes = "修改菜单顺序", httpMethod = "POST", produces = "application/json")
    @PostMapping("/drag")
    public void drag(@Valid @RequestBody DragMenuDto dragMenuDto) {
        menuBizService.dragMenu(dragMenuDto);
    }

}
