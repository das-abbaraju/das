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
	private Map<ContractorTrade, String> tradeCssMap;

	public ContractorTradeAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String tradeAjax() {
		if (trade.getId() == 0 && trade.getTrade() != null) {
			for (ContractorTrade t : contractor.getTrades()) {
				if (trade.getTrade().equals(t.getTrade())) {
					trade = t;
					break;
				}
			}
		}
		return "trade";
	}

	public String saveTradeAjax() {
		List<Trade> ancestors = new ArrayList<Trade>();
		Trade parent = trade.getTrade();
		while (parent != null) {
			ancestors.add(parent);
			parent = parent.getParent();
		}

		/*
		 * Check if the contractor already has a trade in this tree. If there is
		 * one, it needs to be changed
		 */
		for (ContractorTrade conTrade : contractor.getTrades()) {
			if (ancestors.contains(conTrade.getTrade())) {
				conTrade.setTrade(trade.getTrade());
				conTrade.setActivityPercent(trade.getActivityPercent());
				conTrade.setManufacture(trade.isManufacture());
				conTrade.setSelfPerformed(trade.isSelfPerformed());

				trade = conTrade;
				break;
			}
		}

		trade.setContractor(contractor);
		trade.setAuditColumns(permissions);
		tradeDAO.save(trade);

		return "trade";
	}

	public String removeTradeAjax() {

		return "trade";
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
