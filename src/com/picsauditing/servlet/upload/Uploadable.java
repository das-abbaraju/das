package com.picsauditing.servlet.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemStream;

public interface Uploadable {
	
	public void init(HttpServletRequest req);
	public void delete();
	public boolean processFormField(FileItemStream stream) throws Exception;
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception;
	public void preProcess();
	public void postProcess();
	public String getFtpDir();
	public String getDirectory();
	public long saveFile(FileItemStream stream, String fileName, String fieldName) throws Exception;
		
		
}
