package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.sys.bean.dto.RolePrivilegeUpdateDto;
import cn.happyselect.sys.bean.dto.RoleTmpCreateDto;
import cn.happyselect.sys.bean.dto.RoleTmpUpdateDto;
import cn.happyselect.sys.constant.RoleLevelConstant;
import cn.happyselect.sys.entity.RoleTmp;
import cn.happyselect.sys.mapper.RoleTmpMapper;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色模板service服务类
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月07日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class RoleTmpServiceImpl {

    @Resource
    private RoleTmpMapper roleTmpMapper;

    @Autowired
    private RoleTmpServiceImpl roleTmpService;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private RolePrivilegeBizService rolePrivilegeBizService;

    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    @Transactional
    public RoleTmp createRoleTmp(RoleTmpCreateDto pojo) {
        RoleTmp t = new RoleTmp();
        BeanUtil.copyProperties(pojo, t, CopyOptions.create().ignoreNullValue());
        t.setRoleCode(IdWorker.getIdStr());
        t.setLevel(RoleLevelConstant.LEVEL_1);
        t.setSystemType(pojo.getSystemType());
        roleTmpMapper.insert(t);

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

    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    public RoleTmp updateRoleTmp(RoleTmpUpdateDto pojo) {
        RoleTmp t = roleTmpService.selectRoleTmp(pojo.getRoleCode());
        Assert.notNull(t, "此角色不存在");

        if (StringUtils.hasText(pojo.getRoleName())) {
            t.setRoleName(pojo.getRoleName());
        }

        if (StringUtils.hasText(pojo.getSystemType())) {
            t.setSystemType(pojo.getSystemType());
        }

        if (StringUtils.hasText(pojo.getStatus())) {
            t.setStatus(pojo.getStatus());
        }
        roleTmpMapper.updateById(t);
        return t;
    }

    @PreAuthorize("T(cn.happyselect.base.enums.SystemType).OPER == authentication.systemType")
    @Transactional
    public void deleteRoleTmp(String roleCode) {
        Assert.hasText(roleCode, "角色编号不能为空");
        RoleTmp t = roleTmpService.selectRoleTmp(roleCode);
        Assert.notNull(t, "此角色不存在");

        //删除角色模板权限
        rolePrivilegeService.delete(roleCode);
        //删除角色模板
        roleTmpMapper.deleteById(t.getId());
    }

    /**
     * 根据角色编号查询角色
     *
     * @param roleCode
     * @return
     */
    public RoleTmp selectRoleTmp(String roleCode) {
        Assert.hasText(roleCode, "角色编码不能为空");
        LambdaQueryWrapper<RoleTmp> query = new LambdaQueryWrapper<>();
        query.eq(RoleTmp::getRoleCode, roleCode);
        return roleTmpMapper.selectOne(query);
    }

    public List<RoleTmp> selectBySystemType(String systemType) {
        Assert.hasText(systemType, "系统类型不能为空");
        LambdaQueryWrapper<RoleTmp> query = new LambdaQueryWrapper<>();
        query.eq(RoleTmp::getSystemType, systemType);
        query.eq(RoleTmp::getStatus, StatusConstant.TRUE);
        return roleTmpMapper.selectList(query);
    }
}
