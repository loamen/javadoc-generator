package com.loamen.javadoc.generator.entity;

import lombok.Data;

import java.util.List;



/**
 * java类的相关信息
 * @author Don
 */
@Data
public class ClassComment {
	/** 类的全类名 */
	private String className;
	/** 类的简单类名 */
	private String simpleClassName;
	/** 类注释 */
	private String classComment;
	/** 字段相关信息 */
	private List<FieldComment> fields;
	/** 方法相关信息 */
	private List<MethodComment> methods;
}
