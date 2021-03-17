package com.loamen.javadoc.generator.utils;

import com.loamen.javadoc.generator.entity.Modifier;

/**
 * The type String util.
 *
 * @program: javadoc -generator
 * @description: 字符串操作
 * @author: don
 * @create: 2021 -01-22 15:48
 */
public class StringUtil {
    private StringUtil() {
    }

    /**
     * Is null or empty boolean.
     *
     * @param string the string
     * @return the boolean
     */
    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * Gets modifier.
     *
     * @param modifier the modifier
     * @return the modifier
     */
    public static Modifier getModifier(String modifier) {
        Modifier fieldModifier = null;
        modifier = modifier.trim();
        if (!StringUtil.isNullOrEmpty(modifier)) {
            String[] modifiers = modifier.split("&");
            for (int i = 0; i < modifiers.length; i++) {
                if (modifiers[i].equalsIgnoreCase("PUBLIC")) {
                    fieldModifier = Modifier.PUBLIC;
                } else if (modifiers[i].equalsIgnoreCase("PROTECTED")) {
                    fieldModifier = Modifier.PROTECTED;
                } else if (modifiers[i].equalsIgnoreCase("PRIVATE")) {
                    fieldModifier = Modifier.PRIVATE;
                } else {
                    fieldModifier = Modifier.PACKAGE;
                }
            }
        } else {
            fieldModifier = Modifier.PUBLIC;
        }
        return fieldModifier;
    }
}
