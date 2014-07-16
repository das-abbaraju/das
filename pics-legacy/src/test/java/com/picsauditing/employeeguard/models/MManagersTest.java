package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

public abstract class MManagersTest {
	protected EGTestDataUtil egTestDataUtil;
	@Mock
	protected ApplicationContext applicationContext;

	@Mock
	protected SessionInfoProvider sessionInfoProvider;

	@Mock
	protected AccountService accountService;

	protected Map<String, Object> requestMap= new HashMap<>();

	protected void setUp() throws Exception{
		MockitoAnnotations.initMocks(this);
		egTestDataUtil = new EGTestDataUtil();

		Whitebox.setInternalState(SessionInfoProviderFactory.class, "mockSessionInfoProvider", sessionInfoProvider);
		when(sessionInfoProvider.getRequest()).thenReturn(requestMap);

		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
		when(applicationContext.getBean("AccountService")).thenReturn(accountService);

	}

}
