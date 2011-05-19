package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Tree;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class ContractorTradeAction extends ContractorActionSupport {

	@Autowired
	private TradeDAO tradeDAO;

	private ContractorTrade trade;
	private Map<ContractorTrade, String> tradeCssMap;

	private List<ContractorTrade> affectedTrades = new ArrayList<ContractorTrade>();

	public ContractorTradeAction() {
		this.subHeading = getText("ContractorTrades.title");
	}

	public String tradeAjax() {
		if (trade.getId() == 0 && trade.getTrade() != null) {

			/*
			 * Look for the existing trade in the current contractor's trades This will help prevent them from adding
			 * duplicates.
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

		if (!contractor.getTrades().contains(trade))
			contractor.getTrades().add(trade);

		// TODO Sort Contractor Trades by Name

		return "trade";
	}

	public String removeTradeAjax() {
		tradeDAO.remove(trade);
		trade = null;

		return "trade";
	}

	public String nextStep() throws Exception {
		findContractor();
		contractor.setTradesUpdated(new Date());
		tradeDAO.save(contractor);

		if (!getRegistrationStep().isDone())
			this.redirect(ContractorRegistrationStep.Risk.getUrl(contractor.getId()));

		return SUCCESS;
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

	public List<ContractorTrade> getAffectedTrades() {
		return affectedTrades;
	}

	public Map<ContractorTrade, String> getTradeCssMap() {
		if (tradeCssMap == null) {
			tradeCssMap = new HashMap<ContractorTrade, String>();
			int sumTrades = 0;
			for (ContractorTrade trade : contractor.getTrades()) {
				sumTrades += trade.getActivityPercent();
				if (trade.isSelfPerformed() || trade.isManufacture())
					sumTrades++;
			}

			// assign style mappings
			for (ContractorTrade trade : contractor.getTrades()) {
				// int activityPercent = trade.getActivityPercent() * trade.getActivityPercent();
				int activityPercent = trade.getActivityPercent();
				// if (trade.isSelfPerformed() || trade.isManufacture())
				// activityPercent++;

				int tradePercent = Math.round(10f * activityPercent / sumTrades);

				switch (trade.getActivityPercent()) {
				case 1:
					tradePercent = cap(tradePercent, 1, 6);
					break;
				case 3:
					tradePercent = cap(tradePercent, 2, 7);
					break;
				case 5:
					tradePercent = cap(tradePercent, 3, 8);
					break;
				case 7:
					tradePercent = cap(tradePercent, 4, 9);
					break;
				case 9:
					tradePercent = cap(tradePercent, 5, 10);
					break;

				default:
					tradePercent = cap(tradePercent, 1, 10);
				}
				tradeCssMap.put(trade, "" + tradePercent);

			}
		}

		return tradeCssMap;
	}

	private int cap(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
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
	
	/**
	 * Sorts contractor trades for cloud
	 * 
	 * @return
	 */
	public String cloudTrades() throws Exception {
		findContractor();

		Collections.sort(contractor.getTrades(), new Comparator<ContractorTrade>() {
			@Override
			public int compare(ContractorTrade o1, ContractorTrade o2) {
				if (o1 == null || o2 == null)
					return 0;
				return o1.getTrade().getName().toString().compareTo(o2.getTrade().getName().toString());
			}
		});

		return "cloud";
	}

}
