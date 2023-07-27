package com.gracerun.log.interceptor;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class RuleBasedLoggingAttribute extends LoggingAttribute {

    /**
     * 异常日志属性
     */
    @Nullable
    private List<ThrowableLogRuleAttribute> logRuleAttributes;

    @Override
    public boolean printCondition(Throwable e) {
        ThrowableLogRuleAttribute winner = getWinner(e);
        if (Objects.nonNull(winner)) {
            return true;
        }
        return super.printCondition(e);
    }

    @Nullable
    private ThrowableLogRuleAttribute getWinner(Throwable e) {
        ThrowableLogRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;
        if (Objects.nonNull(this.logRuleAttributes)) {
            for (ThrowableLogRuleAttribute rule : this.logRuleAttributes) {
                int depth = rule.getDepth(e);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = rule;
                }
            }
        }
        return winner;
    }

    @Override
    public int getPrintMaxRow(Throwable e) {
        ThrowableLogRuleAttribute winner = getWinner(e);
        if (Objects.nonNull(winner)) {
            return winner.getMaxRow();
        }
        return -1;
    }

}