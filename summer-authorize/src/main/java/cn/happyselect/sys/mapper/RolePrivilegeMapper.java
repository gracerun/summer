package cn.happyselect.sys.mapper;

import cn.happyselect.sys.bean.dto.RolePrivilegeTreeQueryDto;
import cn.happyselect.sys.bean.dto.RoleUrlQueryDto;
import cn.happyselect.sys.bean.vo.MenuTreeVo;
import cn.happyselect.sys.bean.vo.RolePrivilegeTreeVo;
import cn.happyselect.sys.bean.vo.RoleUrlVo;
import cn.happyselect.sys.entity.RolePrivilege;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 角色权限关系数据访问层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
public interface RolePrivilegeMapper extends BaseMapper<RolePrivilege> {

    /**
     * 批量插入角色权限关系,忽略重复的数据
     * @param list
     */
    void batchInsert(List<RolePrivilege> list);
    /**
     * 查询指定角色的所有权限关联的url
     *
     * @param roleUrlQueryDto
     * @return
     */
    List<RoleUrlVo> selectAllPrivilegeUrl(RoleUrlQueryDto roleUrlQueryDto);

    /**
     * 查询指定角色的仅功能权限关联的url
     *
     * @param roleUrlQueryDto
     * @return
     */
    List<String> selectFunctionUrl(RoleUrlQueryDto roleUrlQueryDto);

    /**
     * 查询当前角色给目标角色的授权树,区分已授权的权限
     *
     * @param privilegeTreeQueryBean
     * @return
     */
    List<RolePrivilegeTreeVo> findRolePrivilegeManageTree(RolePrivilegeTreeQueryDto privilegeTreeQueryBean);

    /**
     * 查询指定角色的权限树
     *
     * @param privilegeTreeQueryBean
     * @return
     */
    List<RolePrivilegeTreeVo> findRolePrivilegeValidTree(RolePrivilegeTreeQueryDto privilegeTreeQueryBean);

    /**
     * 查询首页菜单树
     *
     * @param privilegeTreeQueryBean
     * @return
     */
    List<MenuTreeVo> selectIndexMenu(RolePrivilegeTreeQueryDto privilegeTreeQueryBean);
}
