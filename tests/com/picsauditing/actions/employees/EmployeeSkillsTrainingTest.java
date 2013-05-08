package com.picsauditing.actions.employees;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.EmployeeCompetency;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.jpa.entities.OperatorCompetencyEmployeeFile;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.search.Database;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class EmployeeSkillsTrainingTest {
	private EmployeeSkillsTraining employeeSkillsTraining;

	@Mock
	private Database databaseForTesting;
	@Mock
	private Employee employee;
	@Mock
	private EmployeeCompetency employeeCompetency;
	@Mock
	private OperatorCompetency operatorCompetency;
	@Mock
	private OperatorCompetencyEmployeeFile employeeFile;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		employeeSkillsTraining = new EmployeeSkillsTraining();
		employeeSkillsTraining.setEmployee(employee);

		Whitebox.setInternalState(employeeSkillsTraining, "permissions", permissions);

		when(permissions.isContractor()).thenReturn(true);
		when(permissions.has(OpPerms.ContractorSafety)).thenReturn(true);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test(expected = RecordNotFoundException.class)
	public void testExecute_NullEmployee() throws Exception {
		employeeSkillsTraining.setEmployee(null);
		employeeSkillsTraining.execute();
	}

	@Test
	public void testExecute_EmployeeProvided() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, employeeSkillsTraining.execute());
	}

	@Test
	public void testIsCanAccessDocumentation_ContractorNotSafety() {
		when(permissions.has(OpPerms.ContractorSafety)).thenReturn(false);
		assertFalse(employeeSkillsTraining.isCanAccessDocumentation());
	}

	@Test
	public void testIsCanAccessDocumentation_OperatorUploadDocumentation() {
		when(permissions.has(OpPerms.ContractorSafety)).thenReturn(false);
		when(permissions.has(OpPerms.UploadEmployeeDocumentation)).thenReturn(true);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertTrue(employeeSkillsTraining.isCanAccessDocumentation());
	}

	@Test
	public void testIsCanAccessDocumentation_OperatorNoUploadDocumentation() {
		when(permissions.has(OpPerms.ContractorSafety)).thenReturn(false);
		when(permissions.has(OpPerms.UploadEmployeeDocumentation)).thenReturn(false);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		assertFalse(employeeSkillsTraining.isCanAccessDocumentation());
	}

	@Test
	public void testIsCanAccessDocumentation_PicsEmployee() {
		when(permissions.has(OpPerms.ContractorSafety)).thenReturn(false);
		when(permissions.has(OpPerms.UploadEmployeeDocumentation)).thenReturn(false);
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperatorCorporate()).thenReturn(false);
		when(permissions.isPicsEmployee()).thenReturn(true);
		assertTrue(employeeSkillsTraining.isCanAccessDocumentation());
	}

	@Test
	public void testGetCompetenciesMissingDocumentation_NoEmployeeCompetenciesMeansNoneMissing() throws Exception {
		when(employee.getEmployeeCompetencies()).thenReturn(Collections.<EmployeeCompetency>emptyList());

		List<OperatorCompetency> competenciesMissingDocumentation = employeeSkillsTraining.getCompetenciesMissingDocumentation();
		assertNotNull(competenciesMissingDocumentation);
		assertTrue(competenciesMissingDocumentation.isEmpty());
	}

	@Test
	public void testGetCompetenciesMissingDocumentation_AllCompetenciesHaveDocumentation() throws Exception {
		List<EmployeeCompetency> competencies = new ArrayList<>();
		competencies.add(employeeCompetency);

		when(employee.getEmployeeCompetencies()).thenReturn(competencies);
		when(employeeCompetency.isMissingDocumentation()).thenReturn(false);

		List<OperatorCompetency> competenciesMissingDocumentation = employeeSkillsTraining.getCompetenciesMissingDocumentation();
		assertNotNull(competenciesMissingDocumentation);
		assertTrue(competenciesMissingDocumentation.isEmpty());
	}

	@Test
	public void testGetCompetenciesMissingDocumentation_NoCompetenciesHaveDocumentation() throws Exception {
		List<EmployeeCompetency> competencies = new ArrayList<>();
		competencies.add(employeeCompetency);

		when(employee.getEmployeeCompetencies()).thenReturn(competencies);
		when(employeeCompetency.getCompetency()).thenReturn(operatorCompetency);
		when(employeeCompetency.isMissingDocumentation()).thenReturn(true);

		List<OperatorCompetency> competenciesMissingDocumentation = employeeSkillsTraining.getCompetenciesMissingDocumentation();
		assertNotNull(competenciesMissingDocumentation);
		assertFalse(competenciesMissingDocumentation.isEmpty());
		assertEquals(operatorCompetency, competenciesMissingDocumentation.get(0));
	}

	@Test
	public void testGetFilesByStatus_NoCompetencyFilesEmptyMap() throws Exception {
		when(employee.getCompetencyFiles()).thenReturn(Collections.<OperatorCompetencyEmployeeFile>emptyList());

		Map<String, List<OperatorCompetencyEmployeeFile>> map = employeeSkillsTraining.getFilesByStatus();
		assertNotNull(map);
		assertTrue(map.isEmpty());
	}

	@Test
	public void testGetFilesByStatus_FileIsCurrentAndContainedInCurrent() throws Exception {
		List<OperatorCompetencyEmployeeFile> files = new ArrayList<>();
		files.add(employeeFile);

		when(employee.getCompetencyFiles()).thenReturn(files);
		when(employeeFile.isExpired()).thenReturn(false);

		Map<String, List<OperatorCompetencyEmployeeFile>> map = employeeSkillsTraining.getFilesByStatus();
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertTrue(map.keySet().contains(EmployeeSkillsTraining.CURRENT));
		assertFalse(map.keySet().contains(EmployeeSkillsTraining.EXPIRED));

		for (String key : map.keySet()) {
			assertEquals(employeeFile, map.get(key).get(0));
		}
	}

	@Test
	public void testGetFilesByStatus_FileIsNotCurrentAndContainedInExpired() throws Exception {
		List<OperatorCompetencyEmployeeFile> files = new ArrayList<>();
		files.add(employeeFile);

		when(employee.getCompetencyFiles()).thenReturn(files);
		when(employeeFile.isExpired()).thenReturn(true);

		Map<String, List<OperatorCompetencyEmployeeFile>> map = employeeSkillsTraining.getFilesByStatus();
		assertNotNull(map);
		assertFalse(map.isEmpty());
		assertFalse(map.keySet().contains(EmployeeSkillsTraining.CURRENT));
		assertTrue(map.keySet().contains(EmployeeSkillsTraining.EXPIRED));

		for (String key : map.keySet()) {
			assertEquals(employeeFile, map.get(key).get(0));
		}
	}

	@Test
	public void testDownload_WithoutEmployeeFile() throws Exception {
		assertEquals(PicsActionSupport.SUCCESS, employeeSkillsTraining.download());
		assertTrue(employeeSkillsTraining.hasActionErrors());
		assertNull(employeeSkillsTraining.getFileContainer());
	}

	@Test
	public void testDownload_WithEmployeeFile() throws Exception {
		when(employeeFile.getFileName()).thenReturn("filename.file");
		when(employeeFile.getFileContent()).thenReturn("Hello World".getBytes());

		employeeSkillsTraining.setEmployeeFile(employeeFile);
		assertEquals(PicsActionSupport.FILE_DOWNLOAD, employeeSkillsTraining.download());
		assertFalse(employeeSkillsTraining.hasActionErrors());
		assertNotNull(employeeSkillsTraining.getFileContainer());
	}

	@Test
	public void testDownload_WithInvalidEmployeeFile() throws Exception {
		employeeSkillsTraining.setEmployeeFile(employeeFile);
		assertEquals(PicsActionSupport.SUCCESS, employeeSkillsTraining.download());
		assertTrue(employeeSkillsTraining.hasActionErrors());
		assertNull(employeeSkillsTraining.getFileContainer());
	}
}