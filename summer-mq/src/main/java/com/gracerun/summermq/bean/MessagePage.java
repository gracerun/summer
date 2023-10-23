package com.gracerun.summermq.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class MessagePage {

    private List<MessageBody> records;

    private long total;

    private int size;

    public List<MessageBody> getRecords() {
        return records;
    }

    public void setRecords(List<MessageBody> records) {
        this.records = records;
    }

    public long getPages() {
        if (getSize() == 0) {
            return 0L;
        }
        long pages = getTotal() / getSize();
        if (getTotal() % getSize() != 0) {
            pages++;
        }
        return pages;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("total", total)
                .append("size", size)
                .toString();
    }
}
