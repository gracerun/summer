package com.gracerun.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gracerun.business.mapper.BusinessMsgMapper;
import com.gracerun.component.util.RedisUtil;
import com.gracerun.log.annotation.Logging;
import com.gracerun.log.serializer.ToBlankSerializer;
import com.gracerun.repository.CommonServiceImpl;
import com.gracerun.repository.PageInfo;
import com.gracerun.repository.QueryHelpMybatisPlus;
import com.gracerun.repository.SuperEntity;
import com.gracerun.repository.util.ConvertUtil;
import com.gracerun.repository.util.PageUtil;
import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.constant.MessageStatusConstant;
import com.gracerun.summermq.constant.QueueConstant;
import com.gracerun.summermq.producer.RedisMessageProducer;
import com.gracerun.summermq.producer.SummerMQTemplate;
import com.gracerun.tool.bean.ResponseBean;
import com.gracerun.tool.constant.ResponseCode;
import com.gracerun.tool.exception.ServiceException;
import org.joda.time.DateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 业务消息
 *
 * @author minliangqin
 * @date 2023-04-17
 **/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class BusinessMsgService extends CommonServiceImpl<BusinessMsgMapper, BusinessMsg> {

    @Resource
    private BusinessMsgMapper businessMsgMapper;
    @Resource
    private SummerMQTemplate summerMQTemplate;
    @Resource
    private RedisMessageProducer redisMessageProducer;

    @Logging(serializeReturnUsing = ToBlankSerializer.class)
    public PageInfo<BusinessMsgDto> queryAll(BusinessMsgQueryParam query, Pageable pageable) {
        IPage<BusinessMsg> queryPage = PageUtil.toMybatisPage(pageable);
        IPage<BusinessMsg> page = businessMsgMapper.selectPage(queryPage, QueryHelpMybatisPlus.getPredicate(query));
        return ConvertUtil.convertPage(page, BusinessMsgDto.class);
    }

    public List<BusinessMsgDto> queryAll(BusinessMsgQueryParam query) {
        return ConvertUtil.convertList(businessMsgMapper.selectList(QueryHelpMybatisPlus.getPredicate(query)), BusinessMsgDto.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public int insert(BusinessMsgDto resources) {
        BusinessMsg entity = ConvertUtil.convert(resources, BusinessMsg.class);
        Date date = new Date();
        entity.setOptimistic(0);
        entity.setCreateTime(date);
        entity.setLastUpdateTime(date);
        return businessMsgMapper.insert(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int updateById(BusinessMsgDto resources) {
        BusinessMsg entity = ConvertUtil.convert(resources, BusinessMsg.class);
        Date date = new Date();
        entity.setLastUpdateTime(date);
        int ret = businessMsgMapper.updateById(entity);
        return ret;
    }

    public BusinessMsg getById(Long id) {
        return businessMsgMapper.selectById(id);
    }

    public ResponseBean saveAndExecute(BusinessMsg businessMsg) {
        final MessageBody messageBody = new MessageBody();
        BeanUtil.copyProperties(businessMsg, messageBody, CopyOptions.create().ignoreNullValue());
        summerMQTemplate.sendAndSave(messageBody);
        return ResponseBean.successBean();
    }

    public void updateAndExecute(BusinessMsg businessMsg) {
        final BusinessMsg t = businessMsgMapper.lambdaQuery().eq(SuperEntity::getId, businessMsg.getId()).one();
        if (Objects.nonNull(t)) {
            businessMsg.setOptimistic(null);
            BeanUtil.copyProperties(businessMsg, t, CopyOptions.create().ignoreNullValue());
            t.setNextExecuteTime(new Date());
            t.setStatus(MessageStatusConstant.INIT);
            super.updateById(t);
            final MessageBody messageBody = new MessageBody();
            BeanUtil.copyProperties(t, messageBody, CopyOptions.create().ignoreNullValue());
            messageBody.setPersistent(true);
            messageBody.setOptimistic(messageBody.getOptimistic() + 1);
            redisMessageProducer.syncSend(null, messageBody);
        } else {
            throw new ServiceException("该数据不存在:" + businessMsg.getId());
        }
    }

    public void retry(BusinessMsg businessMsg) {
        final BusinessMsg t = businessMsgMapper.lambdaQuery().eq(SuperEntity::getId, businessMsg.getId()).one();
        if (Objects.nonNull(t)) {
            final MessageBody messageBody = new MessageBody();
            BeanUtil.copyProperties(t, messageBody, CopyOptions.create().ignoreNullValue());
            messageBody.setPersistent(true);
            messageBody.setStatus(MessageStatusConstant.INIT);
            messageBody.setNextExecuteTime(new Date());
            redisMessageProducer.syncSend(null, messageBody);
        } else {
            throw new ServiceException("该数据不存在:" + businessMsg.getId());
        }
    }

    public ResponseBean<String> batchRetry(BusinessMsg businessMsg) {
        try {
            String lockKey = QueueConstant.QUEUE_LOAD_LOCK;
            final ResponseBean responseBean = RedisUtil.withRenderLock(lockKey, 600, () -> {
                updateInitTask(businessMsg);
                long totalSize = findByCount(businessMsg);
                long totalPage = (totalSize + 500 - 1) / 500;
                long retrySize = 0;
                for (int i = 1; i <= totalPage; i++) {
                    List<BusinessMsg> page = findByPage(businessMsg, i - 1);
                    retrySize += page.size();
                    page.forEach(e -> {
                        final MessageBody messageBody = new MessageBody();
                        BeanUtil.copyProperties(e, messageBody, CopyOptions.create().ignoreNullValue());
                        messageBody.setPersistent(true);
                        redisMessageProducer.asyncSend(messageBody);
                    });
                }
                final ResponseBean res = ResponseBean.successBean();
                res.setData(retrySize);
                res.setMsg("成功重试" + retrySize + "条记录");
                return res;
            });

            if (responseBean.isSuccess()) {
                return responseBean;
            } else if (ResponseCode.LOCK_FAIL.equals(responseBean.getCode())) {
                return ResponseBean.bizErrBean("请求过于频繁,请稍后再试");
            } else {
                return ResponseBean.bizErrBean(responseBean.getMsg());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseBean.bizErrBean(e.getMessage());
        }
    }

    /**
     * 关闭待执行状态的任务
     *
     * @param businessNo
     * @param businessType
     */
    public void closeTask(String businessNo, String businessType) {
        lambdaUpdate()
                .setSql("optimistic = optimistic + 1")
                .set(BusinessMsg::getStatus, MessageStatusConstant.SUCCESS)
                .set(BusinessMsg::getRemark, "任务关闭")
                .eq(BusinessMsg::getBusinessNo, businessNo)
                .eq(BusinessMsg::getBusinessType, businessType)
                .update();
    }

    private void updateInitTask(BusinessMsg businessMsg) {
        businessMsgMapper.lambdaUpdate()
                .set(BusinessMsg::getNextExecuteTime, new DateTime().plusSeconds(-10).toDate())
                .eq(BusinessMsg::getBusinessType, businessMsg.getBusinessType())
                .eq(BusinessMsg::getNamespace, businessMsg.getNamespace())
                .eq(BusinessMsg::getStatus, MessageStatusConstant.INIT)
                .update();
    }

    private long findByCount(BusinessMsg businessMsg) {
        return businessMsgMapper.selectCount(new LambdaQueryWrapper<BusinessMsg>()
                .eq(BusinessMsg::getBusinessType, businessMsg.getBusinessType())
                .eq(BusinessMsg::getStatus, MessageStatusConstant.INIT)
                .le(BusinessMsg::getNextExecuteTime, new Date()));

    }

    private List<BusinessMsg> findByPage(BusinessMsg businessMsg, int page) {
        BusinessMsgQueryParam query = new BusinessMsgQueryParam();
        query.setBusinessType(businessMsg.getBusinessType());
        query.setStatus(MessageStatusConstant.INIT);
        query.setNextExecuteTime(new Date());

        Pageable pageable = PageRequest.of(page, 500, Sort.Direction.DESC, "id");
        IPage<BusinessMsg> queryPage = PageUtil.toMybatisPage(pageable);
        IPage<BusinessMsg> pages = businessMsgMapper.selectPage(queryPage, QueryHelpMybatisPlus.getPredicate(query));
        return pages.getRecords();
    }

    /**
     * 非立即重试
     *
     * @param businessMsg
     */
    public void waitRetry(BusinessMsg businessMsg, Date nextExecuteTime) {
        final BusinessMsg t = businessMsgMapper.lambdaQuery().eq(SuperEntity::getId, businessMsg.getId()).one();
        if (Objects.nonNull(t)) {
            final MessageBody messageBody = new MessageBody();
            BeanUtil.copyProperties(t, messageBody, CopyOptions.create().ignoreNullValue());
            messageBody.setPersistent(true);
            messageBody.setStatus(MessageStatusConstant.INIT);
            messageBody.setNextExecuteTime(nextExecuteTime);
            redisMessageProducer.syncSend(null, messageBody);
        } else {
            throw new ServiceException("该数据不存在:" + businessMsg.getId());
        }
    }

}
