package com.picsauditing.util;

import java.io.File;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {
	public void testGetSimilarFiles() {
		try {
			File folder = new File("WebContent/images");
			File[] files = FileUtils.getSimilarFiles(folder, "button_continue");
			for (File file : files) {
				System.out.println(file.getAbsoluteFile());
			}
			assertTrue(files.length == 2);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testGetFtpDir() {
		try {
			File file = FileUtils.ensurePathExists("WebContent/struts/trevor/test/trevor.jsp");
			System.out.println(file.getAbsolutePath());
			file = new File("WebContent/struts/trevor/test/trevor.jsp");
			assertTrue("Failed to delete trevor.jsp", file.delete());
			file = new File("WebContent/struts/trevor/test");
			assertTrue("Failed to delete test", file.delete());
			file = new File("WebContent/struts/trevor");
			assertTrue("Failed to delete trevor", file.delete());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testSize() {
		assertEquals("1 Bytes", FileUtils.size(1));
		assertEquals("1.2 KB", FileUtils.size(1234));
		assertEquals("1.2 MB", FileUtils.size(1234000));
		assertEquals("1.2 GB", FileUtils.size(1234000000));
	}
	
	public void testGetExtension() {
		assertEquals(FileUtils.getExtension("filename.gif"), "gif");
		assertEquals(FileUtils.getExtension("filename.jpeg"), "jpeg");
		assertEquals(FileUtils.getExtension("filename"), "");
	}
}
