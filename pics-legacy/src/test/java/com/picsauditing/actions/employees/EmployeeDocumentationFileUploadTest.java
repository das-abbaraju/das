package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.OperatorCompetencyEmployeeFile;
import com.picsauditing.util.URLUtils;

public class EmployeeDocumentationFileUploadTest extends PicsTranslationTest {

	private EmployeeDocumentationFileUpload employeeDocumentationFileUpload;

	@Mock
	private Date expiration;
	@Mock
	private Employee employee;
	@Mock
	private EmployeeDAO employeeDAO;
	@Mock
	private OperatorCompetency operatorCompetency;
	@Mock
	private URLUtils urlUtils;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		employeeDocumentationFileUpload = new EmployeeDocumentationFileUpload();
		employeeDocumentationFileUpload.setEmployee(employee);
		employeeDocumentationFileUpload.setCompetency(operatorCompetency);
		employeeDocumentationFileUpload.setFileFileName("filename.file");
		employeeDocumentationFileUpload.setExpiration(expiration);

		Whitebox.setInternalState(employeeDocumentationFileUpload, "employeeDAO", employeeDAO);
		Whitebox.setInternalState(employeeDocumentationFileUpload, "urlUtils", urlUtils);
	}

	@Test
	public void testExecute_MissingEmployee() throws Exception {
		employeeDocumentationFileUpload.setEmployee(null);

		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.execute());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
	}

	@Test
	public void testExecute_MissingCompetency() throws Exception {
		employeeDocumentationFileUpload.setCompetency(null);

		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.execute());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
	}

	@Test
	public void testExecute_HasEmployeeAndCompetency() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.execute());
		assertFalse(employeeDocumentationFileUpload.hasActionErrors());
	}

	@Test
	public void testSave_MissingEmployee() throws Exception {
		employeeDocumentationFileUpload.setEmployee(null);
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.save());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
		assertFalse(employeeDocumentationFileUpload.hasActionMessages());
	}

	@Test
	public void testSave_MissingCompetency() throws Exception {
		employeeDocumentationFileUpload.setCompetency(null);
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.save());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
		assertFalse(employeeDocumentationFileUpload.hasActionMessages());
	}

	@Test
	public void testSave_MissingOrEmptyFilename() throws Exception {
		employeeDocumentationFileUpload.setFileFileName(null);
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.save());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
		assertFalse(employeeDocumentationFileUpload.hasActionMessages());

		employeeDocumentationFileUpload.setFileFileName("");
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.save());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
		assertFalse(employeeDocumentationFileUpload.hasActionMessages());
	}

	@Test
	public void testSave_MissingExpiration() throws Exception {
		employeeDocumentationFileUpload.setExpiration(null);
		assertEquals(PicsActionSupport.SUCCESS, employeeDocumentationFileUpload.save());
		assertTrue(employeeDocumentationFileUpload.hasActionErrors());
		assertFalse(employeeDocumentationFileUpload.hasActionMessages());
	}

	@Test
	public void testSave_Happy() throws Exception {
		ArgumentCaptor<OperatorCompetencyEmployeeFile> fileCaptor = ArgumentCaptor
				.forClass(OperatorCompetencyEmployeeFile.class);
		List<OperatorCompetencyEmployeeFile> files = new ArrayList<>();
		when(employee.getCompetencyFiles()).thenReturn(files);
		when(urlUtils.getActionUrl(anyString(), anyString(), anyObject())).thenReturn("/EmployeeSkillsTraining");

		File file = new File("tmp");
		FileUtils.writeByteArrayToFile(file, "Hello World".getBytes());

		employeeDocumentationFileUpload.setFile(file);
		assertEquals(PicsActionSupport.REDIRECT, employeeDocumentationFileUpload.save());
		verify(employeeDAO).save(fileCaptor.capture());

		assertFalse(employeeDocumentationFileUpload.hasActionErrors());
		assertTrue(employeeDocumentationFileUpload.hasActionMessages());

		assertEquals(employee, fileCaptor.getValue().getEmployee());
		assertEquals(operatorCompetency, fileCaptor.getValue().getCompetency());
		assertEquals("filename.file", fileCaptor.getValue().getFileName());
		assertEquals("FILE", fileCaptor.getValue().getFileType());
		assertFalse(files.isEmpty());

		file.delete();
	}
}
