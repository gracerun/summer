package com.gracerun.summermq.service.impl;

import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.bean.MessagePage;
import com.gracerun.summermq.bean.MessageQuery;
import com.gracerun.summermq.service.MessagePersistentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 消息持久化服务
 *
 * @author Tom
 * @version 1.0.0
 * @date 1/11/22
 */
@Slf4j
public class DefaultMessagePersistentService implements MessagePersistentService {

    private String pageSql = "select id, optimistic, status, create_time, last_update_time, business_no, namespace, business_type, times, content, remark, next_execute_time, time, ip, create_span_id, last_update_span_id, timeout from business_msg WHERE ( status = :status and next_execute_time <= :next_execute_time ) order by id desc limit :limitStart , :limitSize";
    private String countSql = "select count(*) from business_msg WHERE (status = :status and next_execute_time <= :next_execute_time )";

    private String insertSql = "insert into business_msg ( id, optimistic, status, create_time, last_update_time, business_no, namespace, business_type, times, content, remark, next_execute_time, create_span_id, timeout ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    private String updateSql = "update business_msg SET status = ?, create_time = ?, last_update_time = ?, business_no = ?, namespace = ?, business_type = ?, times = ?, content = ?, remark = ?, next_execute_time = ?, time = ?, ip = ?, create_span_id = ?, last_update_span_id = ?, timeout = ?, optimistic = optimistic + 1 where id = ? and optimistic = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public MessagePage findByPage(MessageQuery messageQuery) {
        int limitStart = (messageQuery.getPageNum() - 1) * messageQuery.getPageSize();
        int limitSize = messageQuery.getPageSize();

        final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        final MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("status", messageQuery.getStatus());
        mapSqlParameterSource.addValue("next_execute_time", messageQuery.getNextExecuteTime());
        mapSqlParameterSource.addValue("limitStart", limitStart);
        mapSqlParameterSource.addValue("limitSize", limitSize);
        final Integer count = namedParameterJdbcTemplate.queryForObject(countSql, mapSqlParameterSource, Integer.TYPE);
        log.info("count:{}", count);
        final List<MessageBody> list = namedParameterJdbcTemplate.query(pageSql, mapSqlParameterSource, new BeanPropertyRowMapper<>(MessageBody.class));

        MessagePage p = new MessagePage();
        p.setRecords(list);
        p.setSize(messageQuery.getPageSize());
        p.setTotal(count);
        return p;
    }

    @Override
    public int save(MessageBody message) {
        if (StringUtils.hasText(message.getRemark())) {
            message.setRemark(message.getRemark().substring(0, Math.min(message.getRemark().length(), 80)));
        }

        final int size = jdbcTemplate.update(insertSql,
                message.getId(),
                message.getOptimistic(),
                message.getStatus(),
                message.getCreateTime(),
                message.getLastUpdateTime(),
                message.getBusinessNo(),
                message.getNamespace(),
                message.getBusinessType(),
                message.getTimes(),
                message.getContent(),
                message.getRemark(),
                message.getNextExecuteTime(),
                message.getCreateSpanId(),
                message.getTimeout());
        return size;
    }

    @Override
    public int updateById(MessageBody message) {
        if (StringUtils.hasText(message.getRemark())) {
            message.setRemark(message.getRemark().substring(0, Math.min(message.getRemark().length(), 80)));
        }

        final int size = jdbcTemplate.update(updateSql,
                message.getStatus(),
                message.getCreateTime(),
                message.getLastUpdateTime(),
                message.getBusinessNo(),
                message.getNamespace(),
                message.getBusinessType(),
                message.getTimes(),
                message.getContent(),
                message.getRemark(),
                message.getNextExecuteTime(),
                message.getTime(),
                message.getIp(),
                message.getCreateSpanId(),
                message.getLastUpdateSpanId(),
                message.getTimeout(),
                message.getId(),
                message.getOptimistic());
        return size;
    }
}
