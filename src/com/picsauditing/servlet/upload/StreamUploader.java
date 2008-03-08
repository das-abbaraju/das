package com.picsauditing.servlet.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class for Servlet: Uploader
 *
 */
 public class StreamUploader extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public StreamUploader() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.setContentType("text/html");
	    response.setBufferSize(8192);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setBufferSize(8192);
		//PrintWriter out = response.getWriter();
		
		//		 Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if(!isMultipart)
			throw new ServletException("No file(s) to upload");
		//		 Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();
		ProgressListener listener = new UploadProgressListener();
		//request.getSession().setAttribute("listener",listener);
		upload.setProgressListener(listener);
		
		//		Create the Uploadable object
		String uploader = (String)request.getAttribute("uploader");
		if(uploader == null || uploader.equals(""))
			uploader = "-1";
		int i = Integer.parseInt(uploader);
		UploadProcessorFactory uf= new UploadProcessorFactory();
		Uploadable uploadable = uf.getUploadProcessor(i);
		uploadable.init(request);				
		uploadable.preProcess();
		
		boolean writeToFile = true;	
		String fieldsOnly = (String)request.getAttribute("fieldsOnly");
      	if(fieldsOnly != null && Boolean.parseBoolean(fieldsOnly))
      		writeToFile = false;
      	
      	boolean async = "yes".equals((String)request.getAttribute("async"));
		//		 Parse the request
		try {
			FileItemIterator iter = upload.getItemIterator(request);
		   	
			while (iter.hasNext()) {
				FileItemStream stream = iter.next();
			    if (stream.isFormField()) {
			       if(!uploadable.processFormField(stream))
			    	   writeToFile=false;
			       
			    } else {
			    	System.out.println("StreamUploader.writeToFile:"+ writeToFile);
			    	uploadable.uploadFile(stream, writeToFile, async);			    	
			    }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
	    	System.out.println("Error in StreamUploader:"+ e.getMessage());
			throw new ServletException(e);
		} finally {
			uploadable.postProcess();
			uploadable.delete();			
		}
	}
}	
		
