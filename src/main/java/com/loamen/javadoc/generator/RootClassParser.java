package com.loamen.javadoc.generator;

import java.util.LinkedList;
import java.util.List;

import com.loamen.javadoc.generator.entity.*;
import com.loamen.javadoc.generator.utils.StringUtil;
import com.sun.javadoc.*;
import com.sun.tools.javadoc.ModifierFilter;

/**
 * RootClass对象的解析器，用于根据RootClass构建我们自己的ClassComment
 *
 * @author ME
 */
public class RootClassParser {

    /**
     * 需要处理的类字段可见性，默认公有
     */
    private Modifier fieldModifier = Modifier.PUBLIC;
    /**
     * 需要处理的类方法可见性，默认公有
     */
    private Modifier methodModifier = Modifier.PUBLIC;

    /**
     * Instantiates a new Root class parser.
     *
     * @param fieldModifier  the field modifier
     * @param methodModifier the method modifier
     */
    public RootClassParser(Modifier fieldModifier, Modifier methodModifier) {
        this.fieldModifier = fieldModifier;
        this.methodModifier = methodModifier;
    }

    /**
     * 解析
     *
     * @param root the root
     * @return the list
     */
    public List<ClassComment> parse(RootDoc root) {
        if (root == null) {
            return new LinkedList<ClassComment>();
        }
        List<ClassComment> classComments = new LinkedList<ClassComment>();
        ClassDoc[] classes = root.classes();
        for (ClassDoc clasz : classes) {
            ClassComment classComment = new ClassComment();
            classComment.setClassName(clasz.qualifiedTypeName());
            classComment.setSimpleClassName(clasz.simpleTypeName());
            classComment.setClassComment(clasz.commentText());
            classComment.setFields(this.parseFields(clasz.fields()));
            classComment.setMethods(this.parseMethods(clasz.methods()));
            classComments.add(classComment);
        }
        return classComments;
    }

    /**
     * 解析字段
     */
    private List<FieldComment> parseFields(FieldDoc[] fields) {
        if (fields == null || fields.length <= 0) {
            return new LinkedList<FieldComment>();
        }
        List<FieldComment> fieldList = new LinkedList<FieldComment>();
        for (FieldDoc field : fields) {
            if (!this.checkModifier(field)) {
                continue;
            }
            FieldComment fieldComment = new FieldComment();
            fieldList.add(fieldComment);
            fieldComment.setClasz(field.type().qualifiedTypeName());
            fieldComment.setSimpleClassName(field.type().simpleTypeName());
            fieldComment.setFieldComment(field.commentText());
            fieldComment.setFieldName(field.name());
            fieldComment.setDefaultValue(field.constantValue());
            fieldComment.setModifiers(field.modifiers());
        }
        return fieldList;
    }

    /**
     * 检查字段修饰语，也就是public、protected、private、package
     *
     * @return 如果该字段的访问权限修饰语满足我们需要的级别，那就返回true
     */
    private boolean checkModifier(FieldDoc field) {
        ModifierFilter modifierFilter = new ModifierFilter(this.getFieldModifier().ordinal());
        boolean result = modifierFilter.checkModifier(StringUtil.getModifier(field.modifiers()).ordinal());
        return result;
       /* if (modifier.toString().equalsIgnoreCase(field.modifiers())) {
            return true;
        }
        return false;*/
    }

    /**
     * 检查方法修饰语，也就是public、protected、private
     *
     * @return 如果该方法的访问权限修饰语满足我们需要的级别，那就返回true
     */
    private boolean checkModifier(MethodDoc method) {
        ModifierFilter modifierFilter = new ModifierFilter(this.getMethodModifier().ordinal());
        boolean result = modifierFilter.checkModifier(StringUtil.getModifier(method.modifiers()).ordinal());
        return result;
        /*if (this.getMethodModifier().toString().equalsIgnoreCase(method.modifiers())) {
            return true;
        }
        return false;*/
    }


    /**
     * 解析方法
     */
    private List<MethodComment> parseMethods(MethodDoc[] methods) {
        if (methods == null || methods.length <= 0) {
            return new LinkedList<MethodComment>();
        }
        List<MethodComment> methodsList = new LinkedList<MethodComment>();
        for (MethodDoc method : methods) {
            if (!this.checkModifier(method)) {
                continue;
            }
            MethodComment methodComment = new MethodComment();
            methodsList.add(methodComment);
            methodComment.setMethodComment(method.commentText());
            methodComment.setMethodName(method.name());
            methodComment.setReturnEntity(this.parseMethodReturn(method));
            methodComment.setParams(this.parseMethodParam(method));
            methodComment.setExceptions(this.parseMethodException(method));
            methodComment.setModifiers(method.modifiers());
        }
        return methodsList;
    }

    /***
     * 	解析方法的返回值
     * */
    private FieldComment parseMethodReturn(MethodDoc method) {
        // 返回值
        FieldComment returnEntity = new FieldComment();
        returnEntity.setClasz(method.returnType().qualifiedTypeName());
        returnEntity.setSimpleClassName(method.returnType().simpleTypeName());
        for (Tag tag : method.tags()) {
            if (tag.name().equals("@return")) {
                returnEntity.setFieldComment(tag.text());
                break;
            }
        }
        return returnEntity;
    }


    /***
     * 	解析方法的参数
     * */
    private List<FieldComment> parseMethodParam(MethodDoc method) {
        // 参数
        List<FieldComment> params = new LinkedList<FieldComment>();
        for (Parameter parameter : method.parameters()) {
            FieldComment param = new FieldComment();
            param.setClasz(parameter.type().qualifiedTypeName());
            param.setSimpleClassName(parameter.type().simpleTypeName());
            param.setFieldName(parameter.name());
            for (ParamTag paramTag : method.paramTags()) {
                if (paramTag.parameterName().equals(param.getFieldName())) {
                    param.setFieldComment(paramTag.parameterComment());
                    ;
                    break;
                }
            }
            params.add(param);
        }
        return params;
    }

    /***
     * 	解析异常
     * */
    private List<ExceptionComment> parseMethodException(MethodDoc method) {
        // 参数
        List<ExceptionComment> exceptionCommentList = new LinkedList<ExceptionComment>();
        for (ClassDoc classDoc : method.thrownExceptions()) {
            ExceptionComment exceptionComment = new ExceptionComment();
            exceptionComment.setQualifiedTypeName(classDoc.qualifiedTypeName());
            exceptionComment.setSimpleClassName(classDoc.simpleTypeName());
            exceptionComment.setExceptionName(classDoc.name());
            for (ThrowsTag throwsTag : method.throwsTags()) {
                if (throwsTag.exceptionName().equals(exceptionComment.getExceptionName())) {
                    exceptionComment.setExceptionComment(throwsTag.exceptionComment());
                    break;
                }
            }
            exceptionCommentList.add(exceptionComment);
        }
        return exceptionCommentList;
    }

    /**
     * Gets field modifier.
     *
     * @return the field modifier
     */
    public Modifier getFieldModifier() {
        return fieldModifier;
    }

    /**
     * Gets method modifier.
     *
     * @return the method modifier
     */
    public Modifier getMethodModifier() {
        return methodModifier;
    }

    /**
     * Sets field modifier.
     *
     * @param fieldModifier the field modifier
     */
    public void setFieldModifier(Modifier fieldModifier) {
        this.fieldModifier = fieldModifier;
    }

    /**
     * Sets method modifier.
     *
     * @param methodModifier the method modifier
     */
    public void setMethodModifier(Modifier methodModifier) {
        this.methodModifier = methodModifier;
    }


}
