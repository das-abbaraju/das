package com.picsauditing.actions;

import java.io.File;

import com.opensymphony.xwork2.ActionSupport;

public class FileUploadActionSupport extends PicsActionSupport 
{
	protected File Filedata = null;
	protected String FiledataContentType = null;
	protected String FiledataFileName = null;
	protected String Filename = null;
	protected String Upload = null;
	
	
	public String execute() throws Exception
	{
		if( Filedata != null )
		{
			System.out.println("---");
			System.out.println(FiledataFileName);
			System.out.println(Filename);
			System.out.println(Upload);
			
			
			System.out.println("---");
		}
		else
		{
			System.out.println("File was not uploaded");
		}
			
		return SUCCESS;
	}

	public File getFiledata() {
		return Filedata;
	}

	public void setFiledata(File filedata) {
		Filedata = filedata;
	}

	public String getFiledataContentType() {
		return FiledataContentType;
	}

	public void setFiledataContentType(String filedataContentType) {
		FiledataContentType = filedataContentType;
	}

	public String getFiledataFileName() {
		return FiledataFileName;
	}

	public void setFiledataFileName(String filedataFileName) {
		FiledataFileName = filedataFileName;
	}

	public String getFilename() {
		return Filename;
	}

	public void setFilename(String filename) {
		Filename = filename;
	}

	public String getUpload() {
		return Upload;
	}

	public void setUpload(String upload) {
		Upload = upload;
	}


}
