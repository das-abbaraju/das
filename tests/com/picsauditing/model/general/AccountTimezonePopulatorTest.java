package com.picsauditing.model.general;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;

public class AccountTimezonePopulatorTest {
	private AccountTimezonePopulator accountTimezonePopulator;

	@Mock
	private ContractorAccountDAO contractorAccountDAO;
	@Mock
	private AppPropertyDAO appPropertyDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		accountTimezonePopulator = new AccountTimezonePopulator();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(accountTimezonePopulator, this);
	}

	@Test
	public void testRun_DidNotExceedDailyMapApiLimit() throws Exception {
		int count = AccountTimezonePopulator.lookupLimit - 10;
		String json = "{\"count\":" + count + ",\"date\":\"12/7/12 2:47 PM\"}";
		when(appPropertyDAO.getProperty(AccountTimezonePopulator.limitPropertyName)).thenReturn(json);

		accountTimezonePopulator.setTotalAccountsWillRun(100);

		accountTimezonePopulator.run();

		verify(contractorAccountDAO).findCountWhere(anyString());
		verify(contractorAccountDAO).findWhere(anyString(), eq(100));
	}

	@Test
	public void testRun_ExceededDailyMapApiLimit() throws Exception {
		int count = AccountTimezonePopulator.lookupLimit + 10;
		String json = "{\"count\":" + count + ",\"date\":\"" + new SimpleDateFormat().format(new Date()) + "\"}";
		when(appPropertyDAO.getProperty(AccountTimezonePopulator.limitPropertyName)).thenReturn(json);

		accountTimezonePopulator.run();

		assertTrue(accountTimezonePopulator.getInfoMessage().startsWith("Exceeded"));
		assertFalse(accountTimezonePopulator.isPopulatorRunning());
	}

	@Test
	public void testExceededOurDailyMapApiCount_IsExceeded() throws Exception {
		boolean result = doTestExceededOurDailyMapApiCount(AccountTimezonePopulator.lookupLimit + 10);
		assertTrue(result);
	}

	@Test
	public void testExceededOurDailyMapApiCount_IsNotExceeded() throws Exception {
		boolean result = doTestExceededOurDailyMapApiCount(AccountTimezonePopulator.lookupLimit - 10);
		assertFalse(result);
	}

	@Test
	public void testExceededOurDailyMapApiCount_IsNotExceededNullProperty() throws Exception {
		when(appPropertyDAO.getProperty(AccountTimezonePopulator.limitPropertyName)).thenReturn(null);
		Boolean result = Whitebox.invokeMethod(accountTimezonePopulator, "exceededOurDailyMapApiCount");
		assertFalse(result);
	}

	private boolean doTestExceededOurDailyMapApiCount(int count) throws Exception {
		String json = "{\"count\":" + count + ",\"date\":\"" + new SimpleDateFormat().format(new Date()) + "\"}";
		when(appPropertyDAO.getProperty(AccountTimezonePopulator.limitPropertyName)).thenReturn(json);

		Boolean result = Whitebox.invokeMethod(accountTimezonePopulator, "exceededOurDailyMapApiCount");

		return result.booleanValue();
	}

}
