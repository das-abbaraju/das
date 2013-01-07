package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;

public class ContractorDashboardTest {
	// NOTE: when you need Struts/Spring stuff do not use PowerMockRunner.
	// Instead, extend PicsActionTest and take a look at any of its other
	// subclasses (see me for help - Galen)

	private ContractorDashboard dashboard;
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private OperatorAccount corporate;
	private ContractorOperator conOp;
	private ContractorOperator conCorp;
	private Permissions permissions;

	@Mock
	private ContractorOperatorDAO contractorOperatorDAO;
	@Mock
	private Database databaseForTesting;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		dashboard = new ContractorDashboard();

		contractor = EntityFactory.makeContractor();

		corporate = EntityFactory.makeOperator();
		conCorp = EntityFactory.addContractorOperator(contractor, corporate);

		operator = EntityFactory.makeOperator();
		conOp = EntityFactory.addContractorOperator(contractor, operator);
		ArrayList<Facility> corporateFacilities = new ArrayList<Facility>();
		corporateFacilities.add(EntityFactory.makeFacility(operator, corporate));
		operator.setCorporateFacilities(corporateFacilities);

		permissions = EntityFactory.makePermission();
	}

	@Test
	public void testFindCorporateOperators() throws Exception {
		Whitebox.setInternalState(dashboard, "contractorOperatorDAO", contractorOperatorDAO);
		Whitebox.setInternalState(dashboard, "permissions", permissions);
		Whitebox.setInternalState(dashboard, "contractor", contractor);

		long time = (new Date()).getTime();
		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(conOp);
		OperatorAccount irrelevantOperator = EntityFactory.makeOperator();
		ContractorOperator irrelevantConOp = EntityFactory.addContractorOperator(contractor, irrelevantOperator);
		operators.add(irrelevantConOp);
		when(contractorOperatorDAO.findByContractor(Matchers.anyInt(), Matchers.any(Permissions.class))).thenReturn(
				operators);

		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // no co selected

		Whitebox.setInternalState(dashboard, "co", conOp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // no forced flags

		conOp.setForceFlag(FlagColor.Green);
		conOp.setForceBegin(new Date(time - 24 * 60 * 60 * 1000L));
		conOp.setForceEnd(new Date(time + 24 * 60 * 60 * 1000L));
		Whitebox.setInternalState(dashboard, "co", conOp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // co has force flag

		conOp.setForceFlag(null);
		conOp.setForceBegin(null);
		conOp.setForceEnd(null);
		irrelevantConOp.setForceFlag(FlagColor.Green);
		irrelevantConOp.setForceBegin(new Date(time - 24 * 60 * 60 * 1000L));
		irrelevantConOp.setForceEnd(new Date(time + 24 * 60 * 60 * 1000L));
		Whitebox.setInternalState(dashboard, "co", conOp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // irrelevant co has
															// force flag

		irrelevantConOp.setForceFlag(null);
		irrelevantConOp.setForceBegin(null);
		irrelevantConOp.setForceEnd(null);
		conCorp.setForceFlag(FlagColor.Green);
		conCorp.setForceBegin(new Date(time - 24 * 60 * 60 * 1000L));
		conCorp.setForceEnd(new Date(time + 24 * 60 * 60 * 1000L));
		operators.add(conCorp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNotNull(dashboard.getCorporateFlagOverride()); // parent has force
																// flag
	}

	@Test
	public void testGetIncompleteAnnualUpdates() throws Exception {
		Calendar date = Calendar.getInstance();

		String year1 = "" + date.get(Calendar.YEAR);
		date.add(Calendar.YEAR, -1);
		String year2 = "" + date.get(Calendar.YEAR);
		contractor.getAudits().add(createAU(year1, AuditStatus.Complete));
		contractor.getAudits().add(createAU(year2, AuditStatus.Pending));
		
		
		String results = Whitebox.invokeMethod(dashboard, "getIncompleteAnnualUpdates" , contractor, operator.getId());
		assertTrue(results.contains(year2));
		assertTrue(!results.contains(year1));
	}
	
	private ContractorAudit createAU(String year, AuditStatus status) {
		ContractorAudit audit;
		audit = EntityFactory.makeAnnualUpdate(AuditType.ANNUALADDENDUM, contractor, year);
		Calendar date = Calendar.getInstance();
		date.add(Calendar.YEAR, 3);
		audit.setExpiresDate(date.getTime());

		ContractorAuditOperator cao;
		cao = EntityFactory.addCao(audit, operator);
		cao.setStatus(status);
		ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
		caop.setOperator(operator);
		cao.getCaoPermissions().add(caop);
		return audit;
	}
}
