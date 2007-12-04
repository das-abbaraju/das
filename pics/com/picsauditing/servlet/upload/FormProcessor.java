package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class FormProcessor extends AbstractUploadProcessor{
	
	@Override
	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception {
		long lret = -1l;
		if (!stream.isFormField()) {
			String fileName = stream.getName();
			if (fileName != null) {
		        fileName = FilenameUtils.getName(fileName);
		    }

			if(fileName == null || fileName.equals(""))
		    	writeToFile = false;

		    if (writeToFile) {
		       String ret;
		       String emailDir = (String)request.getAttribute("welcomeEmailDir");
		       String userManualDir = (String)request.getAttribute("userManualDir");
		       if(emailDir != null && !emailDir.equals("")){
		    	   if (!"doc".equalsIgnoreCase(FilenameUtils.getExtension(fileName))) {
		   			 request.setAttribute("error", "The file must be a word document (.doc)");
		   			 return lret;
		    	   }
		    	   checkDirectory(ftpDir + "/" + emailDir);
		    	   ret = ftpDir + "/" + emailDir + "/welcome.doc";
		       }else if(userManualDir != null && !userManualDir.equals("")){
		    	   if (!"pdf".equalsIgnoreCase(FilenameUtils.getExtension(fileName))) {
		   			 request.setAttribute("error", "The user manual must be a .pdf file");
		   			 return lret;
		    	   }//if
		    	   checkDirectory(ftpDir+"/"+userManualDir);
		    	   ret = ftpDir+"/"+userManualDir+"/userManual.pdf";
		       }else{
			       ret = ftpDir+getDirectory()+"/formX_X.";
			       ret+= FilenameUtils.getExtension(fileName).toLowerCase();
		       }//else

		       if(!async)
		    	   lret = saveFile(stream, ret, "fileName");
		    }
		}
		return lret;
	}
}
