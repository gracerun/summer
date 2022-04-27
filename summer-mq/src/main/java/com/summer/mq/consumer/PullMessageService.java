
package com.summer.mq.consumer;

import com.summer.mq.factory.MQClientInstance;
import com.summer.mq.util.ThreadUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.*;

@Slf4j
public class PullMessageService implements Runnable, DisposableBean {

    @Getter
    @Setter
    protected volatile boolean stopped = false;

    private final LinkedBlockingQueue<PullRequest> pullRequestQueue = new LinkedBlockingQueue<>();
    private final MQClientInstance mQClientFactory;

    private final ExecutorService pullExecutor = Executors.newSingleThreadExecutor((r) -> new Thread(r, "pullExecutorThread"));
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor((r) -> new Thread(r, "PullMessageServiceScheduledThread"));

    public PullMessageService(MQClientInstance mQClientFactory) {
        this.mQClientFactory = mQClientFactory;
    }

    public void executePullRequestLater(final PullRequest pullRequest, final long timeDelay) {
        if (!isStopped()) {
            this.scheduledExecutorService.schedule(() -> PullMessageService.this.executePullRequestImmediately(pullRequest),
                    timeDelay, TimeUnit.MILLISECONDS);
        } else {
            log.warn("PullMessageServiceScheduledThread has shutdown");
        }
    }

    public void executePullRequestImmediately(final PullRequest pullRequest) {
        try {
            this.pullRequestQueue.put(pullRequest);
        } catch (InterruptedException e) {
            log.error("executePullRequestImmediately pullRequestQueue.put InterruptedException", e);
        }
    }

    public void executeTaskLater(final Runnable r, final long timeDelay) {
        if (!isStopped()) {
            this.scheduledExecutorService.schedule(r, timeDelay, TimeUnit.MILLISECONDS);
        } else {
            log.warn("PullMessageServiceScheduledThread has shutdown");
        }
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    private void pullMessage(final PullRequest pullRequest) {
        final RedisMessagePullConsumer consumer = this.mQClientFactory.selectConsumer(pullRequest.getMessageQueueName());
        if (consumer != null) {
            consumer.pullMessage(pullRequest);
        } else {
            log.warn("No matched consumer for the PullRequest {}, drop it", pullRequest);
        }
    }

    @Override
    public void run() {
        log.info("{} service started", this.getServiceName());

        while (!this.isStopped()) {
            try {
                PullRequest pullRequest = this.pullRequestQueue.take();
                this.pullMessage(pullRequest);
            } catch (InterruptedException ignored) {
                log.error("Pull Message Service Run Method InterruptedException", ignored);
            } catch (Exception e) {
                log.error("Pull Message Service Run Method exception", e);
            }
        }

        log.info("{} service end", this.getServiceName());
    }

    @Override
    public void destroy() throws Exception {
        ThreadUtils.shutdownGracefully(this.pullExecutor, 1000, TimeUnit.MILLISECONDS);
        ThreadUtils.shutdownGracefully(this.scheduledExecutorService, 1000, TimeUnit.MILLISECONDS);
    }

    public String getServiceName() {
        return PullMessageService.class.getSimpleName();
    }

    public void start() {
        pullExecutor.execute(this);
    }
}
