package com.picsauditing.actions.trades;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;

public class ContractorTradeActionTest extends PicsActionTest {

	private ContractorTradeAction contractorTradeAction;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorTradeAction = new ContractorTradeAction();
	}

	@Test
	public void testTradeAjax_WhenTradeIsNull_ThenDontThrowException() {
		contractorTradeAction.setTrade(null);

		contractorTradeAction.tradeAjax();
	}

}
