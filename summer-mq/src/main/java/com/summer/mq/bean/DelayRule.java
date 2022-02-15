package com.summer.mq.bean;

import com.summer.mq.constant.QueueConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息重试间隔规则
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Getter
@Setter
public class DelayRule {

    public static final DelayRule DEFAULT_RULE = new DelayRule(QueueConstant.DELAY_EXPRESSION);

    private String delayExpression;

    private boolean repeatRetry;

    private List<Integer> delaySecondsList = new ArrayList<>();

    private List<String> delayCronExpressionList = new ArrayList<>();

    public DelayRule(String delayExpression) {
        this.delayExpression = delayExpression;
        initDelayList();
    }

    private void initDelayList() {
        String[] split = delayExpression.split("\\s");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (s.endsWith("s")) {
                final int v = Integer.parseInt(s.replace("s", ""));
                delaySecondsList.add(v);
                delayCronExpressionList.add("0/" + v + " * * * * ?");
            } else if (s.endsWith("m")) {
                final int v = Integer.parseInt(s.replace("m", ""));
                delaySecondsList.add(60 * v);
                delayCronExpressionList.add("0 0/" + v + " * * * ?");
            } else if (s.endsWith("h")) {
                final int v = Integer.parseInt(s.replace("h", ""));
                delaySecondsList.add(60 * 60 * v);
                delayCronExpressionList.add("0 0 0/" + v + " * * ?");
            }
        }
    }

    public Integer getSeconds(int i) {
        return delaySecondsList.get(i);
    }

    public String getCronExpression(int i) {
        return delayCronExpressionList.get(i);
    }

    public int size() {
        return delaySecondsList.size();
    }

    public static String fmtLevelName(int i) {
        return org.apache.commons.lang3.StringUtils.leftPad(i + "", 2, '0');
    }

}
