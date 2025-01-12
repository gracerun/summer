package com.gracerun.repository;

import lombok.*;

/**
 * 分页及汇总
 *
 * @author hukailong
 * @since 2023/9/13 5:37 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PageSumInfo<T, S> extends PageInfo<T> {
    /**
     * 汇总数据
     */
    private S sumData;
}
