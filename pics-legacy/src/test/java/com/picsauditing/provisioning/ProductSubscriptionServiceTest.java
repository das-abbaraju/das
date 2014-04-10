package com.picsauditing.provisioning;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import net.sf.ehcache.CacheManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;
/*
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
*/

public class ProductSubscriptionServiceTest  extends PicsTest {
  private ProductSubscriptionService productSubscriptionService;
  private ContractorAccount contractor;
  private OperatorAccount operator;
  private static final Integer operatorId=100;
  private static final Integer contractorId=200;

  @Mock
  private Permissions permissions;

  @Mock
  protected ContractorAccountDAO contractorAccountDAO;

  @Mock
  private OperatorAccountDAO operatorAccountDAO;

  @Mock
  private CacheManager cacheManager;


  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    super.setUp();
    productSubscriptionService = new ProductSubscriptionServiceImpl();
    autowireDAOsFromDeclaredMocks(productSubscriptionService, this);


    CacheManager.getInstance().getCache(ProductSubscriptionService.CACHE_NAME).removeAll();

  }

  private Boolean hasEmployeeGuardLegacy_OperatorHasEG() throws Exception{
    operator = EntityFactory.makeOperator();
    operator.setId(operatorId);
    operator.setRequiresEmployeeGuard(true);
    when(permissions.isContractor()).thenReturn(false);
    when(permissions.isOperatorCorporate()).thenReturn(true);
    when(operatorAccountDAO.find(operator.getId())).thenReturn(operator);
    when(permissions.getAccountId()).thenReturn(operator.getId());

    return productSubscriptionService.hasEmployeeGuardLegacy(permissions);

  }

  private Boolean hasEmployeeGuardLegacy_ContratorHasEG() throws Exception{
    contractor = EntityFactory.makeContractor();
    contractor.setId(contractorId);
    contractor.setHasEmployeeGuard(true);
    when(permissions.isContractor()).thenReturn(true);
    when(permissions.isOperatorCorporate()).thenReturn(false);
    when(contractorAccountDAO.find(contractor.getId())).thenReturn(contractor);
    when(permissions.getAccountId()).thenReturn(contractor.getId());
    return productSubscriptionService.hasEmployeeGuardLegacy(permissions);

  }


  @Test
  public void testHasEmployeeGuardLegacy_OperatorHasEG_PullFromDB() throws Exception {

    boolean status=hasEmployeeGuardLegacy_OperatorHasEG();

    assertTrue("Expected Operator to have EmployeeGuard", status);
  }

  @Test
  public void testHasEmployeeGuardLegacy_OperatorHasEG_PullFromCache() throws Exception {
    hasEmployeeGuardLegacy_OperatorHasEG();
    hasEmployeeGuardLegacy_OperatorHasEG();

    Boolean status=Whitebox.invokeMethod(productSubscriptionService, "findFromCacheLegacy", operator.getId());
    assertTrue("Expected findFromCacheLegacy to return true", status);

  }

  @Test
  public void testHasEmployeeGuardLegacy_ContractorHasEG_PullFromDB() throws Exception {
    boolean status=hasEmployeeGuardLegacy_ContratorHasEG();

    assertTrue("Expected Contrator to have EmployeeGuard", status);

  }

  @Test
  public void testHasEmployeeGuardLegacy_ContractorHasEG_PullFromCache() throws Exception {
    hasEmployeeGuardLegacy_ContratorHasEG();
    hasEmployeeGuardLegacy_ContratorHasEG();

    Boolean status=Whitebox.invokeMethod(productSubscriptionService, "findFromCacheLegacy", contractor.getId());
    assertTrue("Expected findFromCacheLegacy to return true", status);

  }

  @Test
  public void testEmployeeGuardRemovedLegacy() throws Exception {
    hasEmployeeGuardLegacy_OperatorHasEG();

    //-- Currently we dirty the cache only.
    productSubscriptionService.employeeGuardRemovedLegacy(operator.getId());

    Boolean status=Whitebox.invokeMethod(productSubscriptionService, "findFromCacheLegacy", operator.getId());
    assertNull("Expected findFromCacheLegacy to return null", null);

  }

  @Test
  public void testEmployeeGuardAcquiredLegacy() throws Exception {

  }


}
