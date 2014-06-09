package com.picsauditing.employeeguard.util;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageHelper {

	public static final int THUMBNAIL_DEFAULT_SCALE=300;
	public static final String THUMBNAIL_DEFAULT_FORMAT="jpg";

	/**
	 *
	 * Given scale=300
	 *
	 * Example 1
	 * 3000 X 1000
	 * new Width=300, new Height= (300X1000)/3000
	 *
	 * Example 2
	 * 1000 X 1000
	 * new Height=300, new Width= (300X1000)/3000
	 *
	 * Example 3
	 * 100 X 50
	 * Same values returned since both are < scale value
	 *
	 * @param scale
	 * @return
	 */
	public static AspectResult calculateThumnailSize(int width, int height, int scale){
		int newWidth=0;
		int newHeight=0;

		if(width < scale && height < scale){
			return new AspectResult(width, height);
		}

		if(width ==0  || height == 0){
			return new AspectResult(width, height);
		}

		if(width > height){
			newWidth = scale;
			newHeight = (scale*height)/width;
		}
		else{
			newHeight = scale;
			newWidth =  (scale*width)/height;

		}

		return new AspectResult(newWidth, newHeight);
	}

	public static InputStream resizeImage(int width, int height, BufferedImage originalImage, String formatName) throws IOException {

		BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.BALANCED, width, height);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, formatName, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());

		return is;
	}

	public static class AspectResult{
		private int width;
		private int height;

		private AspectResult(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
}
