package com.picsauditing.employeeguard.services.entity.util.file;

import com.picsauditing.util.Strings;
import com.spun.util.ClassUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CsvFileImportReaderTest {

	@Test
	public void testIsValidFileType_InvalidFileType() {
		boolean result = new CsvFileImportReader().isValidFileType("abc.123");

		assertFalse(result);
	}

	@Test
	public void testIsValidFileType_ValidFileType() {
		boolean result = new CsvFileImportReader().isValidFileType("abc.CSV");

		assertTrue(result);
	}

	@Test(expected = FileImportReaderException.class)
	public void testOpen_Failure() throws FileImportReaderException {
		new CsvFileImportReader().open(null);
	}

	@Test
	public void testOpen_Success() throws FileImportReaderException {
		new CsvFileImportReader().open(getFile());
	}

	@Test(expected = FileImportReaderException.class)
	public void testReadLine_Failure() throws FileImportReaderException {
		new CsvFileImportReader().readLine();
	}

	@Test
	public void testReadLine_Successful() throws FileImportReaderException {
		FileImportReader fileImportReader = new CsvFileImportReader();
		fileImportReader.open(getFile());

		String[] result = fileImportReader.readLine();

		assertTrue(ArrayUtils.isEquals(new String[]{"this", "is", "a", "test"}, result));
	}

	@Test
	public void testClose() throws FileImportReaderException {
		FileImportReader fileImportReader = new CsvFileImportReader();

		fileImportReader.open(getFile());

		fileImportReader.close();
	}

	private File getFile() {
		return new File(ClassUtils.getSourceDirectory(CsvFileImportReaderTest.class)
				.getAbsolutePath() + Strings.FILE_SEPARATOR + "test.csv");
	}
}
