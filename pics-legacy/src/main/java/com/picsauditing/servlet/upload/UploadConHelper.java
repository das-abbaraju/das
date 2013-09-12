package com.picsauditing.servlet.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadConHelper extends UploadHelperController {
	public void init(HttpServletRequest request, HttpServletResponse response){
		super.init(request, response);
		try {
			include("StreamUploader", request, response);
		}catch(Exception e){
			try {
				include("/exception_handler.jsp", request, response);
			} catch(Exception ex){
			}
		}
	}
}
