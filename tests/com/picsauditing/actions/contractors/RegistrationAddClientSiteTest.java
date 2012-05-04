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
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.mock.SearchEngineMockPolicy;;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAddClientSite.class, ActionContext.class, AjaxUtils.class })
@MockPolicy(SearchEngineMockPolicy.class)
public class RegistrationAddClientSiteTest extends PicsTest {
	RegistrationAddClientSite registrationAddClientSite;
	ContractorAccount contractor;

	List<OperatorAccount> results;

	@Mock
	Query query;
	@Mock
	HttpServletRequest request;
	@Mock
	Permissions permissions;

	@Before
	public void setUp() throws Exception {
		Map<String, Object> session = new HashMap<String, Object>();
		session.put("permissions", permissions);

		ActionContext actionContext = mock(ActionContext.class);
		when(actionContext.getSession()).thenReturn(session);

		PowerMockito.mockStatic(ActionContext.class);
		when(ActionContext.getContext()).thenReturn(actionContext);

		PowerMockito.mockStatic(AjaxUtils.class);

		registrationAddClientSite = PowerMockito.spy(new RegistrationAddClientSite());
		autowireEMInjectedDAOs(registrationAddClientSite);

		contractor = EntityFactory.makeContractor();
		results = new ArrayList<OperatorAccount>();

		when(em.find(ContractorAccount.class, contractor.getId())).thenReturn(contractor);
		when(ServletActionContext.getRequest()).thenReturn(request);
		when(permissions.getAccountId()).thenReturn(contractor.getId());
		when(permissions.isContractor()).thenReturn(true);
		PowerMockito.doReturn(true).when(registrationAddClientSite, "checkPermissionToView");
		// when(registrationAddClientSite.checkPermissionToView()).thenReturn(true);

		for (int i = 0; i < 3; i++) {
			results.add(EntityFactory.makeOperator());
		}
	}

	@Test
	public void testEmptySearchValue() throws Exception {
		PowerMockito.doReturn(Collections.emptyList()).when(registrationAddClientSite, "loadSearchResults");
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(false);

		registrationAddClientSite.setSearchValue(null);

		registrationAddClientSite.search();

		PowerMockito.verifyPrivate(registrationAddClientSite, times(1)).invoke("loadSearchResults");
	}

	@Test
	public void testNotEmptySearchValue() throws Exception {
		registrationAddClientSite.setSearchValue("Hello World");

		registrationAddClientSite.search();

		PowerMockito.verifyPrivate(registrationAddClientSite, never()).invoke("loadSearchResults");
	}

	@Test
	public void testSearchForOperators() throws Exception {
		// for testing not empty search value
		registrationAddClientSite.setSearchValue("*");

		when(em.createQuery(anyString())).thenReturn(query);
		when(query.getResultList()).thenReturn(results);

		registrationAddClientSite.search();

		assertEquals(results, registrationAddClientSite.getSearchResults());
	}

	@Test
	public void testReturnAjax() throws Exception {
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(true);

		assertEquals("ClientSiteList", registrationAddClientSite.search());
	}

	@Test
	public void testReturnSuccess() throws Exception {
		PowerMockito.when(AjaxUtils.isAjax(request)).thenReturn(false);

		assertEquals(ActionSupport.SUCCESS, registrationAddClientSite.search());
	}
}
