package com.summer.log.core;

import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules;
import com.summer.log.encoder.PatternWrapLayoutEncoder;
import com.summer.log.filter.LogCategoryFilter;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.core.env.Environment;

/**
 * @author Tom
 * @version 1.0.0
 * @date 4/11/22
 */
class SpringBootCategoryConfigurator extends JoranConfigurator {

    private LoggingInitializationContext initializationContext;

    SpringBootCategoryConfigurator(LoggingInitializationContext initializationContext) {
        this.initializationContext = initializationContext;
    }

    @Override
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        Environment environment = this.initializationContext.getEnvironment();
        rs.addRule(new ElementSelector("configuration/springProperty"), new SpringPropertyAction(environment));
        rs.addRule(new ElementSelector("*/springProfile"), new SpringProfileAction(environment));
        rs.addRule(new ElementSelector("*/springProfile/*"), new NOPAction());
    }

    @Override
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        registry.add(AppenderBase.class, "layout", PatternWrapLayout.class);
        registry.add(UnsynchronizedAppenderBase.class, "layout", PatternWrapLayout.class);

        registry.add(AppenderBase.class, "encoder", PatternWrapLayoutEncoder.class);
        registry.add(UnsynchronizedAppenderBase.class, "encoder", PatternWrapLayoutEncoder.class);

        registry.add(AppenderBase.class, "filter", LogCategoryFilter.class);
        registry.add(UnsynchronizedAppenderBase.class, "filter", LogCategoryFilter.class);

        registry.add(EvaluatorFilter.class, "evaluator", JaninoEventEvaluator.class);

        SSLNestedComponentRegistryRules.addDefaultNestedComponentRegistryRules(registry);
    }

}
