package cn.happyselect.sys.service;

import cn.happyselect.base.bean.dto.UserContextFace;

/**
 * 用户上下文接口
 *
 * @author adc
 * @version 1.0.0
 * @date 5/24/21
 */
public interface UserContextInterface {

    /**
     * 加载用户上下文属性
     * @param dto
     */
    void load(UserContextFace dto);
}
