package com.picsauditing.strutsutil;

import org.apache.struts2.dispatcher.StreamResult;

import com.opensymphony.xwork2.ActionInvocation;

public class FileResult extends StreamResult {

	private static final long serialVersionUID = -7418715043646785992L;
	
	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		FileDownloadContainer fileContainer = (FileDownloadContainer) invocation.getStack().findValue("fileContainer");
		if (fileContainer == null) {
			return;
		}
		
		contentDisposition = fileContainer.getContentDisposition();
		inputStream = fileContainer.getFileInputStream();
		contentType = fileContainer.getContentType();
		
		super.doExecute(finalLocation, invocation);
	}

}
