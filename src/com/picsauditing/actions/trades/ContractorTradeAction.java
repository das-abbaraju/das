package com.picsauditing.actions.trades;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Tree;

@SuppressWarnings("serial")
public class ContractorTradeAction extends ContractorActionSupport {

	private TradeDAO tradeDAO;

	private ContractorTrade trade;
	private Tree<Trade> tradeHiererchy;
	private Map<ContractorTrade, String> tradeCssMap;

	public ContractorTradeAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String tradeAjax() {
		return "trade";
	}

	public void setTradeDAO(TradeDAO tradeDAO) {
		this.tradeDAO = tradeDAO;
	}

	public ContractorTrade getTrade() {
		return trade;
	}

	public void setTrade(ContractorTrade trade) {
		this.trade = trade;
	}

	public Tree<Trade> getTradeHiererchy() {
		return tradeHiererchy;
	}

	public void setTradeHiererchy(Tree<Trade> tradeHiererchy) {
		this.tradeHiererchy = tradeHiererchy;
	}

	public Map<ContractorTrade, String> getTradeCssMap() {
		if (tradeCssMap == null) {
			tradeCssMap = new HashMap<ContractorTrade, String>();
			int total = 0;
			for (ContractorTrade trade : contractor.getTrades()) {
				total += trade.getActivityPercent();
			}

			for (ContractorTrade trade : contractor.getTrades()) {
				int percentage = (int) (((float) trade.getActivityPercent() / total) * 100);
				tradeCssMap.put(trade, "trade-" + percentage / 10);
			}
		}

		return tradeCssMap;
	}

	public Map<Integer, String> getActivityPercentMap() {
		Map<Integer, String> result = new LinkedHashMap<Integer, String>();
		result.put(9, "most");
		result.put(7, "aboveAverage");
		result.put(5, "average");
		result.put(3, "belowAverage");
		result.put(1, "rarely");

		return result;
	}

}
