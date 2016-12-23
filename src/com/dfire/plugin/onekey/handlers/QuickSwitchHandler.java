package com.dfire.plugin.onekey.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class QuickSwitchHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public QuickSwitchHandler() {
	}

	public static IWorkbenchWindow window;

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@SuppressWarnings({ "restriction", "unused", "null" })
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage wbPage = window.getActivePage();
		IEditorPart part = wbPage.getActiveEditor();
		IProject project = null;
		IFile file = null;
		if (part != null) {
			Object object = part.getEditorInput().getAdapter(IFile.class);
			if (object != null) {
				file = ((IFile) object);
				project = file.getProject();
			}
			String filepath = file.getFullPath().removeFirstSegments(1).toString();
			try {
				if (filepath.contains("Test.java")) {
					String temp = filepath.replace("src/test/java/com/dfire/test/", "src/test/resources/testcase/");
					String csvpath = temp.replace(".java", ".csv");
					IFile csv = project.getFile(csvpath);
					if (csv.exists()) {
						IDE.openEditor(wbPage, csv);
					}
				} else if (filepath.contains("Test.csv")) {
					String temp = filepath.replace("src/test/resources/testcase/", "src/test/java/com/dfire/test/");
					String csvpath = temp.replace(".csv", ".java");
					IFile csv = project.getFile(csvpath);
					if (csv.exists()) {
						IDE.openEditor(wbPage, csv);
					}
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
