package cn.happyselect.sys.mapper;

import cn.happyselect.sys.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 菜单数据访问层
 *
 * @author yaodan
 * @version 1.0
 * @created 2020年07月29日
 * @since 1.8
 */
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据parentcode查询同级中的最大序号
     *
     * @param parentCode
     * @return
     */
    Integer selectMaxOrderByParentCode(String parentCode);
}
