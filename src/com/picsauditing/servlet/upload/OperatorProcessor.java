package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class OperatorProcessor extends AbstractUploadProcessor {

	private final String INVALID_EXTENSION = "User manuals may only be .pdf files";
	private final String NO_FILE_SELECTED = "Please select a file to upload";

	public long uploadFile(FileItemStream stream, boolean writeToFile, boolean async) throws Exception{
		long lret = -1l;
		if (!stream.isFormField()) {
			String fieldName = stream.getFieldName();
			String fileName = stream.getName();
			if (fileName != null)
				fileName = FilenameUtils.getName(fileName);
			fields.put(fieldName, fileName);
			String dir = "";
			String id = request.getParameter("id");
			//We need this for a unique fileName id
			if(id == null || id.equals("")){
				fields.put("fileid", Integer.toString(fileName.hashCode()));
			}else{
				fields.put("editID", id);
				fields.put("id", id);
			}//else

			if(fieldName.equals("userManual")){
				checkDirectory(ftpDir + "/files/userManuals");
				dir =  "/files/userManuals/userManual_";
			}//if
			//Process a file upload
			if ("".equals(fileName))
				request.setAttribute("error_userManual", NO_FILE_SELECTED);
			if (writeToFile) {
				String ext = FilenameUtils.getExtension(fileName);
				if(ext == null || ext == "")
					return lret;
				boolean bchk = checkExtension(ext);
				boolean berr = false;
		    	if(dir.contains("userManuals")){
					if(!bchk){
    					request.setAttribute("error_userManual", INVALID_EXTENSION);
    					berr = true;
    				}//if
				}//if

				if(berr)
					return lret;
				if(fields.get("fileid") != null)
					id = fields.get("fileid");

				String fn = ftpDir+dir+id+".";
				deleteFile(fn);
				fn+=ext;

				if(!async)
					lret = saveFile(stream, fn, fieldName);
			}//if
		}//if
		return lret;
	}//uploadFile
}//OperatorProcessor