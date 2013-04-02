package com.picsauditing.actions.report;

import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;
import com.picsauditing.PicsTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.access.Permissions;

public class ReportNewContractorSearchTest extends PicsTest {
	ReportNewContractorSearch reportNewContractorSearch;

	@Mock
	private Permissions permissions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.setUp();
		reportNewContractorSearch = new ReportNewContractorSearch();
		autowireEMInjectedDAOs(reportNewContractorSearch);

		permissions = EntityFactory.makePermission();
		PicsTestUtil.forceSetPrivateField(reportNewContractorSearch,
				"permissions", permissions);

		when(i18nCache.hasKey("global.CompanyName", Locale.ENGLISH)).thenReturn(Boolean.TRUE);
		when(i18nCache.getText("global.CompanyName", Locale.ENGLISH)).thenReturn("Company Name");
	}

	@Test
	public void testBuildQuery() {
//		String[] location = {"SC"};
//		String[] flagStatus = {"Green"};
//		int[] trade = {259};
//		reportNewContractorSearch.getFilter().setAccountName(null);
//		reportNewContractorSearch.getFilter().setLocation(location);
//		reportNewContractorSearch.getFilter().setFlagStatus(flagStatus);
//		reportNewContractorSearch.getFilter().setTrade(trade);
//		reportNewContractorSearch.getFilter().setPrimaryInformation(true);
//		reportNewContractorSearch.getFilter().setTradeInformation(true);
//		reportNewContractorSearch.buildQuery();
//		String sql = reportNewContractorSearch.getSql().toString();
//		assertTrue(sql.indexOf("Green") >= 0);
	}

	@Test
	public void testAdd() {
	}

}
