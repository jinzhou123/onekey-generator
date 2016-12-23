package com.dfire.plugin.onekey.handlers;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.SourceMethod;
import org.eclipse.jdt.internal.core.SourceMethodInfo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.dfire.plugin.onekey.content.GetContent;
import com.dfire.plugin.onekey.content.OpenEditor;
import com.dfire.plugin.onekey.utils.FreemarkerUtil;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class Handler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public Handler() {
	}

	public static String first;
	public static String rest;
	public static String temp;
	public static String MethodName;// 类名
	public static String methodName;// 方法名
	public static String ServiceName;// 服务名 I+首字母大写
	public static String serviceName;// 服务名 首字母小写
	public static String projectPath;// 项目路径
	public static String testBaseFolder;
	public static String scriptsFolder;
	public static String csvFolder;
	public static int num;// 入参个数
	public static String[] ParameterNames = null;// 入参名
	public static String[] ParameterTypes = null;// 入参类型
	public static String ReturnTypeName;// 返回类型
	public static IProject pjt;
	public static IWorkbenchWindow window;

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@SuppressWarnings("restriction")
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelectionService selectionService = window.getSelectionService();
		ISelection selection = selectionService.getSelection();
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				ResourcesPlugin.getWorkspace().getRoot().getProject(), true, "选择对应的测试工程，按ok确认");
		dialog.setTitle("请选择对应的测试工程");
		boolean findlocation = false;
		boolean clickCancel = false;
		int runtime = 0;
		try {
			if (selection instanceof IStructuredSelection) {
				for (Iterator iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
					Object element = iter.next();
					if (element instanceof SourceMethod) {
						// 获取所选方法内的信息
						SourceMethodInfo sourceMethodInfo = (SourceMethodInfo) ((SourceMethod) element)
								.getElementInfo();
						getMethodInfo(element, sourceMethodInfo);
						if (runtime > 0) {
							try {
								mkDir(projectPath);
								generaterScriptsAndCsv(projectPath);
							} catch (Exception e) {
								e.printStackTrace();
								MessageDialog.openError(window.getShell(), "Error", "对文件或文件夹进行操作时，发生异常" + e.toString());
							}
						} else {
							if (clickCancel == false && dialog.open() == ContainerSelectionDialog.OK) {
								Object[] result = dialog.getResult();
								if (result.length == 1) {
									projectPath = ((Path) result[0]).toString();
									int n = projectPath.length();
									IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();
									for (int i = 0; i < project.length; i++) {
										if (project[i].getName().equals(projectPath.substring(1, n))) {
											pjt = project[i];
											projectPath = project[i].getLocation().toString();
											findlocation = true;
										}
									}
									if (findlocation) {
										if (projectPath.contains("RemoteSystemsTempFiles")) {
											MessageDialog.openError(window.getShell(), "Error", "别调皮，我知道这个不是测试工程~");
											return null;
										}
										try {
											mkDir(projectPath);
											generaterScriptsAndCsv(projectPath);
										} catch (Exception e) {
											e.printStackTrace();
											MessageDialog.openError(window.getShell(), "Error",
													"对文件或文件夹进行操作时，发生异常" + e.toString());
										}
									} else {
										MessageDialog.openError(window.getShell(), "Error", "项目路径未找到");
										return null;
									}
								} else {
									MessageDialog.openError(window.getShell(), "Error", "请至少选择一个项目");
									return null;
								}
								runtime++;
							} else {
								clickCancel = true;
							}
						}
					}
				}
			} else {
				MessageDialog.openError(window.getShell(), "Error", "别调皮，请选择要测试的方法~");
			}
		} catch (Exception ex) {
			MessageDialog.openError(window.getShell(), "Error", "啊哦。。。发生未知异常了。。" + ex.toString());
		}
		return null;
	}

	public void getMethodInfo(Object element, SourceMethodInfo sourceMethodInfo) {
		// 获取方法名
		methodName = ((SourceMethod) element).getElementName();
		temp = methodName + "Test";
		first = temp.substring(0, 1).toUpperCase();
		rest = temp.substring(1, temp.length());
		MethodName = new StringBuffer(first).append(rest).toString();
		// 获取服务名
		IJavaElement parent = ((SourceMethod) element).getParent();
		ServiceName = parent.getElementName();
		first = ServiceName.substring(0, 1).toUpperCase();
		rest = ServiceName.substring(1, ServiceName.length());
		if (first.equals("I")) {
			serviceName = new StringBuffer(rest.substring(0, 1).toLowerCase()).append(rest.substring(1, rest.length()))
					.toString();
		} else {
			serviceName = new StringBuffer(first.toLowerCase()).append(rest).toString();
		}
		// 获取入参个数
		num = ((SourceMethod) element).getNumberOfParameters();
		// 获取入参名字与类型
		try {
			ParameterNames = ((SourceMethod) element).getParameterNames();
			ParameterTypes = ((SourceMethod) element).getParameterTypes();
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		// 获取返回类型名字
		ReturnTypeName = String.valueOf(sourceMethodInfo.getReturnTypeName());

	}

	public void mkDir(String projectPath) throws Exception {
		// 新建目录
		testBaseFolder = projectPath + "/src/test/java/com/dfire/testBase";
		scriptsFolder = projectPath + "/src/test/java/com/dfire/test/" + ServiceName;
		csvFolder = projectPath + "/src/test/resources/testcase/" + ServiceName;

		File b = new File(testBaseFolder);
		File f = new File(scriptsFolder);
		File c = new File(csvFolder);
		if (!b.exists()) {
			b.mkdirs();
		}
		if (!f.exists()) {
			f.mkdirs();
		}
		if (!c.exists()) {
			c.mkdirs();
		}
	}

	public void generaterScriptsAndCsv(String projectPath) throws Exception {
		// 创建java文件与csv文件 还有TestBase
		File testbase = new File(testBaseFolder + "/TestBase.java");
		File testscript = new File(scriptsFolder + "/" + MethodName + ".java");
		File csv = new File(csvFolder + "/" + MethodName + ".csv");

		String src = null;
		// 适配mac
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			src = "/src";
		} else {
			src = "\\src";
		}

		if (testbase.exists() && testscript.exists() && csv.exists()) {
			MessageDialog.openWarning(window.getShell(), "警告", "测试脚本" + ServiceName + "." + MethodName + "已存在，请不要重复生成");
			return;
		}

		// 判断文件是否存在，没有则创建并填充内容
		if (!testbase.exists()) {
			testbase.createNewFile();
			GetContent.writeToJavaFile(testbase.getAbsolutePath(), FreemarkerUtil.getTemplate("TestBaseTemp.ftl"));
			OpenEditor.openeditor(testbase.getAbsolutePath().substring(testbase.getAbsolutePath().indexOf(src),
					testbase.getAbsolutePath().length()));
		}

		if (!csv.exists()) {
			csv.createNewFile();
			GetContent.writeToCsvFile(csv.getAbsolutePath(), FreemarkerUtil.getTemplate("CsvTemp.ftl"));
			OpenEditor.openeditor(csv.getAbsolutePath().substring(csv.getAbsolutePath().indexOf(src),
					csv.getAbsolutePath().length()));
		}

		if (!testscript.exists()) {
			testscript.createNewFile();
			GetContent.writeToJavaFile(testscript.getAbsolutePath(), FreemarkerUtil.getTemplate("ScriptTemp.ftl"));
			OpenEditor.openeditor(testscript.getAbsolutePath().substring(testscript.getAbsolutePath().indexOf(src),
					testscript.getAbsolutePath().length()));
		}

		// MessageDialog.openInformation(window.getShell(), "完成", "Done");
	}

}
