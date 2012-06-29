package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
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

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ContractorBadge.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class ContractorBadgeTest {
	private ContractorBadge contractorBadge;
	//@Mock
	//protected  BetaPool betaPool;
	@Mock
	private Permissions permissions;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorBadge = new ContractorBadge();

		Map<String, String> toggles = new HashMap<String, String>();
		toggles.put("Badge", "1");
		toggles.put("SwitchUserServer", "2");
		when(permissions.getToggles()).thenReturn(toggles);
		permissions.getGroups().add(User.GROUP_DEVELOPER);
		Whitebox.setInternalState(contractorBadge, "permissions", permissions);
	}

	@Test
	public void testDisplayContractorBadge() throws Exception{
		//PowerMockito.stub(PowerMockito.method(BetaPool.class, "getBetaPoolByBetaLevel")).toReturn(BetaPool.Developer);
		//PowerMockito.stub(PowerMockito.method(BetaPool.class, "isUserBetaTester")).toReturn(true);
		//TODO: not sure why its not working.  Need help
		//assertEquals("SUCCESS", Whitebox.invokeMethod(contractorBadge, "displayContractorBadge").toString());
	}
}
