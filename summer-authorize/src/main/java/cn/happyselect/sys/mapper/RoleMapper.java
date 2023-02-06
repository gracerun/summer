package cn.happyselect.sys.mapper;

import cn.happyselect.sys.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 角色数据访问层
 *
 * @author yaodan
 * @created 2020年07月30日
 * @version 1.0
 * @since 1.8
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 查找用户已分配的角色
     *
     * @param userId 用户ID
     * @return 返回角色列表
     */
    List<Role> selectByUserId(String userId);
}
