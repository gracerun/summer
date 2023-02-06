package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.sys.annotation.PrivilegeSync;
import cn.happyselect.sys.bean.dto.RolePrivilegeTreeQueryDto;
import cn.happyselect.sys.bean.dto.RolePrivilegeUpdateDto;
import cn.happyselect.sys.bean.vo.RolePrivilegeTreeVo;
import cn.happyselect.sys.constant.PrivilegeTypeConstant;
import cn.happyselect.sys.constant.RoleCodeConstant;
import cn.happyselect.sys.entity.Role;
import cn.happyselect.sys.entity.RolePrivilege;
import cn.happyselect.sys.entity.RoleTmp;
import cn.happyselect.sys.mapper.RolePrivilegeMapper;
import cn.happyselect.sys.mapper.UserRoleMapper;
import cn.happyselect.sys.util.QuickUnionPathCompressionUF;
import cn.happyselect.sys.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 角色权限业务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-11
 */
@Service
@Slf4j
public class RolePrivilegeBizService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RolePrivilegeMapper rolePrivilegeMapper;

    @Autowired
    private RolePrivilegeServiceImpl rolePrivilegeService;

    @Autowired
    private RoleServiceImpl roleService;

    @Autowired
    private RoleTmpServiceImpl roleTmpService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 修改角色权限
     *
     * @param dto
     * @return
     */
    @Transactional
    @PrivilegeSync
    public void updateRolePrivilege(RolePrivilegeUpdateDto dto) {
        String roleCode = dto.getRoleCode();
        Assert.hasText(roleCode, "授权角色不能为空");

        if (!StringUtils.hasText(dto.getDeleteMenuCodes()) && !StringUtils.hasText(dto.getAddMenuCodes()) && !StringUtils.hasText(dto.getDeleteMenuFunctionCodes()) && !StringUtils.hasText(dto.getAddMenuFunctionCodes())) {
            throw new RuntimeException("修改角色权限参数不能为空");
        }

        Collection<String> adminRoleCodes = roleService.selectAllAdminRole(SystemType.OPER);

        Role t = roleService.selectRole(roleCode);
        if (t == null) {
            //查询角色模板
            RoleTmp roleTmp = roleTmpService.selectRoleTmp(roleCode);
            Assert.notNull(roleTmp, "此角色不存在");
            Assert.isTrue(SystemType.OPER.equals(UserHolder.getCurrentUser().getSystemType()), "无操作权限,请联系管理员!");
        } else {
            UserHolder.validUserIdRelation(t.getUserId());
        }

        String addMenuCodes = dto.getAddMenuCodes();
        String deleteMenuCodes = dto.getDeleteMenuCodes();
        String addMenuFunctionCodes = dto.getAddMenuFunctionCodes();
        String deleteMenuFunctionCodes = dto.getDeleteMenuFunctionCodes();

        if (StringUtils.hasText(deleteMenuCodes)) {
            if (!CollectionUtils.isEmpty(adminRoleCodes) && adminRoleCodes.contains(roleCode)) {
                throw new RuntimeException("不允许删除管理员权限");
            }
            rolePrivilegeService.delete(roleCode, PrivilegeTypeConstant.MENU, Arrays.asList(deleteMenuCodes.split(",")));
        }

        if (StringUtils.hasText(deleteMenuFunctionCodes)) {
            if (!CollectionUtils.isEmpty(adminRoleCodes) && adminRoleCodes.contains(roleCode)) {
                throw new RuntimeException("不允许删除管理员权限");
            }
            rolePrivilegeService.delete(roleCode, PrivilegeTypeConstant.FUNCTION, Arrays.asList(deleteMenuFunctionCodes.split(",")));
        }

        if (StringUtils.hasText(addMenuCodes)) {
            List<RolePrivilege> list = Stream.of(addMenuCodes.split(",")).map(s -> {
                RolePrivilege record = new RolePrivilege();
                record.setType(PrivilegeTypeConstant.MENU);
                record.setRoleCode(roleCode);
                record.setPrivilegeCode(s);
                return record;
            }).collect(Collectors.toList());

            rolePrivilegeMapper.batchInsert(list);
        }

        if (StringUtils.hasText(addMenuFunctionCodes)) {
            List<RolePrivilege> list = Stream.of(addMenuFunctionCodes.split(",")).map(s -> {
                RolePrivilege record = new RolePrivilege();
                record.setType(PrivilegeTypeConstant.FUNCTION);
                record.setRoleCode(roleCode);
                record.setPrivilegeCode(s);
                return record;
            }).collect(Collectors.toList());
            rolePrivilegeMapper.batchInsert(list);
        }

        validCompleteness(roleCode);
    }

    /**
     * 查询角色权限树集合 校验树是否完整
     *
     * @param roleCode
     */
    private void validCompleteness(String roleCode) {
        RolePrivilegeTreeQueryDto privilegeTreeQueryBean = new RolePrivilegeTreeQueryDto();
        privilegeTreeQueryBean.setAuthRoleCode(roleCode);
        privilegeTreeQueryBean.setStatus(StatusConstant.TRUE);
        List<RolePrivilegeTreeVo> list = rolePrivilegeMapper.findRolePrivilegeValidTree(privilegeTreeQueryBean);

        if (!CollectionUtils.isEmpty(list)) {
            int[][] nodes = new int[list.size()][2];
            Map<String, Integer> id = new HashMap<>(list.size());
            int index = 0;
            for (int i = 0, length = list.size(); i < length; i++) {
                Integer p = id.get(list.get(i).getNodeId());
                if (p == null) {
                    p = index++;
                    id.put(list.get(i).getNodeId(), p);
                }
                Integer q = id.get(list.get(i).getParentNodeId());
                if (q == null) {
                    q = index++;
                    id.put(list.get(i).getParentNodeId(), q);
                }
                nodes[i][0] = p;
                nodes[i][1] = q;
            }

            QuickUnionPathCompressionUF qu = new QuickUnionPathCompressionUF(nodes);
            boolean validTree = qu.validTree();
            if (!validTree) {
                logger.info("is not tree, {}", list);
            }
            Assert.isTrue(validTree, "权限数据完整性校验失败");
        }
    }

    /**
     * 角色权限分配查询
     *
     * @param roleCode
     * @return
     */
    public List<RolePrivilegeTreeVo> findRolePrivilegeManageTree(String roleCode) {
        Assert.hasText(roleCode, "授权角色编码不能为空");

        List<String> roleCodes = new ArrayList<>();
        Role t = roleService.selectRole(roleCode);
        if (t == null) {
            //查询角色模板
            RoleTmp roleTmp = roleTmpService.selectRoleTmp(roleCode);
            Assert.notNull(roleTmp, "此角色不存在");
            if (SystemType.AGENT.name().equalsIgnoreCase(roleTmp.getSystemType())) {
                roleCodes.add(RoleCodeConstant.AGENT);
            } else if (SystemType.CUSTOMER.name().equalsIgnoreCase(roleTmp.getSystemType())) {
                roleCodes.add(RoleCodeConstant.CUSTOMER);
            } else {
                throw new RuntimeException("角色编码错误");
            }
        } else {
            UserHolder.validUserIdRelation(t.getUserId());
            roleCodes = userRoleMapper.selectByUserId(t.getUserId());
        }

        if (!roleCodes.contains(roleCode)) {
            roleCodes.add(roleCode);
        }
        RolePrivilegeTreeQueryDto dto = new RolePrivilegeTreeQueryDto();
        dto.setAuthRoleCode(roleCode);
        dto.setRoleCodes(roleCodes);
        dto.setStatus(StatusConstant.TRUE);
        List<RolePrivilegeTreeVo> rolePrivilegeManageTree = rolePrivilegeMapper.findRolePrivilegeManageTree(dto);

        Collection<String> adminRoleCodes = roleService.selectAllAdminRole(SystemType.OPER);

        if (!CollectionUtils.isEmpty(adminRoleCodes) && adminRoleCodes.contains(roleCode) && !CollectionUtils.isEmpty(rolePrivilegeManageTree)) {
            rolePrivilegeManageTree.stream().forEach(vo -> vo.setChkDisabled(StatusConstant.TRUE));
        }

        return rolePrivilegeManageTree;
    }

    public List<RolePrivilegeTreeVo> findByRoleCode(List<String> roleCodes) {
        if (CollectionUtils.isEmpty(roleCodes)) {
            return null;
        }
        RolePrivilegeTreeQueryDto dto = new RolePrivilegeTreeQueryDto();
        dto.setAuthRoleCode("");
        dto.setRoleCodes(roleCodes);
        dto.setStatus(StatusConstant.TRUE);
        List<RolePrivilegeTreeVo> rolePrivilegeManageTree = rolePrivilegeMapper.findRolePrivilegeManageTree(dto);
        return rolePrivilegeManageTree;
    }
}
