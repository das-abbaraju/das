package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("deprecation")
public class DataConversionRequestAccountTest extends PicsTranslationTest {

	private DataConversionRequestAccount requestConversion;

	private PicsTestUtil picsTestUtil;

	@Mock
	private EntityManager entityManager;
	@Mock
	private OperatorAccount operator;
	@Mock
	private Permissions permissions;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


		requestConversion = new DataConversionRequestAccount();
		picsTestUtil = new PicsTestUtil();

		picsTestUtil.autowireEMInjectedDAOs(requestConversion, entityManager);

		Whitebox.setInternalState(requestConversion, "permissions", permissions);

		when(entityManager.createQuery(anyString())).thenReturn(query);
	}

	@Test
	public void testExecute() {
		when(query.getResultList()).thenReturn(Collections.emptyList());

		assertEquals(PicsActionSupport.SUCCESS, requestConversion.execute());

		verify(entityManager).createQuery(anyString());
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
		verify(query).getResultList();
	}

	@Test
	public void testNeedsUpgrade_NoResults() throws Exception {
		assertFalse((Boolean) Whitebox.invokeMethod(requestConversion, "needsUpgrade"));
	}

	@Test
	public void testNeedsUpgrade_HasResults() throws Exception {
		List<ContractorRegistrationRequest> needsConversion = new ArrayList<ContractorRegistrationRequest>();
		needsConversion.add(new ContractorRegistrationRequest());

		Whitebox.setInternalState(requestConversion, "requestsNeedingConversion", needsConversion);

		assertTrue((Boolean) Whitebox.invokeMethod(requestConversion, "needsUpgrade"));
	}

	@Test
	public void testNeedsUpgrade_ForOneOperator() throws Exception {
		ContractorRegistrationRequest request = new ContractorRegistrationRequest();

		List<ContractorRegistrationRequest> needsConversion = new ArrayList<ContractorRegistrationRequest>();
		needsConversion.add(request);

		when(operator.getId()).thenReturn(1);
		when(query.getResultList()).thenReturn(needsConversion);

		Whitebox.setInternalState(requestConversion, "restrictToOperator", operator);

		needsConversion = Whitebox.invokeMethod(requestConversion, "findRequestsNeedingConversion");

		Whitebox.setInternalState(requestConversion, "requestsNeedingConversion", needsConversion);

		assertTrue((Boolean) Whitebox.invokeMethod(requestConversion, "needsUpgrade"));

		verify(operator, times(2)).getId();
	}
}
