
package com.summer.mq.consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Accessors(chain = true)
@Getter
@Setter
public class PullRequest {

    private String consumerGroup;

    private String messageQueueName;

    private boolean batchPull = false;

    private int batchSize = 10;

    public PullRequest(String consumerGroup, String messageQueueName) {
        this.consumerGroup = consumerGroup;
        this.messageQueueName = messageQueueName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("consumerGroup", consumerGroup)
                .append("messageQueueName", messageQueueName)
                .append("batchPull", batchPull)
                .append("batchSize", batchSize)
                .toString();
    }
}
