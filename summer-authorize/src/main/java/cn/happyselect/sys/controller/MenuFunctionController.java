package cn.happyselect.sys.controller;

import cn.happyselect.sys.entity.MenuFunction;
import cn.happyselect.sys.service.MenuFunctionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 菜单功能控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月07日
 * @since 1.8
 */
@Slf4j
@Api(value = "菜单功能", tags = "菜单功能")
@RestController
@RequestMapping("/menuFunction")
public class MenuFunctionController {

    @Autowired
    private MenuFunctionServiceImpl menuFunctionService;


    /**
     * 条件分页查询菜单功能
     *
     * @param queryDto 条件信息
     * @return
     */
//    @ApiOperation(value = "分页查询菜单功能", notes = "按条件分页查询菜单功能", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<MenuFunction> findByPage(@Valid @RequestBody MenuFunctionDto queryDto) {
//        return menuFunctionService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 新增菜单功能
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增菜单功能数据", notes = "新增菜单功能数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public MenuFunction create(@Valid @RequestBody MenuFunction pojo) {
        return menuFunctionService.addMenuFunction(pojo);
    }

    /**
     * 更新菜单功能
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新菜单功能数据", notes = "更新菜单功能数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public MenuFunction update(@RequestBody MenuFunction pojo) {
        return menuFunctionService.updateMenuFunction(pojo);
    }

    /**
     * 删除菜单功能
     *
     * @param functionCode
     * @return
     */
    @ApiOperation(value = "物理删除菜单功能数据", notes = "物理删除菜单功能数据", httpMethod = "POST", produces = "application/json")
    @PostMapping(value = "/delete/{functionCode}")
    public void delete(@PathVariable String functionCode) {
        menuFunctionService.deleteMenuFunction(functionCode);
    }

}
