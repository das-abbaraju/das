package com.picsauditing.util;

import static com.picsauditing.util.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class FileUtilsTest {
	@Test
	public void testGetSimilarFiles() {
		File folder = new File("tests");
		File[] files = FileUtils.getSimilarFiles(folder, "test_notes");
		assertTrue(files.length == 2);
	}

//	@Test
//	public void testDelete() {
//		File file = FileUtils.ensurePathExists("TestFolder/subfolder/subsub/subsubsub");
//		System.out.println(file.getAbsolutePath());
//		file = new File("TestFolder/subfolder/subsub/subsubsub/something.jsp");
//		assertTrue(file.exists());
//		assertTrue("Failed to delete something.jsp", file.delete());
//		file = new File("TestFolder/subfolder/subsub/subsubsub");
//		assertTrue("Failed to delete subsubsub", file.delete());
//		file = new File("TestFolder/subfolder/subsub");
//		assertTrue("Failed to delete subsub", file.delete());
//	}

	@Test
	public void testSize() {
		assertEquals("1 Bytes", FileUtils.size(1));
		assertEquals("1.2 KB", FileUtils.size(1234));
		assertEquals("1.2 MB", FileUtils.size(1234000));
		assertEquals("1.2 GB", FileUtils.size(1234000000));
	}

	@Test
	public void testGetExtension() {
		assertEquals("gif", FileUtils.getExtension("filename.gif"));
		assertEquals("jpeg", FileUtils.getExtension("filename.jpeg"));
		assertEquals("", FileUtils.getExtension("filename"));
	}

	@Test
	public void testThousandize() throws Exception {
		assertEquals("", FileUtils.thousandize(0));
		assertEquals("", FileUtils.thousandize(1));
		assertEquals("", FileUtils.thousandize(10));
		assertEquals("", FileUtils.thousandize(100));
		assertEquals("", FileUtils.thousandize(999));
		assertEquals("100/", FileUtils.thousandize(1000));
		assertEquals("100/", FileUtils.thousandize(10000));
		assertEquals("100/", FileUtils.thousandize(100000));
		assertEquals("100/", FileUtils.thousandize(100001));
		assertEquals("100/", FileUtils.thousandize(100999));
		assertEquals("101/", FileUtils.thousandize(101000));
		assertEquals("100/000/", FileUtils.thousandize(1000000));
		assertEquals("221/535/", FileUtils.thousandize(2215356));
		assertEquals("987/654/", FileUtils.thousandize(987654321));
		// 2 billion is approaching the limit of an int type -- not that we'd
		// get there for a while yet...
		assertEquals("198/765/432/", FileUtils.thousandize(1987654321));
		assertEquals("200/000/000/", FileUtils.thousandize(2000000000));

	}

	@Test
	public void testMassMove() throws Exception {
		Map<Integer, Integer> pairings = new HashMap();
		pairings.put(4, 456789);
		pairings.put(123456, 654321);
		pairings.put(234567, 765432);
		pairings.put(345678, 3);
		String scriptTemplate = "mv FromBase/${sourceHashFolder}fromPrefix_${fromID}.jpg to/base/${destinationHashFolder}toPrefix_${toID}.jpg\n";

		String shellScript = FileUtils.massManipulateScript(pairings, scriptTemplate);
		assertContains("mv FromBase/fromPrefix_4.jpg to/base/456/toPrefix_456789.jpg", shellScript);
		assertContains("mv FromBase/123/fromPrefix_123456.jpg to/base/654/toPrefix_654321.jpg", shellScript);
		assertContains("mv FromBase/234/fromPrefix_234567.jpg to/base/765/toPrefix_765432.jpg", shellScript);
		assertContains("mv FromBase/345/fromPrefix_345678.jpg to/base/toPrefix_3.jpg", shellScript);
	}

	@Test
	public void testSingleMove() throws Exception {
		String scriptTemplate = "\r\n# osha_${fromID} -> data_${toID}\r\n"
				+ "files=`ls /var/pics/www_files/files/${sourceHashFolder}osha_${fromID}.*`\r\n"
				+ "for i in $files; do \r\n" + "	ext=`echo $i|awk -F . '{print $NF}'`\r\n"
				+ "	mkdir -p /var/pics/www_files/files/${destinationHashFolder}\r\n"
				+ "	mv $i /var/pics/www_files/files/${destinationHashFolder}data_${toID}.${ext}\r\n" + "done\r\n";
		VelocityAdaptor adaptor = new VelocityAdaptor();

		String shellScript = FileUtils.singleManipulateScript(adaptor,123456, 654321, scriptTemplate);
		String expected = "\r\n# osha_123456 -> data_654321\r\n"
				+ "files=`ls /var/pics/www_files/files/123/osha_123456.*`\r\n" + "for i in $files; do \r\n"
				+ "	ext=`echo $i|awk -F . '{print $NF}'`\r\n" + "	mkdir -p /var/pics/www_files/files/654/\r\n"
				+ "	mv $i /var/pics/www_files/files/654/data_654321.${ext}\r\n" + "done\r\n";
		assertEquals(expected, shellScript);
	}
}
