package com.efuture.omd.storage;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 虚拟字段描述
 * @author xzw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD, ANNOTATION_TYPE })
public @interface Virtual {
	public String name()     default ""; //虚拟字段名称(数据中枢性名称)
	public String type()     default ""; //服务名,取虚拟字典的服务
	public String method()   default ""; //表格或方法名称
	public String from()     default ""; //来源字段(确定字段值)
	public String to()       default ""; //字段名称
	public String key()      default ""; //虚拟字段来源表的关键字列表,没有填写则和from一致
	public String fetch()    default ""; //用指定的获取单条数据
	public String batch()    default ""; //取所有数据获取批量
}