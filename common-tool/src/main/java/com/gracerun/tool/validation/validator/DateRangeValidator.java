package com.gracerun.tool.validation.validator;

import com.gracerun.tool.validation.constraints.DateRange;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 时间范围校验
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/11/27
 */
@Slf4j
public class DateRangeValidator implements ConstraintValidator<DateRange, List<Date>> {

    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    private long min;
    private long max;
    private TimeUnit unit;

    @Override
    public void initialize(DateRange parameters) {
        min = parameters.min();
        max = parameters.max();
        unit = parameters.unit();
        validateParameters();
    }

    @Override
    public boolean isValid(List<Date> value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }
        if (value.size() < 2) {
            return false;
        }
        long between = unit.convert(value.get(1).getTime() - value.get(0).getTime(), TimeUnit.MILLISECONDS);
        return between >= min && between <= max;
    }

    private void validateParameters() {
        if (min < 0) {
            throw LOG.getMinCannotBeNegativeException();
        }
        if (max < 0) {
            throw LOG.getMaxCannotBeNegativeException();
        }
        if (max < min) {
            throw LOG.getLengthCannotBeNegativeException();
        }
    }

}
