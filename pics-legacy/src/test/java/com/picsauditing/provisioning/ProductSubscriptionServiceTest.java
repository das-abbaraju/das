package com.picsauditing.provisioning;

import com.picsauditing.access.Permissions;
import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.employeeguard.entities.AccountEmployeeGuard;
import com.picsauditing.web.SessionInfoProvider;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductSubscriptionServiceTest {

	private ProductSubscriptionService productSubscriptionService;
	private AccountEmployeeGuard operatorAccountEmployeeGuard;
	private AccountEmployeeGuard contractorAccountEmployeeGuard;
	private static final Integer operatorId = 100;
	private static final Integer contractorId = 200;

	@Mock
	private Permissions permissions;

	@Mock
	private AccountEmployeeGuardDAO accountEmployeeGuardDAO;

	@Mock
	private CacheManager cacheManager;

	@Mock
	SessionInfoProvider sessionInfoProvider;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		productSubscriptionService = new ProductSubscriptionServiceImpl();
		CacheManager.getInstance().getCache(ProductSubscriptionService.CACHE_NAME).removeAll();
		operatorAccountEmployeeGuard = new AccountEmployeeGuard(operatorId);
		contractorAccountEmployeeGuard = new AccountEmployeeGuard(contractorId);
		Whitebox.setInternalState(productSubscriptionService, "accountEmployeeGuardDAO", accountEmployeeGuardDAO);
	}

	private Boolean hasEmployeeGuard_OperatorHasEG() throws Exception {
		when(permissions.isContractor()).thenReturn(false);
		when(permissions.isOperatorCorporate()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(operatorId);
		when(accountEmployeeGuardDAO.find(operatorAccountEmployeeGuard.getAccountId())).thenReturn(operatorAccountEmployeeGuard);
		when(permissions.getAccountId()).thenReturn(operatorAccountEmployeeGuard.getAccountId());
		return productSubscriptionService.hasEmployeeGUARD(permissions);

	}

	private Boolean hasEmployeeGuard_ContratorHasEG() throws Exception {
		when(permissions.isContractor()).thenReturn(true);
		when(permissions.isOperatorCorporate()).thenReturn(false);
		when(permissions.getAccountId()).thenReturn(contractorAccountEmployeeGuard.getAccountId());
		when(accountEmployeeGuardDAO.find(contractorAccountEmployeeGuard.getAccountId())).thenReturn(contractorAccountEmployeeGuard);
		when(permissions.getAccountId()).thenReturn(contractorAccountEmployeeGuard.getAccountId());
		return productSubscriptionService.hasEmployeeGUARD(permissions);
	}

	@Test
	public void testHasEmployeeGuard_OperatorHasEG_PullFromDB() throws Exception {

		boolean status = hasEmployeeGuard_OperatorHasEG();

		assertTrue("Expected Operator to have EmployeeGuard", status);
	}

	@Test
	public void testHasEmployeeGuard_OperatorHasEG_PullFromCache() throws Exception {
		hasEmployeeGuard_OperatorHasEG();
		hasEmployeeGuard_OperatorHasEG();

		Boolean status = Whitebox.invokeMethod(productSubscriptionService, "findFromCache", operatorId);
		assertTrue("Expected findFromCache to return true", status);

	}

	@Test
	public void testHasEmployeeGuard_ContractorHasEG_PullFromDB() throws Exception {
		boolean status = hasEmployeeGuard_ContratorHasEG();

		assertTrue("Expected Contrator to have EmployeeGuard", status);

	}

	@Test
	public void testHasEmployeeGuard_ContractorHasEG_PullFromCache() throws Exception {
		hasEmployeeGuard_ContratorHasEG();
		hasEmployeeGuard_ContratorHasEG();

		Boolean status = Whitebox.invokeMethod(productSubscriptionService, "findFromCache", contractorId);
		assertTrue("Expected findFromCache to return true", status);

	}

	@Test
	public void testEmployeeGuardRemoved() throws Exception {
		hasEmployeeGuard_OperatorHasEG();

		when(accountEmployeeGuardDAO.find(operatorAccountEmployeeGuard.getAccountId())).thenReturn(operatorAccountEmployeeGuard);

		productSubscriptionService.removeEmployeeGUARD(operatorAccountEmployeeGuard.getAccountId());

		Boolean status = Whitebox.invokeMethod(productSubscriptionService, "findFromCache", operatorAccountEmployeeGuard.getAccountId());
		assertNull("Expected findFromCache to return null", null);
		verify(accountEmployeeGuardDAO).remove(any(AccountEmployeeGuard.class));
	}

	@Test
	public void testAddEmployeeGuard() throws Exception {

		hasEmployeeGuard_OperatorHasEG();

		productSubscriptionService.addEmployeeGUARD(operatorAccountEmployeeGuard.getAccountId());

		Boolean status = Whitebox.invokeMethod(productSubscriptionService, "findFromCache", operatorAccountEmployeeGuard.getAccountId());
		assertNull("Expected findFromCache to return null", null);
	}

}
