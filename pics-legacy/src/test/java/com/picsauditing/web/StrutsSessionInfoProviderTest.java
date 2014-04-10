package com.picsauditing.web;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class StrutsSessionInfoProviderTest {

	private SessionInfoProvider sessionInfoProvider;

	@Mock
	private ActionMapping actionMapping;
	@Mock
	private Permissions permissions;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		setupActionContext();

		sessionInfoProvider = new StrutsSessionInfoProvider();
	}

	private void setupActionContext() {
		ActionContext.setContext(new ActionContext(new HashMap<String, Object>()));
		ActionContext.getContext().put(ActionContext.SESSION, new HashMap<String, Object>());
		ActionContext.getContext().getSession().put(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);
		ActionContext.getContext().setParameters(new HashMap<String, Object>() {{
			put("id", "6");
		}});
		ActionContext.getContext().put(ServletActionContext.ACTION_MAPPING, actionMapping);
	}

	@Test
	public void testGetUserId() {
		when(permissions.getUserId()).thenReturn(678);

		int result = sessionInfoProvider.getUserId();

		assertEquals(678, result);
	}

	@Test
	public void testGetAccountId() {
		when(permissions.getAccountId()).thenReturn(1230);

		int result = sessionInfoProvider.getAccountId();

		assertEquals(1230, result);
	}

	@Test
	public void testGetId() {
		when(permissions.getAccountId()).thenReturn(6);

		int result = sessionInfoProvider.getId();

		assertEquals(6, result);
	}

	@Test
	public void testGetNamespace() {
		when(actionMapping.getNamespace()).thenReturn(StrutsSessionInfoProvider.EMPLOYEEGUARD_NAMESPACE);

		NameSpace result = sessionInfoProvider.getNamespace();

		assertEquals(NameSpace.EMPLOYEEGUARD, result);
	}
}
