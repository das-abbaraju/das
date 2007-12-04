package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class SimpleUploadProcessor extends AbstractUploadProcessor{

	@Override
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception {
		long lret = -1l;
		
		if (!stream.isFormField()) {
		    String fileName = stream.getName();
		    if (fileName != null) {
		        fileName = FilenameUtils.getName(fileName);		        
		    }		   
		    if (writeToFile) {
		    	String ret = ftpDir + getDirectory() + "/" + fileName;
		    	if(!async)
		    		lret = saveFile(stream, ret, stream.getFieldName());		    		
		    	
		        
		    }
		}
		
		return lret;
		    
	}
}
