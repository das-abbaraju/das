package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BetaPool.class, I18nCache.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorBadgeTest {
	private ContractorBadge contractorBadge;
	@Mock
	protected  BetaPool betaPool;
	@Mock
	private Permissions permissions;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(BetaPool.class);
		PowerMockito.mockStatic(I18nCache.class);
		
		contractorBadge = new ContractorBadge();

		Map<String, String> toggles = new HashMap<String, String>();
		toggles.put("Badge", "1");
		toggles.put("SwitchUserServer", "2");

		when(permissions.getToggles()).thenReturn(toggles);

		Whitebox.setInternalState(contractorBadge, "permissions", permissions);
	}

	@Test
	public void testContractorBadgeToggle_Developer() throws Exception{
		when(BetaPool.getBetaPoolByBetaLevel(anyInt())).thenReturn(BetaPool.Developer);
		when(BetaPool.isUserBetaTester(permissions, BetaPool.Developer)).thenReturn(true);

		assertEquals("success", Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle").toString());
	}
	
	@Test
	public void testContractorBadgeToggle_Stakeholder() throws Exception{
		when(BetaPool.getBetaPoolByBetaLevel(anyInt())).thenReturn(BetaPool.Stakeholder);
		when(BetaPool.isUserBetaTester(permissions, BetaPool.Stakeholder)).thenReturn(false);

		assertEquals("failed", Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle").toString());
	}
}
