package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "ContractorBadgeTest-context.xml" })
public class ContractorBadgeTest {
	private ContractorBadge contractorBadge;
	private FeatureToggle featureToggle;

	@Mock
	private Database databaseForTesting;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
		
		contractorBadge = new ContractorBadge();

		featureToggle = SpringUtils.getBean("FeatureToggle");
		reset(featureToggle);
	}

	@Test
	public void testContractorBadgeToggle_Developer() throws Exception{
		when(featureToggle.isFeatureEnabled("Toggle.Badge")).thenReturn(true);

		String strutsResult = Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle");

		assertEquals("success", strutsResult);
	}
	
	@Test
	public void testContractorBadgeToggle_Stakeholder() throws Exception{
		when(featureToggle.isFeatureEnabled("Toggle.Badge")).thenReturn(false);

		String strutsResult = Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle");

		assertEquals("failed", strutsResult);
	}
}
