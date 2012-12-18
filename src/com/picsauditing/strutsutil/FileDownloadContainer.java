package com.picsauditing.strutsutil;

import java.io.InputStream;


public final class FileDownloadContainer {
	
	private String contentDisposition;
	private String contentType;
	private InputStream fileInputStream;
	
	public final String getContentDisposition() {
		return contentDisposition;
	}
	
	public final String getContentType() {
		return contentType;
	}
	
	public final InputStream getFileInputStream() {
		return fileInputStream;
	}
	
	public static class Builder {
		
		private String contentDisposition;
		private String contentType;
		private InputStream fileInputStream;
		
		public Builder contentDisposition(final String contentDisposition) {
			this.contentDisposition = contentDisposition;
			return this;
		}
		
		public Builder contentType(final String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		public Builder fileInputStream(final InputStream fileInputStream) {
			this.fileInputStream = fileInputStream;
			return this;
		}
		
		public FileDownloadContainer build() {
			FileDownloadContainer fileDownloadContainer = new FileDownloadContainer();
			fileDownloadContainer.contentDisposition = contentDisposition;
			fileDownloadContainer.contentType = contentType;
			fileDownloadContainer.fileInputStream = fileInputStream;
			
			return fileDownloadContainer;
		}
		
	}

}
