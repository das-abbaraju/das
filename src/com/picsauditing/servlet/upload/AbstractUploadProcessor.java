package com.picsauditing.servlet.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import com.picsauditing.util.Strings;

public abstract class AbstractUploadProcessor implements Uploadable {

    protected HttpServletRequest request = null;	
	protected Map<String,String> fields = new HashMap<String,String>();
	protected Map<String,String> files = new HashMap<String,String>();
	protected boolean bExtOK = true;
	protected String ftpDir = "";
	protected String directory;
		
	public void init(HttpServletRequest request){
		this.request = request;
		ftpDir = System.getProperty("pics.ftpDir");
		if(Strings.isEmpty(ftpDir))
			ftpDir = request.getSession().getServletContext().getInitParameter("FTP_DIR");
		directory = (String)request.getAttribute("directory");
		if(directory == null)
			directory = "";
	}
	
	public void delete(){
		
	}

	protected boolean postFormFieldProcess(String[] values) {
		if(values.length == 2)
	    	   fields.put(values[0],values[1]);
		
		return true;
		
	}
		
	public void postProcess(){
		request.setAttribute("uploadfields", fields);
		
		
	}
	
	public void preProcess(){
		if(!getDirectory().equals(""))
			checkDirectory(ftpDir + getDirectory());
	}

	public boolean processFormField(FileItemStream stream) throws Exception {
		String[] strings = new String[2];
		if (stream.isFormField()) {
			 strings[0] = stream.getFieldName();
		     strings[1] = Streams.asString(stream.openStream());
		     return postFormFieldProcess(strings);
		}
		
		return true;
	}	

	public Map<String, String> getParams() {
		return fields;
	}
		
	public Map<String, String> getFiles() {
		return files;
	}

	protected boolean checkExtension(String ext){
		String exts = (String)request.getAttribute("exts");
		if(exts == null || exts.equals(""))
			return true;
		
		String[] list = exts.split(",");
		for(int i = 0; i < list.length; i++)
			if(list[i].equals(ext.toLowerCase()))
				return true;
		
		return false;
	}

	public abstract long uploadFile(FileItemStream stream,
			boolean writeToFile, boolean async) throws Exception;	
	
	public String getFtpDir(){
		return ftpDir;
	}

	public boolean isBExtOK() {
		return bExtOK;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String getDirectory() {
		if(!directory.equals("") && !directory.startsWith("/"))
			directory = "/" + directory;
		return directory;
	}
	
	protected void checkDirectory(String dir){
		File newFolderFile = new File(dir);
		if (!newFolderFile.exists())
			newFolderFile.mkdirs();
	}
	
	protected void deleteFile(String fn){
		String[] exts = new String[] {"pdf", "jpg", "doc", "txt", "xls", "gif", "zip"};
		for(int i = 0; i < exts.length; i++){
			File f = new File(fn + exts[i]);
			if(f.exists())
				f.delete();
		}
	}
	
	public long saveFile(FileItemStream stream, String fn, String fieldName) throws Exception{
		FileOutputStream uploadedFile = new FileOutputStream(fn);
        long lret = Streams.copy(stream.openStream(), uploadedFile,true);
        request.setAttribute(fieldName, fn);
        return lret;
	}
}
