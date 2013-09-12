package com.picsauditing.servlet.upload;

import org.apache.commons.fileupload.ProgressListener;

public class UploadProgressListener implements ProgressListener {
	
	private long megaBytes = -1;
	private String htmlOut = "";
			
	public String getHtmlOut() {
		return htmlOut;
	}

	public void setHtmlOut(String htmlOut) {
		this.htmlOut = htmlOut;
	}
	
	public void update(long pBytesRead, long pContentLength, int pItems) {
		   StringBuffer out = new StringBuffer();
		   
		   long mBytes = pBytesRead / 1000000;
	       if (megaBytes == mBytes) {
	           return;
	       }
	       megaBytes = mBytes;
	              
	       
		   out.append("We are currently reading item " + pItems + "\n");
	       if (pContentLength == -1) {
	           out.append("So far, " + pBytesRead + " bytes have been read.\n");
	       } else {
	           out.append("So far, " + pBytesRead + " of " + pContentLength
	                              + " bytes have been read.\n");
	       }
	       
	       htmlOut = out.toString();
	   }
}
