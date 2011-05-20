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
		sortTrades();
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

		sortTrades();
		return "trade";
	}

	public String removeTradeAjax() {
		tradeDAO.remove(trade);
		trade = null;
		sortTrades();

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
	private void sortTrades() {
		Collections.sort(contractor.getTrades(), new Comparator<ContractorTrade>() {
			@Override
			public int compare(ContractorTrade o1, ContractorTrade o2) {
				if (o1 == null || o2 == null)
					return 0;
				return o1.getTrade().getName().toString().compareTo(o2.getTrade().getName().toString());
			}
		});
	}

}
