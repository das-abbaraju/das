package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class Prequal_OSHAProcessor extends AbstractUploadProcessor {
		
	private final String errorMsg = "Only .pdf, .doc, .txt, .xls and .jpg files are accepted. Please change %s to this format, or contact PICS for help";
	
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception{
		
		long lret = -1l;
		String OID = request.getParameter("oID");
		fields.put("OID", OID);
		
		if (!stream.isFormField()) {
		    String fieldName = stream.getFieldName();		    
		    String fileName = stream.getName();       
		    
		    if (fileName != null) 
		        fileName = FilenameUtils.getName(fileName);		        
		    
		    fields.put(fieldName, fileName);
		    String dir = "";
		    
		    checkDirectory(ftpDir + getDirectory() + "/oshas");
		    	    
		    if(fieldName.equals("osha1_file"))		    	
		    	dir = getDirectory() + "/oshas/osha1_";
		    	
		   		    
		    if(fieldName.equals("osha2_file"))
		    	dir = getDirectory() + "/oshas/osha2_";
		  
		    
		    if(fieldName.equals("osha3_file"))
		    	dir = getDirectory() + "/oshas/osha3_";
		   
		    
		    //Process a file upload
		    if (writeToFile) {
		    	String ext = FilenameUtils.getExtension(fileName);
		    	if(ext == null || ext == "")
		    		return lret;
		    	
		    	if(!checkExtension(ext)){
					request.setAttribute("error", String.format(errorMsg, fileName));
		    	    return  lret;
		    	}
		    	
		    	String fn = ftpDir + dir + OID + ".";
		    	deleteFile(fn);
		    	fn+=ext;		    	
		    	
		    	if(!async)
		    		lret = saveFile(stream, fn, fieldName);
		    	
		        
		    }
		    
		}
		
		return lret;
		
	}
	
		
}
