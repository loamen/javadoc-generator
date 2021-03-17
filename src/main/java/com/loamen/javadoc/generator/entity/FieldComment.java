package com.loamen.javadoc.generator.entity;

import lombok.Data;

/**
 * java类中字段的相关信息
 *
 * @author Don
 */
@Data
public class FieldComment {
    /**
     * 字段类型
     */
    private String clasz;
    /**
     * 类的简单类名
     */
    private String simpleClassName;
    /**
     * 字段注释
     */
    private String fieldComment;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 默认值，必须是final修饰的基本数据类型及其包装类
     */
    private Object defaultValue;
	/**
	 * 字段修饰符
	 */
	private String modifiers;
}
