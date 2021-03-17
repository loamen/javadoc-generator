package com.loamen.javadoc.generator.entity;

import lombok.Data;

import java.util.List;


/**
 * java类中方法的相关信息
 *
 * @author Don
 */
@Data
public class MethodComment {
    /**
     * 修饰符
     */
    private String modifiers;
    /**
     * 方法注释
     */
    private String methodComment;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数
     */
    private List<FieldComment> params;
    /**
     * 返回值
     */
    private FieldComment returnEntity;
    /**
     * 抛出异常
     */
    private List<ExceptionComment> exceptions;
}
