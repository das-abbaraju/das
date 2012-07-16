package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorDashboardTest {

	ContractorDashboard dashboard;
	ContractorAccount contractor;
	OperatorAccount operator;
	OperatorAccount corporate;
	ContractorOperator conOp;
	ContractorOperator conCorp;
	Permissions permissions;

	@Mock
	ContractorOperatorDAO contractorOperatorDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

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
}
