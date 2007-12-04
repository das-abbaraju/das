package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.FilenameUtils;

public class ContractorProcessor extends AbstractUploadProcessor {
		
	private final String errorMsg_logo = "Only .jpg and .gif files are accepted logos. Please change your logo to this format, or contact PICS for help";
	private final String errorMsg_brochure = "Only .pdf, .doc, .txt or .jpg files are accepted brochures. Please change your brochure to this format, or contact PICS for help";
	
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
			}
		    
		    if(fieldName.equals("brochure_file")){
		    	checkDirectory(ftpDir + "/files/brochures");		   
		    	dir =  "/files/brochures/brochure_";
		    }
		    
		    if(fieldName.equals("logo_file")){
		    	checkDirectory(ftpDir + "/logos");
		    	dir = "/logos/logo_";
		    }
		    
		    //Process a file upload
		    if (writeToFile) {
		    	String ext = FilenameUtils.getExtension(fileName);
		    	if(ext == null || ext == "")
		    		return lret;
		    	
		    	boolean bchk = checkExtension(ext);
		    	boolean berr = false;
		    	if(dir.contains("logos")){
		    		if(!bchk || ext=="pdf" || ext=="doc" || ext=="txt"){
		    			request.setAttribute("error_logo", errorMsg_logo);
		    			berr = true;
		    		}
		    	}
		    	
		    	if(dir.contains("brochure")){
		    		if(ext.contains("gif") || !bchk){
		    			request.setAttribute("error_brochure", errorMsg_brochure);
		    			berr = true;
		    		}
		    			
		    	}
		    	
		    	if(berr)
		    		return lret;
		    	
		    	if(fields.get("fileid") != null)
		    		id = fields.get("fileid");	
		    	
		    	String fn = ftpDir + dir + id + ".";
		    	deleteFile(fn);
		    	fn+=ext;		
		    	
		    	if(!async){
		    		lret = saveFile(stream, fn, fieldName);
			    	
		    	}
		        
		    }	    	
		    
		}
		
		return lret;
		
	}
	
	
}
