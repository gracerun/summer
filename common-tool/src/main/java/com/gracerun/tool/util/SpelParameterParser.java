package com.gracerun.tool.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 注解参数解析
 *
 * @author Tom
 * @version 1.0.0
 * @date 2023/6/15
 */
public class SpelParameterParser {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 获取method的参数名称列表
     *
     * @param method method
     * @return java.lang.String[]
     */
    public static String[] getParameterNames(Method method) {
        return NAME_DISCOVERER.getParameterNames(method);
    }

    /**
     * 解析表达式
     *
     * @param expressionStr 待解析的表达式字符串
     * @param context       context
     * @return java.lang.String
     */
    public static String parse(String expressionStr, EvaluationContext context) {
        // 解析过后的Spring表达式对象
        Expression expression = PARSER.parseExpression(expressionStr);
        // 表达式从上下文中计算出实际参数值
        return expression.getValue(context, String.class);
    }

    /**
     * <p>
     * 根据参数名和参数值构建EvaluationContext
     * </p>
     *
     * @param paramNames  参数名称集合
     * @param paramValues 参数值集合
     * @return org.springframework.expression.EvaluationContext
     */
    public static EvaluationContext buildContext(String[] paramNames, Object[] paramValues) {
        EvaluationContext context = new StandardEvaluationContext();
        if (paramNames == null || paramValues == null) {
            return context;
        }
        for (int i = 0; i < paramValues.length; i++) {
            context.setVariable(paramNames[i], paramValues[i]);
        }
        return context;
    }

}
