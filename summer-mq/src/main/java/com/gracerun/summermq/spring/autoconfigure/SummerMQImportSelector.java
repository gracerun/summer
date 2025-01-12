package com.gracerun.summermq.spring.autoconfigure;

import com.gracerun.summermq.spring.annotation.EnableSummerMQ;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class SummerMQImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> enableAttrMap = importingClassMetadata.getAnnotationAttributes(EnableSummerMQ.class.getName());
        AnnotationAttributes enableAttrs = AnnotationAttributes.fromMap(enableAttrMap);
        boolean enableDataBaseProducer = enableAttrs.getBoolean("enableDataBaseProducer");

        Set<String> imports = new LinkedHashSet<>();
        imports.add(SummerMQConfiguration.class.getName());
        imports.add(RedisScriptConfiguration.class.getName());
        imports.add(MessagePersistentConfiguration.class.getName());
        imports.add(ListenerContainerConfiguration.class.getName());
        imports.add(DelayConsumerConfiguration.class.getName());
        if (enableDataBaseProducer) {
            imports.add(DataBaseProducerConfiguration.class.getName());
        }
        return StringUtils.toStringArray(imports);
    }

}
