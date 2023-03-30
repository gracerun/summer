# 简介
summer将通用功能组件进行封装，提供轻量级消息组件、日志增强、分布式锁、http等常用工具。使开发人员最大限度避免“复制粘贴“代码的问题。

# summer-mq
`summer-mq`是一个轻量级消息组件，通过集成`redis`组件实现普通消息、事务消息、定时/延迟消息。不需要额外部署消息中间件即可享受异步通信的快感。

##使用说明

依赖JDK8+，依赖`springboot`版本

```xml
<spring-boot.version>2.6.8</spring-boot.version>
<spring-cloud.version>2021.0.3</spring-cloud.version>
```
Maven

在项目的pom.xml的dependencies中加入以下内容:

```xml
<dependency>
    <groupId>com.gracerun</groupId>
    <artifactId>summer-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

##消费者配置

实现`MessageListener`接口，配置`@SummerMQMessageListener`注解，设置`topic`标识，设置消费者线程池大小。

`consumerNamespace`：消费者名称空间，默认值为：`${spring.profiles.active:summer.mq.queue}`，
*消费者与生产者名称空间相同的情况下才能消费对应的消息*

`topic`：用于消息分类

`delayExpression`：消费失败时重试次数和重试时间间隔，默认重试19次，
默认时间间隔`1s 3s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h`

`repeatRetry`：当消费失败并且达到最大重试次数之后是否需要重复尝试消费，默认`false`

`batchRpopSize`：单次拉取消息的最大数量，默认10

`corePoolSize`：消费者核心线程数大小，默认4

`maximumPoolSize`：消费者线程数最大值，默认4

`keepAliveTime`：消费者线程空闲超时时间，单位秒，默认60

`blockingQueueSize`：缓存队列空间大小，默认2000

返回结果：

`ConsumeStatus.CONSUME_SUCCESS`表示消费成功，消息从消费队列移除

`ConsumeStatus.RECONSUME_LATER`表示消费失败，间隔一段时间之后再次进入消费队列

```java
@Slf4j
@Component
@SummerMQMessageListener(topic = "msg_notify")
public class MsgNotifyConsumer implements MessageListener {

    @Override
    public ConsumeStatus consumeMessage(MessageBody msg) {
        log.info("msgId:{}", msg.getId());
        try {
            TimeUnit.MILLISECONDS.sleep(RandomUtils.nextInt(10, 20));
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return ConsumeStatus.CONSUME_SUCCESS;
    }
}
```
##生产者配置

在系统启动类上配置`@EnableSummerMq`，设置生产者线程池大小。
```java
@SpringBootApplication
@EnableScheduling
@EnableSummerMq
@Slf4j
public class Application implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("系统启动成功\n.....\nsuccess");
    }
}
```
##发送普通消息

`summerMQTemplate.sendAndSave`表示发送消息并将消息持久化到数据库
`summerMQTemplate.send`表示发送消息不会将消息持久化到数据库
如果在事务内执行则事务提交成功之后才发送该消息，如果在事务外执行则立即发送。

```java
@Autowired
private SummerMQTemplate summerMQTemplate;

@Transactional
public void sendAndSave(MessageBody messageBody) {
    summerMQTemplate.sendAndSave(messageBody);
}
```
##发送延时消息

设置消息体的nextExecuteTime属性可以实现消息延迟送达的效果。
```java
@Autowired
private SummerMQTemplate summerMQTemplate;

