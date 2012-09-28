package com.picsauditing.actions.employees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.JobCompetency;
import com.picsauditing.jpa.entities.JobRole;
import com.picsauditing.jpa.entities.OperatorCompetency;
import com.picsauditing.search.Database;
import com.picsauditing.util.DoubleMap;

public class JobCompetencyMatrixTest {
	private JobCompetencyMatrix jobCompetencyMatrix;

	@Mock
	private Database database;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		jobCompetencyMatrix = new JobCompetencyMatrix();
		PicsTestUtil util = new PicsTestUtil();
		util.autowireEMInjectedDAOs(jobCompetencyMatrix, entityManager);

		when(entityManager.createQuery(anyString())).thenReturn(query);

		Whitebox.setInternalState(jobCompetencyMatrix, "permissions", permissions);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testExecute_Audit() throws Exception {
		ContractorAudit audit = EntityFactory.makeContractorAudit(AuditType.HSE_COMPETENCY,
				EntityFactory.makeContractor());

		jobCompetencyMatrix.setAudit(audit);

		assertEquals(PicsActionSupport.SUCCESS, jobCompetencyMatrix.execute());
		assertEquals(audit.getContractorAccount(), jobCompetencyMatrix.getAccount());
	}

	@Test
	public void testExecute_LoggedInAsContractor() throws Exception {
		ContractorAccount contractor = loggedInContractor();

		when(query.getResultList()).thenReturn(Collections.emptyList());

		assertEquals(PicsActionSupport.SUCCESS, jobCompetencyMatrix.execute());
		assertEquals(contractor, jobCompetencyMatrix.getAccount());

		assertNotNull(jobCompetencyMatrix.getRoles());
		assertNotNull(jobCompetencyMatrix.getCompetencies());
	}

	@Test
	public void testGetRolesOperatorCompetency_NewCompetency() {
		List<JobRole> roles = new ArrayList<JobRole>();
		roles.add(new JobRole());

		Whitebox.setInternalState(jobCompetencyMatrix, "roles", roles);

		assertNull(jobCompetencyMatrix.getRoles(new OperatorCompetency()));
	}

	@Test
	public void testGetRolesOperatorCompetency_ExistingCompetency() {
		JobRole role = new JobRole();
		OperatorCompetency competency = new OperatorCompetency();
		JobCompetency jobCompetency = new JobCompetency();

		jobCompetency.setJobRole(role);
		jobCompetency.setCompetency(competency);

		List<JobRole> roles = new ArrayList<JobRole>();
		roles.add(role);

		DoubleMap<JobRole, OperatorCompetency, JobCompetency> map = new DoubleMap<JobRole, OperatorCompetency, JobCompetency>();
		map.put(role, competency, jobCompetency);

		Whitebox.setInternalState(jobCompetencyMatrix, "map", map);
		Whitebox.setInternalState(jobCompetencyMatrix, "roles", roles);

		assertNotNull(jobCompetencyMatrix.getRoles(competency));
	}

	private ContractorAccount loggedInContractor() {
		ContractorAccount contractor = EntityFactory.makeContractor();

		when(entityManager.find(Account.class, contractor.getId())).thenReturn(contractor);
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractor.getId());

		return contractor;
	}
}
