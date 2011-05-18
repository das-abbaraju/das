package com.picsauditing.actions.trades;

import java.io.IOException;
import java.util.Date;
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
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.util.Tree;

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

		return "trade";
	}

	public String removeTradeAjax() {
		tradeDAO.remove(trade);

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
			// table to count frequency, later on it will be used
			// to assign styles
			int[] freq = {-1, 0, -1, 0, -1, 0, -1, 0, -1, 0};
			
			tradeCssMap = new HashMap<ContractorTrade, String>();

			// determine frequencies of each activity percents
			for (ContractorTrade trade : contractor.getTrades()) {
				freq[trade.getActivityPercent()]++;
			}
			
			// determine the most frequent
			// this will be given the mid-point style
			int mostFreqIndex = 0;
			for (int i=1; i<10; i++) {
				if (freq[i] > freq[mostFreqIndex]) mostFreqIndex = i;
			}
			
			int css = 5; // most frequent style
			freq[mostFreqIndex] = css++; // assign most frequent style
			
			// assign activity percents above most frequent with greater style
			for (int i=mostFreqIndex+1; i<10; i++) {
				if (freq[i] >=0) {
					freq[i] = css++;
				}
			}
			
			// assign activity percents below most frequent with lesser style
			css = 4;
			for (int i=mostFreqIndex-1; i>=0; i--) {
				if (freq[i] >=0) {
					freq[i] = css--;
				}
			}
			
			// assign style mappings
			for (ContractorTrade trade : contractor.getTrades()) {
				tradeCssMap.put(trade, "trade-cloud-" + freq[trade.getActivityPercent()]);
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
