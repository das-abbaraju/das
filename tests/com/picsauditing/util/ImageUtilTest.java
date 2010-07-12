package com.picsauditing.util;

import java.awt.image.BufferedImage;
import java.io.File;

import junit.framework.TestCase;

public class ImageUtilTest extends TestCase {
	
	public void testCreateBufferedImage(){
		BufferedImage image;
		String ftpDir = "C:/temp";
		image = ImageUtil.createBufferedImage(ftpDir, "/files/", "img1", "jpg");
		assertTrue(image!=null);
	}
	
	public void testResize(){
		BufferedImage image, img;
		String ftpDir = "C:/temp";
		image = ImageUtil.createBufferedImage(ftpDir, "/files/", "img2", "jpg");
		img = ImageUtil.resize(image, 400, 400, false);
		assertTrue(img.getWidth()==400 && img.getHeight()==400);
	}
	
	public void testWriteImageWithQuality(){
		File test1 = null, test2 = null;
		BufferedImage image;
		String ftpDir = "C:/temp";
		test1 = new File(ftpDir + "/files/img3.jpg");
		image = ImageUtil.createBufferedImage(ftpDir, "/files/", "img3", "jpg");
		String size1 = FileUtils.size(test1);
		test2 = ImageUtil.writeImageWithQuality(image, "jpg", .75f);
		assertFalse(size1.equals(FileUtils.size(test2)));
	}
	
	public void testCrop(){
		BufferedImage image;
		String ftpDir = "C:/temp";
		image = ImageUtil.createBufferedImage(ftpDir, "/files/", "img4", "jpg");
		image = ImageUtil.crop(image, 100, 100, 100, 100);
		assertTrue(image.getWidth()==100 && image.getHeight()==100);		
	}
}
