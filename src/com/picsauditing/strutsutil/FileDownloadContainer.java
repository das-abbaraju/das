package com.picsauditing.strutsutil;


public final class FileDownloadContainer {
	
	private String contentDisposition;
	private String contentType;
	private byte[] file;
	
	public final String getContentDisposition() {
		return contentDisposition;
	}
	
	public final String getContentType() {
		return contentType;
	}
	
	public final byte[] getFile() {
		return file;
	}
	
	public static class Builder {
		
		private String contentDisposition;
		private String contentType;
		private byte[] file;
		
		public Builder contentDisposition(final String contentDisposition) {
			this.contentDisposition = contentDisposition;
			return this;
		}
		
		public Builder contentType(final String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		public Builder file(final byte[] file) {
			this.file = file;
			return this;
		}
		
		public FileDownloadContainer build() {
			FileDownloadContainer fileDownloadContainer = new FileDownloadContainer();
			fileDownloadContainer.contentDisposition = contentDisposition;
			fileDownloadContainer.contentType = contentType;
			fileDownloadContainer.file = file;
			
			return fileDownloadContainer;
		}
		
	}

}
