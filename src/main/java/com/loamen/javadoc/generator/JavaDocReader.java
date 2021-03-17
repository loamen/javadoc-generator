package com.loamen.javadoc.generator;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.loamen.javadoc.generator.entity.ClassComment;
import com.loamen.javadoc.generator.entity.Modifier;
import com.loamen.javadoc.generator.utils.StringUtil;
import com.sun.javadoc.RootDoc;

/**
 * java文件doc读取器
 *
 * @author ME
 */
public class JavaDocReader {

    /**
     * 用于接收javadoc解析完成后生成的语法树根节点
     */
    private CustomDoclet doclet = new CustomDoclet();
    /**
     * 类路径，系统会在类路径下去找一些相关的类文件
     */
    private String classpath;
    /**
     * java源文件地址。可以是java源文件的绝对地址，也可以是包名。只能填写一个，
     */
    private String sourcepath;
    /**
     * 是否递归处理子包，只有在sourcepath为包名时才起作用，subpackages不为空是才是true。默认false
     */
    private boolean isSubpackages = false;
    /**
     * 源文件或者包名
     */
    private String source;
    /**
     * 要递归的子包
     */
    private String subpackages;
    /**
     * 需要处理的类字段可见性，默认公有
     */
    private Modifier fieldModifier = Modifier.PUBLIC;
    /**
     * 需要处理的类方法可见性，默认公有
     */
    private Modifier methodModifier = Modifier.PUBLIC;

    /**
     * 构造函数
     *
     * @param sourcePath  目标jar包，非空必填
     * @param subpackages 需要递归处理的子包，可以为空
     * @param classPath   相关的jar包的地址，绝对地址，javaPackage必须要能在这些路径中找到，非空必填
     * @throws IOException the io exception
     */
    public JavaDocReader(String sourcePath, String subpackages, String classPath) throws IOException {
        this.init(null,sourcePath, subpackages, classPath, Modifier.PUBLIC, Modifier.PUBLIC);
    }


    /**
     * 构造函数
     *
     * @param source         某个源文件或则包名
     * @param sourcePath     目标源码路径，非空必填
     * @param subpackages    需要递归处理的子包，可以为空
     * @param classPath      classPath路径，绝对地址，javaPackage必须要能在这些路径中找到，非空必填
     * @param fieldModifier  需要处理的类字段可见性，非空
     * @param methodModifier 需要处理的类方法可见性，非空
     * @throws IOException the io exception
     */
    public JavaDocReader(String source,String sourcePath, String subpackages, String classPath
            , Modifier fieldModifier, Modifier methodModifier) throws IOException {
        this.init(source,sourcePath, subpackages, classPath, fieldModifier, methodModifier);
    }


    /**
     * 构造函数
     *
     * @param sourcePath java文件地址，非空必填，绝对路径
     * @param classpath  源文件中引用的相关类的jar包地址，可选
     * @throws IOException the io exception
     */
    public JavaDocReader(String sourcePath, String classpath) throws IOException {
        this.init(null,sourcePath,null, classpath, Modifier.PUBLIC, Modifier.PUBLIC);
    }


    /**
     * 构造函数
     *
     * @param sourcePath     java文件地址，非空必填，绝对路径
     * @param classpath      源文件中引用的相关类的jar包地址，可选
     * @param fieldModifier  需要处理的类字段可见性，非空
     * @param methodModifier 需要处理的类方法可见性，非空
     * @throws IOException the io exception
     */
    public JavaDocReader(String sourcePath, String classpath
            , Modifier fieldModifier, Modifier methodModifier) throws IOException {
        this.init(null, sourcePath, null,classpath, fieldModifier, methodModifier);
    }


    /**
     * 构造函数
     * @param source         某个java源文件或者包名
     * @param sourcePath     .java源文件地址，非空。可以是java源文件的绝对地址，也可以是包名。<br>
     *                       如果是java源文件的绝对地址，只能填写一个地址，不能填写多个地址。<br>
     *                       如果是包名，该包必须能在classpath下找到，只能填写一个包名<br><br>
     * @param classpath      类路径，系统会在类路径下去找一些相关的类文件，可以为空
     * @param subpackages    是否递归处理子包，只有在sourcepath为包名时才起作用，可以为空
     * @param fieldModifier  需要处理的类字段可见性，非空
     * @param methodModifier 需要处理的类方法可见性，非空
     */
    private void init(String source,String sourcePath, String subpackages, String classpath
            , Modifier fieldModifier, Modifier methodModifier) {

        this.checkNotEmpty(sourcePath, "目标java文件不能为空");

        this.source = source;
        this.classpath = classpath;
        this.sourcepath = sourcePath;
        this.subpackages = subpackages;
        this.fieldModifier = fieldModifier == null ? this.fieldModifier : fieldModifier;
        this.methodModifier = methodModifier == null ? this.methodModifier : methodModifier;
        this.isSubpackages = this.checkNotEmpty(subpackages);
    }

