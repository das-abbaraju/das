package com.picsauditing.actions.report;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.util.ReportFilterContractor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReportContractorRiskAssessment.class, ActionContext.class, ReportFilterContractor.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ReportContractorRiskAssessmentTest {
	private ReportContractorRiskAssessment reportContractorRiskAssessment;
	private ContractorAccount contractorAccount;

	private final String DEFAULT = "DEFAULT";

	@Mock
	private ActionContext actionContext;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Permissions permissions;
	@Mock
	private ReportFilterContractor reportFilterContractor;

	@Before
	public void setUp() throws InstantiationException, IllegalAccessException {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(ActionContext.class);

		Map<String, Object> sessions = new HashMap<String, Object>();
		sessions.put("filter" + ListType.Contractor, reportFilterContractor);
		sessions.put("permissions", permissions);

		Mockito.when(ActionContext.getContext()).thenReturn(actionContext);
		Mockito.when(actionContext.getSession()).thenReturn(sessions);
		Mockito.when(permissions.hasPermission(OpPerms.RiskRank, OpType.View)).thenReturn(true);

		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultName")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultCity")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultZip")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultAmount")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultTaxID")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultPerformedBy")).toReturn(DEFAULT);
		PowerMockito.stub(PowerMockito.method(ReportFilterContractor.class, "getDefaultSelectPerformedBy")).toReturn(
				DEFAULT);

		reportContractorRiskAssessment = new ReportContractorRiskAssessment();
		contractorAccount = EntityFactory.makeContractor();
		PicsTestUtil picsTestUtil = new PicsTestUtil();

		Whitebox.setInternalState(reportContractorRiskAssessment, "con", contractorAccount);
		Whitebox.setInternalState(reportContractorRiskAssessment, "permissions", permissions);
		picsTestUtil.autowireEMInjectedDAOs(reportContractorRiskAssessment, entityManager);
	}

	@Test
	public void testAcceptWithNullType() throws Exception {
		reportContractorRiskAssessment.setType(null);
		Assert.assertEquals(Action.SUCCESS, reportContractorRiskAssessment.accept());

		Mockito.verify(entityManager.merge(contractorAccount), Mockito.never());
	}
}
