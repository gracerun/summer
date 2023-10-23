package com.gracerun.summermq.constant;

public enum ConsumeStatus {
    /**
     * Success consumption
     */
    CONSUME_SUCCESS,
    /**
     * Failure consumption,later try to consume
     */
    RECONSUME_LATER,

    /**
     * Stop consumption,requires manual intervention
     */
    CONSUME_STOP
}
