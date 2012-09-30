package com.picsauditing.actions.contractors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mock.SearchEngineMockPolicy;
import com.picsauditing.strutsutil.AjaxUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAddClientSite.class, ActionContext.class, AjaxUtils.class, SmartFacilitySuggest.class,
		ServletActionContext.class })
@MockPolicy(SearchEngineMockPolicy.class)
@PowerMockIgnore({"javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*"})
public class RegistrationAddClientSiteTest extends PicsTest {
	RegistrationAddClientSite registrationAddClientSite;
	ContractorAccount contractor;

	private List<OperatorAccount> results;

	@Mock
	private Query query;
	@Mock
	private HttpServletRequest request;
	@Mock
	private Permissions permissions;
	@Mock
	private HttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		
		Map<String, Object> session = new HashMap<String, Object>();
		session.put("permissions", permissions);

		ActionContext actionContext = mock(ActionContext.class);
		PowerMockito.mockStatic(ActionContext.class);
		PowerMockito.mockStatic(AjaxUtils.class);
		PowerMockito.mockStatic(SmartFacilitySuggest.class);
		PowerMockito.mockStatic(ServletActionContext.class);

		when(actionContext.getSession()).thenReturn(session);
		when(ActionContext.getContext()).thenReturn(actionContext);
		when(request.getCookies()).thenReturn(null);
		when(ServletActionContext.getRequest()).thenReturn(request);
		when(ServletActionContext.getResponse()).thenReturn(response);

		registrationAddClientSite = PowerMockito.spy(new RegistrationAddClientSite());
		autowireEMInjectedDAOs(registrationAddClientSite);

		contractor = EntityFactory.makeContractor();

		when(permissions.isContractor()).thenReturn(true);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(permissions.getAccountStatus()).thenReturn(AccountStatus.Active);
		when(em.find(ContractorAccount.class, contractor.getId())).thenReturn(contractor);
		PowerMockito.doReturn(true).when(registrationAddClientSite, "checkPermissionToView");

		results = new ArrayList<OperatorAccount>();
		for (int i = 0; i < 3; i++) {
			results.add(EntityFactory.makeOperator());
		}
	}

	@Test
	public void testEmptySearchValue() throws Exception {
		PowerMockito.doReturn(Collections.emptyList()).when(registrationAddClientSite, "loadSearchResults");

		registrationAddClientSite.setSearchValue(null);
		registrationAddClientSite.search();

		PowerMockito.verifyPrivate(registrationAddClientSite, times(1)).invoke("loadSearchResults");
	}

	@Test
	public void testNotEmptySearchValue() throws Exception {
		List<OperatorAccount> subList = results.subList(0, 1);

		when(em.createNativeQuery(anyString(), eq(OperatorAccount.class))).thenReturn(query);
		when(query.getResultList()).thenReturn(subList);

		registrationAddClientSite.setSearchValue("Hello World");
		registrationAddClientSite.search();

		PowerMockito.verifyPrivate(registrationAddClientSite, never()).invoke("loadSearchResults");

		assertEquals(subList, registrationAddClientSite.getSearchResults());
	}

	@Test
	public void testSearchForOperators() throws Exception {
		// for testing not empty search value
		when(em.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(results);

		registrationAddClientSite.setSearchValue("*");
		registrationAddClientSite.search();

		assertEquals(results, registrationAddClientSite.getSearchResults());
	}

	@Test
	public void testReturnAjax() throws Exception {
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(true);
		PowerMockito.doReturn(Collections.emptyList()).when(registrationAddClientSite, "loadSearchResults");
		assertEquals("ClientSiteList", registrationAddClientSite.search());
	}

	@Test
	public void testReturnSuccess() throws Exception {
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(false);
		PowerMockito.doReturn(Collections.emptyList()).when(registrationAddClientSite, "loadSearchResults");
		assertEquals(ActionSupport.SUCCESS, registrationAddClientSite.search());
	}

	@Test
	public void testRemoveExistingOperatorsFromSearch() throws Exception {
		List<OperatorAccount> searchResults = results.subList(0, 1);

		for (OperatorAccount operator : results) {
			EntityFactory.addContractorOperator(contractor, operator);
		}

		when(em.createNativeQuery(anyString(), eq(OperatorAccount.class))).thenReturn(query);
		when(query.getResultList()).thenReturn(searchResults);

		registrationAddClientSite.setSearchValue("Hello World");
		registrationAddClientSite.search();

		// registrationAddClientSite.getSearchResults() this calls loadSearchResults
		List<OperatorAccount> results = Whitebox.getInternalState(registrationAddClientSite, "searchResults"); 
		
		assertEquals(new ArrayList<OperatorAccount>(), results);
		PowerMockito.verifyPrivate(registrationAddClientSite, never()).invoke("loadSearchResults");
	}
}