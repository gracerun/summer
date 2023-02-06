package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.UserCreateDto;
import cn.happyselect.sys.bean.dto.UserUpdateDto;
import cn.happyselect.sys.entity.User;
import cn.happyselect.sys.service.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户控制层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月30日
 * @since 1.8
 */
@Slf4j
@Api(value = "用户", tags = "用户")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserServiceImpl userService;

    /**
     * 条件分页查询用户
     *
     * @param queryDto 条件信息
     * @return
     */
//    @Translation
//    @DataScope(field = "parentUserId", comparer = Comparer.EQU, dataType = DataType.USER)
//    @ApiOperation(value = "分页查询用户", notes = "按条件分页查询用户", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<User> findByPage(@Valid @RequestBody UserDto queryDto) {
//        return userService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 新增员工
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "新增员工数据", notes = "新增员工数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/create")
    public User create(@RequestBody UserCreateDto pojo) {
        return userService.createEmployee(pojo);
    }

    /**
     * 更新用户
     *
     * @param pojo
     * @return
     */
    @ApiOperation(value = "更新用户数据", notes = "更新用户数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/update")
    public User update(@RequestBody UserUpdateDto pojo) {
        return userService.updateUser(pojo, true);
    }

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "逻辑删除用户数据", notes = "逻辑删除用户数据", httpMethod = "POST", produces = "application/json")
    @PostMapping("/delete/{userId}")
    public void delete(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

}
