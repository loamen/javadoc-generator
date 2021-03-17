package com.loamen.javadoc.generator;

import com.sun.javadoc.Doclet;
import com.sun.javadoc.RootDoc;

/**
 * 用来获取javadoc解析完成后生成的语法树根节点
 *
 * @author Don
 */
public class CustomDoclet extends Doclet {
	/**
	 * 	静态对象，用于接收javadoc解析完成后生成的语法树根节点<br>
	 * 	在后面我们会用他来获取我们需要的数据
	 * */
	private static RootDoc root;

	/**
	 * 在javadoc解析完java文件后，生成语法树，然后就会调用这个方法去让Doclet生成doc文档
	 *
	 * @param rootDoc the root doc
	 * @return the boolean
	 */
	public static boolean start(RootDoc rootDoc) {
		CustomDoclet.root = rootDoc;
		return true;
	}

	/**
	 * 获取语法树的根节点
	 *
	 * @return the root
	 */
	public static RootDoc getRoot() {
		return root;
	}

}
