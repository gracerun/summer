

package com.gracerun.summermq.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SummerMQImportSelector implements ImportSelector {

    private static final String SUMMERMQ_CONFIGURATION_CLASS_NAME =
            "com.gracerun.summermq.spring.autoconfigure.SummerMQConfiguration";

    private static final String REDIS_CONFIGURATION_CLASS_NAME =
            "com.gracerun.summermq.spring.autoconfigure.RedisConfiguration";

    private static final String MESSAGE_PERSISTENTT_CONFIGURATION_CLASS_NAME =
            "com.gracerun.summermq.spring.autoconfigure.MessagePersistentConfiguration";

    private static final String LISTENER_CONTAINER_CONFIGURATION_CLASS_NAME =
            "com.gracerun.summermq.spring.autoconfigure.ListenerContainerConfiguration";

    private static final String DELAY_CONSUMER_CONFIGURATION_CLASS_NAME =
            "com.gracerun.summermq.spring.autoconfigure.DelayConsumerConfiguration";

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Set<String> imports = new LinkedHashSet<>();
        imports.add(SUMMERMQ_CONFIGURATION_CLASS_NAME);
        imports.add(REDIS_CONFIGURATION_CLASS_NAME);
        imports.add(MESSAGE_PERSISTENTT_CONFIGURATION_CLASS_NAME);
        imports.add(LISTENER_CONTAINER_CONFIGURATION_CLASS_NAME);
        imports.add(DELAY_CONSUMER_CONFIGURATION_CLASS_NAME);
        return StringUtils.toStringArray(imports);
    }

}
