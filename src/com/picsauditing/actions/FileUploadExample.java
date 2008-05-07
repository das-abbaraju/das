package com.picsauditing.actions;

import java.io.File;

import com.opensymphony.xwork2.ActionSupport;

public class FileUploadExample extends ActionSupport 
{
	protected File theFile = null;
	protected String theFileContentType = null;
	protected String theFileFileName = null;
	
	public String input()
	{
		addActionMessage("Please Upload a file");
		return INPUT;
	}
	
	public String execute()
	{
		if( theFile != null )
		{
			System.out.println("File Uploaded: " + theFileFileName + "\t Content Type: " + theFileContentType);
			System.out.println("Current File Path: " + theFile.getAbsolutePath() );
			addActionMessage("File Uploaded Successfully");
		}
		else
		{
			System.out.println("File was not uploaded");
			addActionError("Please upload a file");
		}
			
		return SUCCESS;
	}


	public void setTheFile(File theFile) {
		this.theFile = theFile;
	}
	public void setTheFileContentType(String theFileContentType) {
		this.theFileContentType = theFileContentType;
	}
	public void setTheFileFileName(String theFileFileName) {
		this.theFileFileName = theFileFileName;
	}
}
