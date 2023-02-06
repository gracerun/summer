package cn.happyselect.sys.mapper;

import cn.happyselect.sys.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 用户角色关系数据访问层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 查找用户角色
     *
     * @param userId 用户ID
     * @return 返回角色列表
     */
    List<String> selectByUserId(String userId);

}
