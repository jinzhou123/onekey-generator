package com.dfire.plugin.onekey.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.dialogs.MessageDialog;

import com.dfire.plugin.onekey.handlers.Handler;
import com.dfire.plugin.onekey.utils.FreemarkerUtil;

import freemarker.template.Template;

public class GetContent extends Handler {

	public static void writeToCsvFile(String fileName, Template tp) {
		if (tp == null) {
			MessageDialog.openError(window.getShell(), "Error", "找不到模版，请检查路径");
			return;
		}
		Map<String, Object> root = new HashMap<String, Object>();
		// 传参
		root.put("ParameterNames", ParameterNames);
		String[] NewParameterTypes = new String[num];
		Map<String, String> paraMap = new LinkedHashMap<String, String>();
		try {
			for (int i = 0; i < ParameterTypes.length; i++) {
				// 类型转化
				NewParameterTypes[i] = Signature.toString(ParameterTypes[i]);
				paraMap.put(ParameterNames[i], NewParameterTypes[i]);
			}
		} catch (IllegalArgumentException e) {
			MessageDialog.openError(window.getShell(), "Error", "莫名奇妙的问题" + e.toString());
		}

		root.put("paraMap", paraMap);
		FreemarkerUtil.fprint(fileName, root, tp);
	}

	public static void writeToJavaFile(String fileName, Template tp) {
		if (tp == null) {
			MessageDialog.openError(window.getShell(), "Error", "找不到模版，请检查路径");
			return;
		}
		Map<String, Object> root = new HashMap<String, Object>();
		//
		if (fileName.contains("TestBase.java")) {
			FreemarkerUtil.fprint(fileName, root, tp);
			return;
		}
		// 传参
		root.put("MethodName", MethodName);
		root.put("methodName", methodName);
		root.put("ServiceName", ServiceName);
		root.put("serviceName", serviceName);
		root.put("ReturnTypeName", ReturnTypeName);
		// root.put("num", num);
		root.put("ParameterNames", ParameterNames);
		// root.put("ParameterTypes", ParameterTypes);
		String[] NewParameterTypes = new String[num];
		Map<String, String> paraMap = new LinkedHashMap<String, String>();
		try {
			for (int i = 0; i < ParameterTypes.length; i++) {
				// 类型转化
				NewParameterTypes[i] = Signature.toString(ParameterTypes[i]);
				paraMap.put(ParameterNames[i], NewParameterTypes[i]);
			}
		} catch (IllegalArgumentException e) {
			MessageDialog.openError(window.getShell(), "Error", "莫名奇妙的问题" + e.toString());
		}

		root.put("paraMap", paraMap);
		FreemarkerUtil.fprint(fileName, root, tp);
	}
}
