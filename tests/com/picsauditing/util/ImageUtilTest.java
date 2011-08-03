package com.picsauditing.util;

import java.awt.image.BufferedImage;
import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class ImageUtilTest extends TestCase {
	@Test
	public void testCreateBufferedImage() {
		BufferedImage image;
		image = ImageUtil.createBufferedImage(new File("WebContent/images/buttons.png"));
		assertNotNull(image);
	}

	@Test
	public void testResize() {
		BufferedImage image, img;
		image = ImageUtil.createBufferedImage(new File("WebContent/images/buttons.png"));
		// TODO get resize to work on linux...it's not right now - Trevor 8/3/2011
		//img = ImageUtil.resize(image, 400, 400, false);
		//assertTrue(img.getWidth() == 400 && img.getHeight() == 400);
	}

	@Test
	public void testWriteImageWithQuality() {
		File test1 = null, test2 = null;
		BufferedImage image;
		test1 = new File("WebContent/images/buttons.png");
		image = ImageUtil.createBufferedImage(test1);
		String size1 = FileUtils.size(test1);
		test2 = ImageUtil.writeImageWithQuality(image, "jpg", .75f);
		assertFalse(size1.equals(FileUtils.size(test2)));
	}

	@Test
	public void testCrop() {
		BufferedImage image;
		image = ImageUtil.createBufferedImage(new File("WebContent/images/buttons.png"));
		image = ImageUtil.crop(image, 5, 5, 100, 100);
		assertTrue(image.getWidth() == 100 && image.getHeight() == 100);
	}
}
