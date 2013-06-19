package com.picsauditing.actions.contractors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "ContractorBadgeTest-context.xml" })
public class ContractorBadgeTest extends PicsTranslationTest {
	private ContractorBadge contractorBadge;
	private FeatureToggle featureToggle;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		contractorBadge = new ContractorBadge();

		featureToggle = SpringUtils.getBean("FeatureToggle");
		reset(featureToggle);
	}

	@Test
	public void testContractorBadgeToggle_Developer() throws Exception {
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_BADGE)).thenReturn(true);

		String strutsResult = Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle");

		assertEquals("success", strutsResult);
	}

	@Test
	public void testContractorBadgeToggle_Stakeholder() throws Exception {
		when(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_BADGE)).thenReturn(false);

		String strutsResult = Whitebox.invokeMethod(contractorBadge, "contractorBadgeToggle");

		assertEquals("failed", strutsResult);
	}
}
