package com.dfire.plugin.onekey.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;

import com.dfire.plugin.onekey.Activator;
import com.dfire.plugin.onekey.handlers.Handler;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerUtil extends Handler {

	private static String workPath;// 插件的项目路径

	public static Template getTemplate(String name) {
		try {
			// 通过Freemaker的Configuration读取相应的ftl
			Configuration cfg = new Configuration();
			// 设定去哪里读取相应的ftl模板文件
			URL url = Activator.getDefault().getBundle().getResource("ftl");
			String resourcesPath = FileLocator.toFileURL(url).getPath();
			// 设置对象包装器
			cfg.setObjectWrapper(new DefaultObjectWrapper());
			// 设置异常处理器
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
			// 从哪里加载模板文件
			try {
				cfg.setDirectoryForTemplateLoading(new File(resourcesPath));
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(window.getShell(), "Error", e.toString());
			}
			// 在模板文件目录中找到名称为name的文件
			Template temp = cfg.getTemplate(name);
			return temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 输出文件
	 * 
	 * @param name
	 * @param root
	 * @param Template
	 */
	public static void fprint(String outFile, Map<String, Object> root, Template tp) {

		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
			tp.process(root, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				out.close();
		}
	}
}