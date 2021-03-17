package com.loamen.javadoc.generator.entity;

import lombok.Data;

/**
 * @Author Don
 * @Description 异常描述实体
 * @Date 2021/1/21 15:37
 **/
@Data
public class ExceptionComment {
    /** 限定类型名称 */
    private String qualifiedTypeName;
    /** 类的简单类名 */
    private String simpleClassName;
    /** 异常注释 */
    private String exceptionComment;
    /** 异常名称 */
    private String exceptionName;
}
