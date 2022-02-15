
package com.summer.mq.factory;

import com.summer.mq.constant.ServiceState;
import com.summer.mq.consumer.PullMessageService;
import com.summer.mq.consumer.RedisMessagePullConsumer;
import com.summer.mq.producer.RedisMessageProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class MQClientInstance {
    private static MQClientInstance instance = new MQClientInstance();
    private final ConcurrentMap<String, RedisMessageProducer> producerTable = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RedisMessagePullConsumer> consumerTable = new ConcurrentHashMap<>();

    @Getter
    private final PullMessageService pullMessageService;

    private ServiceState serviceState = ServiceState.CREATE_JUST;

    private MQClientInstance() {
        this.pullMessageService = new PullMessageService(this);
    }

    public static MQClientInstance getInstance() {
        return instance;
    }

    public void start() {
        synchronized (this) {
            switch (this.serviceState) {
                case CREATE_JUST:
                    this.serviceState = ServiceState.START_FAILED;
                    this.pullMessageService.start();
                    this.serviceState = ServiceState.RUNNING;
                    break;
                case RUNNING:
                    break;
                case SHUTDOWN_ALREADY:
                    break;
                default:
                    break;
            }
        }
    }

    public boolean registerConsumer(final String namespace, final RedisMessagePullConsumer consumer) {
        if (null == namespace || null == consumer) {
            return false;
        }

        RedisMessagePullConsumer prev = this.consumerTable.putIfAbsent(namespace, consumer);
        if (prev != null) {
            log.warn("the consumer namespace[{}] exist already.", namespace);
            return false;
        }

        return true;
    }

    public boolean registerProducer(final String namespace, final RedisMessageProducer producer) {
        if (null == namespace || null == producer) {
            return false;
        }

        RedisMessageProducer prev = this.producerTable.putIfAbsent(namespace, producer);
        if (prev != null) {
            log.warn("the producer namespace[{}] exist already.", namespace);
            return false;
        }

        return true;
    }

    public RedisMessageProducer selectProducer(final String namespace) {
        return this.producerTable.get(namespace);
    }

    public RedisMessagePullConsumer selectConsumer(final String namespace) {
        return this.consumerTable.get(namespace);
    }
}
