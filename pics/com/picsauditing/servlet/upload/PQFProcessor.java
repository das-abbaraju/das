package com.picsauditing.servlet.upload;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class PQFProcessor extends AbstractUploadProcessor{
	
	String id;
	String conID;
	String catID;
	String insertQuery="";
	
	private final String errorMsg = "Only .pdf, .doc, .txt, .xls and .jpg files are accepted. Please change $ to this format, or contact PICS for help";
	
	@Override
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception {
		long lret = -1l;		
				
		if (!stream.isFormField()) {
			String fieldName = stream.getFieldName();
			String fileName = stream.getName();
		    String ext = FilenameUtils.getExtension(fileName);
		    if (fileName != null) {
		        fileName = FilenameUtils.getName(fileName);		        
		    }
    
		    if (writeToFile) {		    	
		    	String qID =fieldName.substring(7);
		    	fields.put(fieldName, fileName);

				if (fileName == null || fileName.equals("")) {
					return lret;
				}					
				if(!checkExtension(ext))
					request.setAttribute("error_" + qID, errorMsg);				
				else {
					
					String temp = File.separator;
					String folderPath = ftpDir + getDirectory() +temp+"pqf"+temp+"qID_"+qID;

					checkDirectory(folderPath);
				    
					
					String ret = folderPath+temp+qID+"_"+conID+".";				
			        deleteFile(ret);
					ret+= ext.toLowerCase();
					
					if(!async)
						lret = saveFile(stream, ret, fieldName);
					
					
				}
					
		    }
		}
		
		return lret;
		    
	}

	
	protected boolean postFormFieldProcess(String[] values) {
		super.postFormFieldProcess(values);
		if(values[0].equals("catDoesNotApply")){
			boolean catDoesNotApply = "Yes".equals(values[1]);			
			if (catDoesNotApply) 
				return false;
		}
		
		return true;
	}	
	
	public void init(HttpServletRequest request){
		super.init(request);
		conID = request.getParameter("id");
		id = conID;
		catID = request.getParameter("catID");
	}
		
	
}
