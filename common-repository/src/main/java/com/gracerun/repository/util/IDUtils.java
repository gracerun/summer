package com.gracerun.repository.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;

/**
 * 分布式ID生成，Long
 *
 * @author damoguyansi
 * @Date 2022-11-28
 */
@Slf4j
public class IDUtils {

    public static Long nextId() {
        return IdWorker.getId();
    }

    public static String nextIdStr() {
        return IdWorker.getIdStr();
    }

}
