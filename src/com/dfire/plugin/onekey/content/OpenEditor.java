package com.dfire.plugin.onekey.content;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.dfire.plugin.onekey.handlers.Handler;

public class OpenEditor extends Handler {

	public static void openeditor(String fName) {
		// 打开文件
		if (fName != null) {
			IWorkbenchPage wbPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IFile file = pjt.getFile(fName);
			try {
				// 因为新创建，所以需要刷新文件，否则会找不到路径
				file.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
			try {
				if (file != null) {
					IDE.openEditor(wbPage, file);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			return;
		}
	}
}
