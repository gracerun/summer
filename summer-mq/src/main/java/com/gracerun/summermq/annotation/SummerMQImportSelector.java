

package com.gracerun.summermq.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class SummerMQImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Set<String> imports = new LinkedHashSet<>();
        imports.add(SummerMQConfiguration.class.getName());
        imports.add(RedisConfiguration.class.getName());
        imports.add(MessagePersistentConfiguration.class.getName());
        imports.add(ListenerContainerConfiguration.class.getName());
        imports.add(DelayConsumerConfiguration.class.getName());
        return StringUtils.toStringArray(imports);
    }

}
