package com.picsauditing.employeeguard.controllers.importexport;

import com.picsauditing.PicsActionTest;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.search.Database;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ImportExportActionSupportTest extends PicsActionTest {
	public static final String INVALID = "invalid";
	private ImportExportActionSupport importExportActionSupport;

	@Mock
	private Database database;
	@Mock
	private File file;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupMocks();
		actionContext.put(ServletActionContext.ACTION_MAPPING, new HashMap<>());

		importExportActionSupport = new ImportExportActionSupport() {
			@Override
			protected void processUpload() {
			}

			@Override
			public String download() throws Exception {
				return null;
			}

			@Override
			protected String invalidUploadRedirect() {
				return INVALID;
			}

			@Override
			protected String successfulUploadRedirect() {
				return PicsRestActionSupport.SUCCESS;
			}
		};
	}

	@Test
	public void testProcessUpload_NullOrEmptyFile() throws Exception {
		assertEquals(INVALID, importExportActionSupport.upload());
		assertTrue(importExportActionSupport.hasActionErrors());
	}

	@Test
	public void testProcessUpload_FileIsNotCSV() throws Exception {
		when(file.length()).thenReturn(1l);

		importExportActionSupport.setUpload(file);
		importExportActionSupport.setUploadFileName("Test.other");

		assertEquals(INVALID, importExportActionSupport.upload());
		assertTrue(importExportActionSupport.hasActionErrors());
	}

	@Test
	public void testProcessUpload_FileIsValid() throws Exception {
		when(file.length()).thenReturn(1l);

		importExportActionSupport.setUpload(file);
		importExportActionSupport.setUploadFileName("Test.csv");

		assertEquals(PicsRestActionSupport.SUCCESS, importExportActionSupport.upload());
		assertFalse(importExportActionSupport.hasActionErrors());
	}
}
