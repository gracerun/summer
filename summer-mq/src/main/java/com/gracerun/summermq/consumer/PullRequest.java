package com.gracerun.summermq.consumer;

import com.gracerun.summermq.util.NotNullStringStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Accessors(chain = true)
@Getter
@Setter
public class PullRequest {

    private String consumerNamespace;

    private String messageQueueName;

    private boolean batchPull = false;

    private int batchSize = 10;

    public PullRequest(String consumerNamespace, String messageQueueName) {
        this.consumerNamespace = consumerNamespace;
        this.messageQueueName = messageQueueName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, NotNullStringStyle.getSytle());
    }
}
