package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Tree;

@SuppressWarnings("serial")
public class ContractorTradeAction extends ContractorActionSupport {

	@Autowired
	private TradeDAO tradeDAO;

	private ContractorTrade trade;
	private Tree<Trade> tradeHiererchy;
	private Map<ContractorTrade, Integer> tradeCssMap;

	private List<ContractorTrade> affectedTrades = new ArrayList<ContractorTrade>();

	public ContractorTradeAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String tradeAjax() {
		if (trade.getId() == 0 && trade.getTrade() != null) {

			/*
			 * Look for the existing trade in the current contractor's trades
			 * This will help prevent them from adding duplicates.
			 */
			for (ContractorTrade t : contractor.getTrades()) {
				if (trade.getTrade().equals(t.getTrade())) {
					trade = t;
					break;
				}
			}

			affectedTrades = findAffectedTrades();
		}
		return "trade";
	}

	public String saveTradeAjax() {
		for (ContractorTrade t : findAffectedTrades()) {
			tradeDAO.remove(t);
		}

		trade.setContractor(contractor);
		trade.setAuditColumns(permissions);
		tradeDAO.save(trade);

		return "trade";
	}

	public String removeTradeAjax() {
		tradeDAO.remove(trade);

		return "trade";
	}

	public List<ContractorTrade> findAffectedTrades() {

		List<ContractorTrade> trades = new ArrayList<ContractorTrade>();
		Tree<Trade> hierarchy = tradeDAO.findHierarchyByTrade(trade.getTrade().getId());
		for (ContractorTrade conTrade : contractor.getTrades()) {
			if (!trade.getTrade().equals(conTrade.getTrade()) && hierarchy.contains(conTrade.getTrade())) {
				trades.add(conTrade);
			}
		}

		return trades;
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

	public List<ContractorTrade> getAffectedTrades() {
		return affectedTrades;
	}

	public Map<ContractorTrade, Integer> getTradeCssMap() {
		if (tradeCssMap == null) {
			tradeCssMap = new HashMap<ContractorTrade, Integer>();
			int total = 0;
			for (ContractorTrade trade : contractor.getTrades()) {
				total += trade.getActivityPercent();
			}

			for (ContractorTrade trade : contractor.getTrades()) {
				int percentage = (int) (((float) trade.getActivityPercent() / total) * 100);
				tradeCssMap.put(trade, 14 + (percentage * (30 - 14)) / 100);
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
