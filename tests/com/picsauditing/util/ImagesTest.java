package com.picsauditing.util;

import junit.framework.TestCase;

public class ImagesTest extends TestCase {
	String imgDir = "WebContent/images/";

	public void testGetWidthJpg() {
		try {
			long width = Images.getWidth(imgDir+"3dsquare2.jpg");
			assertEquals(238, width);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testGetWidthPng() {
		try {
			long width = Images.getWidth(imgDir+"icon_link_email.png");
			assertEquals(11, width);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testGetWidthGif() {
		try {
			long width = Images.getWidth(imgDir+"arrow.gif");
			assertEquals(9, width);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
