package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
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
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*",
		"org.apache.xerces.*" })
public class ContractorDashboardTest {

	ContractorDashboard dashboard;
	ContractorAccount contractor;
	OperatorAccount operator;
	ContractorOperator conOp;
	Permissions permissions;

	@Mock
	ContractorOperatorDAO contractorOperatorDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dashboard = new ContractorDashboard();

		contractor = EntityFactory.makeContractor();
		operator = EntityFactory.makeOperator();
		conOp = EntityFactory.addContractorOperator(contractor, operator);
		permissions = EntityFactory.makePermission();

	}

	@Test
	public void testFindCorporateOperators() throws Exception {
		Whitebox.setInternalState(dashboard, "contractorOperatorDAO",
				contractorOperatorDAO);
		Whitebox.setInternalState(dashboard, "permissions", permissions);
		Whitebox.setInternalState(dashboard, "contractor", contractor);

		long time = (new Date()).getTime();
		List<ContractorOperator> operators = new ArrayList<ContractorOperator>();
		operators.add(conOp);
		operators.add(EntityFactory.addContractorOperator(contractor,
				EntityFactory.makeOperator()));
		when(
				contractorOperatorDAO.findByContractor(Matchers.anyInt(),
						Matchers.any(Permissions.class))).thenReturn(operators);

		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // no co selected

		Whitebox.setInternalState(dashboard, "co", conOp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // no forced flags

		ContractorOperator flaggedOperator = EntityFactory
				.addContractorOperator(contractor, EntityFactory.makeOperator());
		flaggedOperator.setForceFlag(FlagColor.Green);
		flaggedOperator.setForceBegin(new Date(time - 24 * 60 * 60 * 1000L));
		flaggedOperator.setForceEnd(new Date(time + 24 * 60 * 60 * 1000L));
		operators.add(flaggedOperator);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNotNull(dashboard.getCorporateFlagOverride()); // parent has force
																// flag

		conOp.setForceFlag(FlagColor.Green);
		conOp.setForceBegin(new Date(time - 24 * 60 * 60 * 1000L));
		conOp.setForceEnd(new Date(time + 24 * 60 * 60 * 1000L));
		Whitebox.setInternalState(dashboard, "co", conOp);
		Whitebox.invokeMethod(dashboard, "findCorporateOverride");
		assertNull(dashboard.getCorporateFlagOverride()); // co has force flag
	}
}