@Transactional
public void delaySend(MessageBody messageBody) {
    messageBody.setNextExecuteTime(new DateTime().plusMinutes(1).toDate());
    summerMQTemplate.sendAndSave(messageBody);
}
```

#summer-log

对Logback日志组件进行增强，支持web请求日志标准化输出、换行日志与异常日志跟踪，请求日志、定时日志、自定义线程日志分类。

内置`spring.localhost.ip-address`变量表示本机ip末尾两段地址

内置`%X{CURRENT_METHOD_NAME}`变量表示当前线程调用的方法名称

`<filter>`由`LogCategoryFilter`实现，value支持`DEFAULT`、`SCHEDULED`、`ASYNC`分别对应请求日志、定时日志、自定义线程日志

日志配置示例：

```xml
<configuration>
    <springProperty name="SPRING_APPLICATION_NAME" scope="context" source="spring.application.name"
                    defaultValue="localhost"/>
    <springProperty name="MAX_HISTORY" source="spring.logback.max.history" defaultValue="3"/>
    <springProperty name="LOG_HOME_DIR" source="spring.logback.log.dir" defaultValue="logs"/>
    <springProperty name="LOCALHOST_IP" source="spring.localhost.ip-address" defaultValue="127.0.0.1"/>

    <property name="LOG_HOME" value="${LOG_HOME_DIR}"/>
    <property name="LOG_HISTORY_HOME" value="${LOG_HOME}/history"/>
    <property name="BIZ_LOG" value="%d{HH:mm:ss.SSS} [${LOCALHOST_IP},%X{spanId},%X{traceId}] %-5level %-32.32logger{32} %X{CURRENT_METHOD_NAME} %msg%n"/>

    <!--业务日志-->
    <appender name="infoAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
      <file>${LOG_HOME}/info.log</file>
      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_HISTORY_HOME}/info%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>${MAX_HISTORY}</maxHistory>
      </rollingPolicy>
      <encoder>
        <pattern>${BIZ_LOG}</pattern>
      </encoder>
      <filter>
        <value>DEFAULT</value>
      </filter>
    </appender>

    <!--自定义线程日志-->
    <appender name="taskAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
      <file>${LOG_HOME}/task.log</file>
      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_HISTORY_HOME}/task%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>${MAX_HISTORY}</maxHistory>
      </rollingPolicy>
      <encoder>
        <pattern>${BIZ_LOG}</pattern>
      </encoder>
      <filter>
        <value>ASYNC</value>
      </filter>
    </appender>

    <!--@Scheduled定时日志-->
    <appender name="scheduledAppender" class="ch.qos.logback.core.rolling.RollingFileAppender">
      <file>${LOG_HOME}/scheduled.log</file>
      <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_HISTORY_HOME}/scheduled%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>${MAX_HISTORY}</maxHistory>
      </rollingPolicy>
      <encoder>
        <pattern>${BIZ_LOG}</pattern>
      </encoder>
      <filter>
        <value>SCHEDULED</value>
      </filter>
    </appender>

    <root level="INFO">
      <appender-ref ref="infoAppender"/>
      <appender-ref ref="taskAppender"/>
      <appender-ref ref="scheduledAppender"/>
    </root>
</configuration>
```

## @Logging

标注在接口、类、方法上打印方法入参与返回参数日志，支持从父类或父接口继承`@Logging`

```java
@Logging(serializeArgsUsing = ToJsonSerializer.class, serializeReturnUsing = ToJsonSerializer.class)
```

`serializeArgsUsing`：参数序列化类型，实现`LogSerializer`自定义序列化方式

`serializeReturnUsing`：返回值序列化类型，实现`LogSerializer`自定义序列化方式

`level`：日志级别

`maxLength`：单行日志最大长度

`throwableLog`：异常日志设置

## @ThrowableLog

支持异常日志个性化配置

```java
@ThrowableLog(throwable = Throwable.class, maxRow = 5)
```

`throwable`：指定异常类型，必须是`Throwable`的子类，表示哪些异常限定最大行数

`maxRow`：异常日志最大行数，默认`-1`表示不限制异常日志行数

##HttpTraceFilter

输出web请求日志，包含clientIP、请求方法、请求URI、协议、响应状态码、请求处理耗时

例：

```java
13:22:36.821 [1.4,e600d3bce49f9758,e600d3bce49f9758] INFO  c.s.log.filter.HttpTraceFilter    127.0.0.1 POST /message/log HTTP/1.1 200 5180
```

#summer-util

提供http请求构建工具类`HttpBuilder`

发送`get`请求

```java
RequestConfig config = RequestConfig.custom()
  .setSocketTimeout(10000)
  .setConnectTimeout(10000)
  .setConnectionRequestTimeout(2000)
  .build();
  
HttpBuilder.get("url")
  .setConfig(config)
  .setLevel(Level.INFO)
	.execute();
```

发送post请求

```java
RequestConfig config = RequestConfig.custom()
  .setSocketTimeout(10000)
  .setConnectTimeout(10000)
  .setConnectionRequestTimeout(2000)
  .build();
  
HttpBuilder.post("url")
  .setJsonParam("{}")
  .setConfig(config)
  .setLevel(Level.INFO)
	.execute();
```

`SpringProfilesUtil`获取环境变量与环境判断

```java
SpringProfilesUtil.getActive();//返回"${spring.profiles.active:}"参数值
```

