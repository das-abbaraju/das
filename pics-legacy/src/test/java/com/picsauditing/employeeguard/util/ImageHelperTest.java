package com.picsauditing.employeeguard.util;

import com.sun.tools.xjc.outline.Aspect;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageHelperTest {

	private File TEST_IMAGE_FILE = new File(ClassLoader.getSystemResource("com/picsauditing/employeeguard/util/ScaredBug.jpg").getFile());


	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testCalculateThumnailSize_ExpectWidthToScaledown_1() throws Exception {
		int scale=300;
		int orgWidth=3000;
		int orgHeight=1000;
		int expectedWidth=300;
		int expectedHeight=100;

		ImageHelper.AspectResult aspectResult = ImageHelper.calculateThumnailSize(orgWidth, orgHeight, scale);

		assertEquals(expectedWidth,aspectResult.getWidth());
		assertEquals(expectedHeight, aspectResult.getHeight());

	}

	@Test
	public void testCalculateThumnailSize_ExpectNoChange() throws Exception {
		int scale=300;
		int orgWidth=100;
		int orgHeight=50;
		int expectedWidth=100;
		int expectedHeight=50;

		ImageHelper.AspectResult aspectResult = ImageHelper.calculateThumnailSize(orgWidth, orgHeight, scale);

		assertEquals(expectedWidth,aspectResult.getWidth());
		assertEquals(expectedHeight,aspectResult.getHeight());

	}

	@Test
	public void testCalculateThumnailSize_ExpectHeightToScaledown_1() throws Exception {
		int scale=300;
		int orgWidth=1000;
		int orgHeight=3000;
		int expectedWidth=100;
		int expectedHeight=300;

		ImageHelper.AspectResult aspectResult = ImageHelper.calculateThumnailSize(orgWidth, orgHeight, scale);

		assertEquals(expectedWidth,aspectResult.getWidth());
		assertEquals(expectedHeight,aspectResult.getHeight());

	}

	@Test
	public void testCalculateThumnailSize_ExpectHeightToScaledown_2() throws Exception {
		int scale=300;
		int orgWidth= -100;
		int orgHeight= 400;
		int expectedWidth= -75;
		int expectedHeight= 300;

		ImageHelper.AspectResult aspectResult = ImageHelper.calculateThumnailSize(orgWidth, orgHeight, scale);

		assertEquals(expectedWidth,aspectResult.getWidth());
		assertEquals(expectedHeight,aspectResult.getHeight());

	}

	@Test
	public void testResizeImage() throws Exception {
		BufferedImage originalImage = ImageIO.read(TEST_IMAGE_FILE);
		ImageHelper.AspectResult aspectResult = ImageHelper.calculateThumnailSize(originalImage.getWidth(), originalImage.getHeight(), ImageHelper.THUMBNAIL_DEFAULT_SCALE);

		InputStream inputStream = ImageHelper.resizeImage(aspectResult.getWidth(), aspectResult.getHeight(), originalImage, ImageHelper.THUMBNAIL_DEFAULT_FORMAT);

		assertNotNull(inputStream);
	}
}
