package com.picsauditing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public class Downloader {
	/**
	 * If the <code>getBufferSize()</code> method is not overridden, this is
	 * the buffer size that will be used to transfer the data to the servlet
	 * output stream.
	 */
	protected static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	protected int contentLength = 0;
	protected File file;
	protected String filename;

	private HttpServletResponse resp;
	private ServletOutputStream op;
	private ServletContext context;

	public Downloader(HttpServletResponse resp, ServletContext context) {
		this.resp = resp;
		this.context = context;
	}

	public void download(InputStream in, int size, String contentType, String filename) throws IOException {
		try {
			resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
			resp.setContentType(contentType);
			resp.setContentLength(size);
			byte[] bbuf = new byte[DEFAULT_BUFFER_SIZE];
	
			int length = 0;
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				resp.getOutputStream().write(bbuf, 0, length);
			}
			
			in.close();
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		} finally {
			if (in != null)
				in.close();
		}
	}

	public void download(File file, String filename) throws FileNotFoundException, IOException {
		InputStream stream;
		stream = new FileInputStream(file);
		int size = (int) file.length();
		if (filename == null)
			filename = file.getName();
		download(stream, size, getMimeType(context, filename), filename);
	}

	static public String getMimeType(ServletContext context, String filename) {
		String mimeType = context.getMimeType(filename);

		if (mimeType == null)
			return "application/octet-stream";
		else
			return mimeType;
	}
}
