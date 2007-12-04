package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class CertificateProcessor extends AbstractUploadProcessor{
	
	private final String errorMsg = "Only .pdf, .doc, .txt or .jpg files are accepted. Please change your document to this format, or contact PICS for help";
	
	@Override
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception {
		long lret = -1l;
				
		String contractor_id = (String)request.getAttribute("contractor_id");
		String fn = "/cert_" + contractor_id + "_";
		
		if (!stream.isFormField()) {
		    String fileName = stream.getName();
		    if (fileName != null) {
		        fileName = FilenameUtils.getName(fileName);		        
		    }
		    if(!checkExtension(FilenameUtils.getExtension(fileName)))
				fields.put("error", errorMsg);		    
		    else if(writeToFile) {		    	
			       String ret = ftpDir + getDirectory() + fn + fileName;
			       if(!async)
			    	   lret = saveFile(stream, ret, "fileName");				       
			 
		    }
		}
		
		return lret;
		    
	}

}
