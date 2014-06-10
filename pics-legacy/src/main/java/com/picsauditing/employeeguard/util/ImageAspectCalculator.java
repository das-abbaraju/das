package com.picsauditing.employeeguard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageAspectCalculator {

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
	public AspectResult calculateThumnailSize(int width, int height, int scale){
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

	private class AspectResult{
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
