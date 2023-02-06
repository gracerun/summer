package cn.happyselect.sys.constant;

/**
 * BeanUtil复制对象时设置忽略属性 IgnoreProperties
 */
public interface IgnoreProperties {

	String[] IGNORE = { "id", "optimistic", "createTime", "lastUpdateTime" };
}
