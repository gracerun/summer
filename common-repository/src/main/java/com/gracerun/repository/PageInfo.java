package com.gracerun.repository;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * Created by damoguyansi
 */
@Data
@Builder
@Accessors(chain = true)
public class PageInfo<T> implements Serializable {
    /**
     * 总数量
     */
    private long totalElements;

    /**
     * 内容
     */
    private List<T> content;

    public PageInfo(long totalElements, List<T> content) {
        this.totalElements = totalElements;
        this.content = content;
    }

    public PageInfo() {
    }
}
