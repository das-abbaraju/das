package com.picsauditing.servlet.upload;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadHelperController {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public void init(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	protected void forward(String target, HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			RequestDispatcher dispatcher = request.getRequestDispatcher(target);
			if(dispatcher == null)
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			else
				dispatcher.forward(request, response);
		}catch(IOException e){
			throw new Exception(e);
		}catch(ServletException e){
			throw new Exception(e);
		}
	}
	
	protected void include(String target, HttpServletRequest request, HttpServletResponse response) throws Exception{
		try{
			RequestDispatcher dispatcher = request.getRequestDispatcher(target);
			if(dispatcher == null)
				response.sendError(HttpServletResponse.SC_NO_CONTENT);
			else
				dispatcher.include(request, response);
		}catch(IOException e){
			throw new Exception(e);
		}catch(ServletException e){
			throw new Exception(e);
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}	
}
