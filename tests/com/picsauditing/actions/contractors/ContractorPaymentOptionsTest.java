package com.picsauditing.actions.contractors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.PaymentMethod;
import com.picsauditing.util.PermissionToViewContractor;

public class ContractorPaymentOptionsTest extends PicsActionTest {
	private ContractorPaymentOptions contractorPaymentOptions;
	
	@Mock
	private ContractorAccountDAO contractorAccountDao;
	@Mock
	private AppPropertyDAO appPropDao;
	@Mock
	private ContractorAccount contractor;
	@Mock
	private PermissionToViewContractor permissionToViewContractor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupMocks();
		contractorPaymentOptions = new ContractorPaymentOptions();
		setObjectUnderTestState(contractorPaymentOptions);

		PicsTestUtil.autowireDAOsFromDeclaredMocks(contractorPaymentOptions, this);

		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(GREATER_THAN_ONE);
		when(contractorAccountDao.find(anyInt())).thenReturn(contractor);
		when(permissionToViewContractor.check(anyBoolean())).thenReturn(true);
		when(appPropDao.find("brainTree.key")).thenReturn(new AppProperty("key", "key"));
		when(appPropDao.find("brainTree.key_id")).thenReturn(new AppProperty("key_id", "key_id"));

		Whitebox.setInternalState(contractorPaymentOptions, "permissionToViewContractor", permissionToViewContractor);
	}

	@Test
	public void testChangePaymentToCheck_NoCcOnFile() throws Exception {
		when(contractor.isCcOnFile()).thenReturn(false);

		String actionResult = contractorPaymentOptions.changePaymentToCheck();

		verify(contractor).setPaymentMethod(PaymentMethod.Check);
		verify(contractorAccountDao).save(contractor);
		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
	}

	@Test
	public void testChangePaymentToCheck_CcOnFile() throws Exception {
		when(contractor.isCcOnFile()).thenReturn(true);

		String actionResult = contractorPaymentOptions.changePaymentToCheck();

		verify(contractor).setPaymentMethod(PaymentMethod.Check);
		verify(contractorAccountDao).save(contractor);
		assertThat(actionResult, is(equalTo(Action.SUCCESS)));
	}

}
