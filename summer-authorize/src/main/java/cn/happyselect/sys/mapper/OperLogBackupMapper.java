package cn.happyselect.sys.mapper;

import cn.happyselect.sys.bean.dto.OperLogBackupQueryDto;
import cn.happyselect.sys.entity.OperLogBackup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 操作日志备份表数据访问层
 *
 * @author yaodan
 * @date 2020年09月01日
 * @version 1.0
 * @since 1.8
 */
public interface OperLogBackupMapper extends BaseMapper<OperLogBackup> {

    int batchBackup(OperLogBackupQueryDto dto);
}