    /**
     * 初始化参数
     *
     * @return String [] javadoc需要的参数数组
     */
    private String[] initArgs() {

        List<String> args = new LinkedList<String>();
        args.add("-quiet");

        args.add("-Xmaxerrs");
        args.add("200");

        args.add("-Xmaxwarns");
        args.add("200");

        args.add("-encoding");
        args.add("utf-8");

        args.add("-doclet");
        args.add(CustomDoclet.class.getName());

        /*args.add("-docletpath");
        args.add(CustomDoclet.class.getName());*/

        if (this.isSubpackages()) {
            args.add("-subpackages");
            args.add(this.getSubpackages());
        }

		/*StringBuilder sb = new StringBuilder();
		for(String classpath: this.getClasspath()) {
			sb.append(classpath).append(";");
		}
		if(sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}*/

        args.add("-classpath");
        args.add(classpath);

        if (this.fieldModifier == Modifier.PRIVATE || this.methodModifier == Modifier.PRIVATE) {
            args.add("-private");
        } else if (this.fieldModifier == Modifier.PROTECTED || this.methodModifier == Modifier.PROTECTED) {
            args.add("-protected");
        } else if (this.fieldModifier == Modifier.PUBLIC || this.methodModifier == Modifier.PUBLIC){
            args.add("-public");
        }else{
            args.add("-package");
        }

        args.add("-sourcepath");
        args.add(this.sourcepath);

        if(!StringUtil.isNullOrEmpty(source)){
            args.add(source);
        }

        return args.toArray(new String[args.size()]);
    }


    /**
     * 执行javadoc，解析源文件
     */
    private void executeJavadoc() {
        String[] initAgrs = this.initArgs();
        int returnCode = com.sun.tools.javadoc.Main.execute(JavaDocReader.class.getClassLoader(), initAgrs);
        if (0 != returnCode) {
            System.out.printf("javadoc ERROR CODE = %d\n", returnCode);
            throw new IllegalStateException();
        }
    }


    /**
     * 获取类注释信息
     *
     * @return List<ClassComment>          list
     */
    public List<ClassComment> execute() {
        this.executeJavadoc();
        RootDoc root = CustomDoclet.getRoot();
        if (root == null) {
            return new LinkedList<ClassComment>();
        }
        RootClassParser parser = new RootClassParser(this.getFieldModifier(), this.getMethodModifier());
        List<ClassComment> parseResult = parser.parse(root);
        return parseResult;
    }

    /**
     * Gets subpackages.
     *
     * @return the subpackages
     */
    public String getSubpackages() {
        return subpackages;
    }

    /**
     * Sets subpackages.
     *
     * @param subpackages the subpackages
     */
    public void setSubpackages(String subpackages) {
        this.subpackages = subpackages;
    }

    /**
     * Gets doclet.
     *
     * @return the doclet
     */
    public CustomDoclet getDoclet() {
        return doclet;
    }

    /**
     * Gets classpath.
     *
     * @return the classpath
     */
    public String getClasspath() {
        return classpath;
    }

    /**
     * Gets sourcepath.
     *
     * @return the sourcepath
     */
    public String getSourcepath() {
        return sourcepath;
    }

    /**
     * Is subpackages boolean.
     *
     * @return the boolean
     */
    public boolean isSubpackages() {
        return isSubpackages;
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
     * Sets doclet.
     *
     * @param doclet the doclet
     */
    public void setDoclet(CustomDoclet doclet) {
        this.doclet = doclet;
    }

    /**
     * Sets classpath.
     *
     * @param classpath the classpath
     */
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Sets sourcepath.
     *
     * @param sourcepath the sourcepath
     */
    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    /**
     * Sets subpackages.
     *
     * @param isSubpackages the is subpackages
     */
    public void setSubpackages(boolean isSubpackages) {
        this.isSubpackages = isSubpackages;
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


    /**
     * 检查非空
     *
     * @param arg          参数
     * @param exceptionMsg 异常信息
     */
    @SuppressWarnings("rawtypes")
    private void checkNotEmpty(Object arg, String exceptionMsg) {
        if (exceptionMsg == null) {
            exceptionMsg = "参数不能为空。";
        }
        if (arg == null) {
            throw new NullPointerException(exceptionMsg);
        }
        if (arg instanceof String) {
            String argStr = (String) arg;
            if (argStr.isEmpty()) {
                throw new IllegalArgumentException(exceptionMsg);
            }
        } else if (arg instanceof Collection) {
            Collection collection = (Collection) arg;
            if (collection.isEmpty()) {
                throw new IllegalArgumentException(exceptionMsg);
            }
        } else if (arg instanceof Map) {
            Map map = (Map) arg;
            if (map.isEmpty()) {
                throw new IllegalArgumentException(exceptionMsg);
            }
        }
    }

    /**
     * 检查非空
     *
     * @param arg 参数
     * @return boolean
     */
    @SuppressWarnings("rawtypes")
    private boolean checkNotEmpty(Object arg) {
        if (arg == null) {
            return false;
        }
        if (arg instanceof String) {
            String argStr = (String) arg;
            if (argStr.isEmpty()) {
                return false;
            }
        } else if (arg instanceof Collection) {
            Collection collection = (Collection) arg;
            if (collection.isEmpty()) {
                return false;
            }
        } else if (arg instanceof Map) {
            Map map = (Map) arg;
            if (map.isEmpty()) {
                return false;
            }
        }
        return true;
    }


}




