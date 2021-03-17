package com.loamen;

import com.loamen.javadoc.generator.JavaDocReader;
import com.loamen.javadoc.generator.utils.WordUtils;
import com.loamen.javadoc.generator.entity.ClassComment;
import com.loamen.javadoc.generator.utils.StringUtil;

import java.io.IOException;
import java.util.*;

import static com.loamen.javadoc.generator.utils.StringUtil.getModifier;

/**
 * Hello world!
 *
 * @author Don
 */
public class App {
    /**
     * The Args map.
     */
    static Map<String, String> argsMap;
    /**
     * The Is arg map.
     */
    static Map<String, Boolean> isArgMap;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) throws Exception {
        println("***********************Javadoc生成器*************************");
        println("* -source         某个java源文件或包名，可空                 *");
        println("* -sourcepath     src/main/java源文件路径，非空。            *");
        println("* -classpath      类路径，可空。                             *");
        println("* -subpackages    是否递归处理所以子包，可空。                *");
        println("* -fieldmodifier  需要处理的类字段可见性，非空。              *");
        println("* -methodmodifier 需要处理的类方法可见性，非空。              *");
        println("* -docpath        Word文档输出目录绝对路径，非空.             *");
        println("*************************************************************");

        run(args);
    }

    /**
     * Run.
     *
     * @param args the args
     */
    static void run(String[] args) {
        argsMap = new HashMap<>(7);
        isArgMap = new HashMap<>(7);

        if (args != null && args.length > 0) {
            println("命令行参数：" + args.length);

            setValue(args);

            if (!StringUtil.isNullOrEmpty(argsMap.get("sourcePath")) &&
                    !StringUtil.isNullOrEmpty(argsMap.get("fieldModifier")) &&
                    !StringUtil.isNullOrEmpty(argsMap.get("methodModifier")) &&
                    !StringUtil.isNullOrEmpty(argsMap.get("docPath"))) {
                JavaDocReader javaDocReader = null;
                try {
                    javaDocReader = new JavaDocReader(
                            argsMap.get("source"),
                            argsMap.get("sourcePath"),
                            argsMap.get("subpackages"),
                            argsMap.get("classPath"),
                            getModifier(argsMap.get("fieldModifier")),
                            getModifier(argsMap.get("methodModifier")));
                } catch (IOException e) {
                    println("错误：" + e.getMessage());
                }
                // 执行读取以获取注释
                List<ClassComment> execute = javaDocReader.execute();
                // 以自己喜欢的方式输入注释
                WordUtils wordUtils = new WordUtils();
                try {
                    wordUtils.export(execute, argsMap.get("docPath"));
                    println("文档生成完成：" + argsMap.get("docPath"));
                } catch (Exception e) {
                    println("文档生成错误：" + e.getMessage());
                }
            }
        } else {
            Scanner input = new Scanner(System.in);
            String str = null;
            String[] arr = new String[]{"source", "sourcepath", "classpath", "subpackages", "fieldmodifier", "methodmodifier", "docpath"};
            String[] args1 = new String[14];
            Integer index = 0;
            for (String s : arr) {
                println("请输入" + s + "：");
                str = input.next();
                println("您输入的是：" + str);
                args1[index] = "-" + s;
                index++;
                args1[index] = str;
                index++;
            }
            run(args1);
            input.close(); // 关闭资源
        }
    }

    /**
     * Sets value.
     *
     * @param args the args
     */
    static void setValue(String[] args) {
        for (String s : args) {
            s = s.trim();
            if (s.toLowerCase().startsWith("-")) {
                resetBoolean();
                String paramName = s.trim().substring(1, s.length());

                switch (paramName.toLowerCase()) {
                    case "source":
                        isArgMap.put("isSource", true);
                        break;
                    case "sourcepath":
                        isArgMap.put("isSourcePath", true);
                        break;
                    case "classpath":
                        isArgMap.put("isClassPath", true);
                        break;
                    case "subpackages":
                        isArgMap.put("isSubpackages", true);
                        break;
                    case "fieldmodifier":
                        isArgMap.put("isFieldModifier", true);
                        break;
                    case "methodmodifier":
                        isArgMap.put("isMethodModifier", true);
                        break;
                    case "docpath":
                        isArgMap.put("isDocPath", true);
                        break;
                    default:
                        break;
                }
            } else {
                if (mapHasVal(isArgMap, "isClassPath", true)) {
                    argsMap.put("classPath", s);
                    println("classPath:" + argsMap.get("classPath"));
                }
                if (mapHasVal(isArgMap, "isFieldModifier", true)) {
                    argsMap.put("fieldModifier", s);
                    println("fieldModifier:" + argsMap.get("fieldModifier"));
                }
                if (mapHasVal(isArgMap, "isMethodModifier", true)) {
                    argsMap.put("methodModifier", s);
                    println("methodModifier:" + argsMap.get("methodModifier"));
                }
                if (mapHasVal(isArgMap, "isSource", true)) {
                    argsMap.put("source", s);
                    println("source:" + argsMap.get("source"));
                }
                if (mapHasVal(isArgMap, "isSubpackages", true)) {
                    argsMap.put("subpackages", s);
                    println("subpackages:" + argsMap.get("subpackages"));
                }
                if (mapHasVal(isArgMap, "isSourcePath", true)) {
                    argsMap.put("sourcePath", s);
                    println("sourcePath:" + argsMap.get("sourcePath"));
                }
                if (mapHasVal(isArgMap, "isDocPath", true)) {
                    argsMap.put("docPath", s);
                    println("sourcePath:" + argsMap.get("docPath"));
                }
                resetBoolean();
            }
        }
    }

    /**
     * Println.
     *
     * @param msg the msg
     */
    static void println(String msg) {
        System.out.println(msg);
    }

    /**
     * Print.
     *
     * @param msg the msg
     */
    static void print(String msg) {
        System.out.print(msg);
    }

    /**
     * Reset boolean.
     */
    static void resetBoolean() {
        isArgMap = new HashMap<>(7);
    }

    /**
     * Map has val boolean.
     *
     * @param map the map
     * @param key the key
     * @param val the val
     * @return the boolean
     */
    static Boolean mapHasVal(Map map, String key, Boolean val) {
        if (map != null && !map.isEmpty() && map.containsKey(key) && map.get(key).equals(val)) {
            return true;
        } else {
            return false;
        }
    }
}

