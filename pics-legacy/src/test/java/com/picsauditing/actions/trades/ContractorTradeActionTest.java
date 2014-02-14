package com.picsauditing.actions.trades;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;

import java.util.TreeSet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class ContractorTradeActionTest extends PicsActionTest {

	private ContractorTradeAction contractorTradeAction;
    @Mock
    ContractorAccountDAO contractorAccountDAO;
    @Mock
    TradeDAO tradeDAO;
    @Mock
    ContractorAccount contractorAccount;
    @Mock
    ContractorTrade newConTrade;
    @Mock
    ContractorTrade conTrade1;
    @Mock
    ContractorTrade conTrade2;
    @Mock
    Trade trade;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		contractorTradeAction = new ContractorTradeAction();
        setInternalState(contractorTradeAction, "contractorAccountDao", contractorAccountDAO);
        setInternalState(contractorTradeAction, "tradeDAO", tradeDAO);
        contractorTradeAction.setContractor(contractorAccount);
        TreeSet<ContractorTrade> contractorTrades = new TreeSet<ContractorTrade>();
        contractorTrades.add(conTrade1);
        contractorTrades.add(conTrade2);
        when(newConTrade.getTrade()).thenReturn(trade);
        when(conTrade1.getTrade()).thenReturn(trade);
        when(conTrade2.getTrade()).thenReturn(trade);
        when(trade.getId()).thenReturn(1111);
        when(trade.getContractorCount()).thenReturn(1);
        when(trade.getSafetyRiskI()).thenReturn(LowMedHigh.High);
        when(trade.getSafetySensitiveI()).thenReturn(YesNo.Yes);
        when(contractorAccount.getTrades()).thenReturn(contractorTrades);
	}

	@Test
	public void testTradeAjax_WhenTradeIsNull_ThenDontThrowException() {
		contractorTradeAction.setTrade(null);

		contractorTradeAction.tradeAjax();
	}

    @Test
    public void testSaveTradeAjax_NullTrade() {
        contractorTradeAction.setTrade(newConTrade);

        when(contractorAccount.getTradeSafetyRisk()).thenReturn(LowMedHigh.None);
        when(trade.getSafetySensitiveI()).thenReturn(null);

        contractorTradeAction.saveTradeAjax();
    }

    @Test
    public void testSaveTradeAjax_UpdateSafetySensitivity() {
        contractorTradeAction.setTrade(newConTrade);

        when(contractorAccount.getTradeSafetyRisk()).thenReturn(LowMedHigh.None);
        when(contractorAccount.isTradeSafetySensitive()).thenReturn(true);
        when(trade.getSafetySensitiveI()).thenReturn(YesNo.Yes);

        contractorTradeAction.saveTradeAjax();
        verify(contractorAccount).setSafetySensitive(true);
    }

}
