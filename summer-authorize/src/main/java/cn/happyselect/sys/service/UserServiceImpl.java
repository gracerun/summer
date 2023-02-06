package cn.happyselect.sys.service;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.happyselect.sys.util.UserHolder;
import cn.happyselect.sys.bean.dto.UserCreateDto;
import cn.happyselect.sys.bean.dto.UserRoleUpdateDto;
import cn.happyselect.sys.bean.dto.UserUpdateDto;
import cn.happyselect.sys.constant.IdentityTypeConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import cn.happyselect.sys.constant.UserStatusConstant;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.entity.RolePrivilege;
import cn.happyselect.sys.entity.RoleTmp;
import cn.happyselect.sys.entity.User;
import cn.happyselect.sys.mapper.RoleMapper;
import cn.happyselect.sys.mapper.RolePrivilegeMapper;
import cn.happyselect.sys.mapper.UserMapper;
import cn.happyselect.sys.util.PasswordUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户service服务类
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月30日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.]+@[a-zA-Z0-9-]+[\\.a-zA-Z]+$");
    public static final Pattern PHONE_PATTERN = Pattern.compile("^[1]\\d{10}$");
    public static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\.]+$");

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RolePrivilegeMapper rolePrivilegeMapper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private RoleTmpServiceImpl roleTmpService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private CurrentUserBizService currentUserBizService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${default.password:1234qwer}")
    private String defaultPassword;

    /**
     * 添加用户
     * 不依赖登录用户
     *
     * @param pojo
     * @return
     */
    @Transactional
    public User createUser(@Valid UserCreateDto pojo) {
        Assert.notNull(pojo.getSystemType(), "系统类型不能为空");
        Assert.notNull(pojo.getUserType(), "用户类型不能为空");

        SystemType systemType = pojo.getSystemType();

        if (UserType.ADMIN == pojo.getUserType() && (SystemType.CUSTOMER == systemType
                || SystemType.AGENT == systemType)) {
            Assert.hasText(pojo.getBusinessNo(), "业务编号不能为空");
        }

        Assert.isTrue(userService.notExistUsername(pojo.getSystemType(), pojo.getUsername(), null), "用户名已被使用");
        Assert.isTrue(userService.notExistEmail(pojo.getSystemType(), pojo.getEmail(), null), "邮箱已被使用");
        Assert.isTrue(userService.notExistPhone(pojo.getSystemType(), pojo.getPhone(), null), "手机号已被使用");

        User user = new User();
        BeanUtil.copyProperties(pojo, user, CopyOptions.create().ignoreNullValue());
        user.setSystemType(systemType.name());
        if (!StringUtils.hasText(user.getUserId())) {
            String userId = IdWorker.getIdStr();
            user.setUserId(userId);
        }
        if (!StringUtils.hasText(user.getStatus())) {
            user.setStatus(StatusConstant.TRUE);
        }
        UserContext userContext = UserHolder.getCurrentUser();
        if (userContext != null) {
            user.setCreator(userContext.getUserId());
        }

        if (UserType.EMPLOYEE == pojo.getUserType()) {
            User parentUser = userService.selectUserByBusinessNo(UserHolder.getCurrentUser().getBusinessNo(), systemType, UserType.ADMIN);
            Assert.notNull(parentUser, "用户数据错误");
            user.setParentUserId(parentUser.getUserId());
            user.setUserType(UserType.EMPLOYEE.name());
        } else if (UserType.ADMIN == pojo.getUserType()) {
            User parentUser = userService.selectUserByBusinessNo(null, SystemType.OPER, UserType.ADMIN);
            Assert.notNull(parentUser, "用户数据错误");
            user.setParentUserId(parentUser.getUserId());
            user.setUserType(UserType.ADMIN.name());
        }

        if (StringUtils.hasText(pojo.getPassword())) {
            String encryptPassword = PasswordUtil.encode(pojo.getPassword());
            user.setPassword(encryptPassword);
        } else {
            String encryptPassword = PasswordUtil.encode(defaultPassword);
            user.setPassword(encryptPassword);
        }
        user.setExpireTime(new Date());
        userMapper.insert(user);

        if (!CollectionUtils.isEmpty(pojo.getRoleCodes())) {
            //员工配置角色
            if (UserType.EMPLOYEE == pojo.getUserType()) {
                UserRoleUpdateDto userRoleUpdateDto = new UserRoleUpdateDto();
                userRoleUpdateDto.setUserId(user.getUserId());
                userRoleUpdateDto.setAddRoleCodes(StringUtils.collectionToDelimitedString(pojo.getRoleCodes(), ","));
                userRoleService.updateUserRole(userRoleUpdateDto);
            } else {
                //配置服务商或商户角色
                userRoleService.createUserRole(user.getUserId(), pojo.getRoleCodes());
            }
        }

        //初始化用户管理角色
        initRole(user.getUserId(), systemType);
        return user;
    }

    public void initRole(String userId, SystemType systemType) {
        if (SystemType.AGENT != systemType && SystemType.CUSTOMER != systemType) {
            return;
        }
        List<RoleTmp> roleTmps = roleTmpService.selectBySystemType(systemType.name());
        if (CollectionUtils.isEmpty(roleTmps)) {
            return;
        }

        List<String> roleCodes = roleTmps.stream().map(r -> r.getRoleCode()).collect(Collectors.toList());
        List<Role> roleList = new ArrayList<>(roleTmps.size());
        Map<String, String> roleCodeRelation = new HashMap<>(roleTmps.size());

        Date date = new Date();
        roleTmps.stream().forEach(t -> {
            Role role = new Role();
            role.setSystemType(t.getSystemType());
            role.setUserId(userId);
            role.setStatus(StatusConstant.TRUE);
            role.setRoleName(t.getRoleName());
            role.setRoleCode(IdWorker.getIdStr());
            role.setLevel(t.getLevel());
            role.setCreateTime(date);
            role.setLastUpdateTime(date);
            role.setOptimistic(0);
            roleList.add(role);
            roleCodeRelation.put(t.getRoleCode(), role.getRoleCode());
        });

        List<RolePrivilege> rolePrivilegeList = rolePrivilegeService.selectList(roleCodes);
        rolePrivilegeList.stream().forEach(t -> t.setRoleCode(roleCodeRelation.get(t.getRoleCode())));


        roleList.forEach(roleMapper::insert);
        rolePrivilegeList.forEach(rolePrivilegeMapper::insert);
    }

    /**
     * 当前登录用户添加员工
     *
     * @param pojo
     * @return
     */
    @Transactional
    public User createEmployee(@Valid UserCreateDto pojo) {
        UserContext userContext = UserHolder.getCurrentUser();

        SystemType systemType = userContext.getSystemType();
        Assert.notNull(systemType, "系统类型错误");

        String businessNo = userContext.getBusinessNo();
        if (SystemType.CUSTOMER == systemType
                || SystemType.AGENT == systemType) {
            Assert.hasText(businessNo, "业务编号不能为空");
        }

        pojo.setSystemType(systemType);
        pojo.setUserType(UserType.EMPLOYEE);
        return createUser(pojo);
    }

    /**
     * 更新用户信息
     *
     * @param dto
     * @return
     */
    public User updateUser(@Valid UserUpdateDto dto, boolean isValid) {
        User t = userService.selectByUserId(dto.getUserId());
        Assert.notNull(t, "此用户不存在");

        SystemType systemType = SystemType.resolve(t.getSystemType());
        Assert.isTrue(userService.notExistUsername(systemType, dto.getUsername(), t.getUserId()), "用户名已被使用");
        Assert.isTrue(userService.notExistEmail(systemType, dto.getEmail(), t.getUserId()), "邮箱已被使用");
        Assert.isTrue(userService.notExistPhone(systemType, dto.getPhone(), t.getUserId()), "手机号已被使用");

        if (isValid) {
            UserHolder.validUserIdRelation(t.getUserId(), t.getParentUserId());
        }
        BeanUtil.copyProperties(dto, t, CopyOptions.create().ignoreNullValue());
        if (StringUtils.hasText(dto.getPassword())) {
            t.setPassword(PasswordUtil.encode(dto.getPassword()));
        }
        userMapper.updateById(t);
        currentUserBizService.refreshUserContext(t);
        return t;
    }

    @Transactional
    public void deleteUser(String userId) {
        Assert.hasText(userId, "用户ID不能为空");
        User t = userService.selectByUserId(userId);
        Assert.notNull(t, "此用户不存在");
        UserHolder.validUserIdRelation(t.getUserId(), t.getParentUserId());
        if (UserType.EMPLOYEE.name().equalsIgnoreCase(t.getUserType())) {
            userRoleService.delete(t.getUserId(), null);
        }
        t.setStatus(StatusConstant.DELETE);
        userMapper.updateById(t);
    }

    public User resetPassword(String userId, String password) {
        String encryptPassword = PasswordUtil.encode(password);
        User t = userService.selectByUserId(userId);
        Assert.notNull(t, "此用户不存在");
        if (UserStatusConstant.LOCKED.equals(t.getStatus())) {
            t.setStatus(UserStatusConstant.TRUE);
        }
        t.setExpireTime(new DateTime().plusDays(180).toDate());
        t.setPassword(encryptPassword);
        userMapper.updateById(t);
        return t;
    }

    /**
     * 清空缓存
     *
     * @param userId
     */
    public void clearCache(String userId) {
        try {
            stringRedisTemplate.delete(RedisKeyConstant.USER_LOCKED + userId);
            stringRedisTemplate.delete(RedisKeyConstant.LOGIN_FAIL_ATTEMPTS + userId);
        } catch (Exception e) {
            log.error("用户缓存清理失败:" + e.getMessage(), e);
        }
    }

    public User selectByUserId(String userId) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUserId, userId);
        return userMapper.selectOne(query);
    }

    public User selectUserByBusinessNo(String businessNo, SystemType systemType, UserType userType) {
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(businessNo)) {
            query.eq(User::getBusinessNo, businessNo);
        }
        query.eq(User::getSystemType, systemType.name());
        query.eq(User::getUserType, userType.name());
        return userMapper.selectOne(query);
    }

    public boolean notExistUsername(SystemType systemType, String username, String notEqualsUserId) {
        if (!StringUtils.hasText(username)) {
            return true;
        }
        return findByIdentityType(systemType, IdentityTypeConstant.USERNAME, username, notEqualsUserId) == null;
    }

    public boolean notExistEmail(SystemType systemType, String email, String notEqualsUserId) {
        if (!StringUtils.hasText(email)) {
            return true;
        }
        return findByIdentityType(systemType, IdentityTypeConstant.EMAIL, email, notEqualsUserId) == null;
    }

    public boolean notExistPhone(SystemType systemType, String phone, String notEqualsUserId) {
        if (!StringUtils.hasText(phone)) {
            return true;
        }
        return findByIdentityType(systemType, IdentityTypeConstant.PHONE, phone, notEqualsUserId) == null;
    }

    public String findIdentifierType(String identifier) {
        if (EMAIL_PATTERN.matcher(identifier).matches()) {
            return IdentityTypeConstant.EMAIL;
        } else if (PHONE_PATTERN.matcher(identifier).matches()) {
            return IdentityTypeConstant.PHONE;
        } else {
            return IdentityTypeConstant.USERNAME;
        }
    }

    public User findByIdentityType(SystemType systemType, String identityType, String identifier, String notEqualsUserId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getSystemType, systemType.name());
        if (IdentityTypeConstant.EMAIL.equals(identityType)) {
            wrapper.eq(User::getEmail, identifier);
        } else if (IdentityTypeConstant.PHONE.equals(identityType)) {
            wrapper.eq(User::getPhone, identifier);
        } else {
            wrapper.eq(User::getUsername, identifier);
        }

        if (StringUtils.hasText(notEqualsUserId)) {
            wrapper.ne(User::getUserId, notEqualsUserId);
        }
        return userMapper.selectOne(wrapper);
    }

    public User findByIdentifier(SystemType systemType, String identifier) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getSystemType, systemType.name());
        if (EMAIL_PATTERN.matcher(identifier).matches()) {
            wrapper.eq(User::getEmail, identifier);
        } else if (PHONE_PATTERN.matcher(identifier).matches()) {
            wrapper.eq(User::getPhone, identifier);
        } else {
            wrapper.eq(User::getUsername, identifier);
        }
        return userMapper.selectOne(wrapper);
    }

}
