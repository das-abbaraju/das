package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void testGetSimilarFiles() {
		File folder = new File("tests");
		File[] files = FileUtils.getSimilarFiles(folder, "test_notes");
		assertTrue(files.length == 2);
	}

	@Test
	public void testGetFtpDir() {
		File file = FileUtils.ensurePathExists("WebContent/struts/trevor/test/trevor.jsp");
		System.out.println(file.getAbsolutePath());
		file = new File("WebContent/struts/trevor/test/trevor.jsp");
		assertTrue("Failed to delete trevor.jsp", file.delete());
		file = new File("WebContent/struts/trevor/test");
		assertTrue("Failed to delete test", file.delete());
		file = new File("WebContent/struts/trevor");
		assertTrue("Failed to delete trevor", file.delete());
	}

	@Test
	public void testSize() {
		assertEquals("1 Bytes", FileUtils.size(1));
		assertEquals("1.2 KB", FileUtils.size(1234));
		assertEquals("1.2 MB", FileUtils.size(1234000));
		assertEquals("1.2 GB", FileUtils.size(1234000000));
	}

	@Test
	public void testGetExtension() {
		assertEquals(FileUtils.getExtension("filename.gif"), "gif");
		assertEquals(FileUtils.getExtension("filename.jpeg"), "jpeg");
		assertEquals(FileUtils.getExtension("filename"), "");
	}
}
