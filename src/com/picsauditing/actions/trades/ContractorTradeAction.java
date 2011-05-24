package com.picsauditing.actions.trades;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.TradeDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Tree;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class ContractorTradeAction extends ContractorActionSupport {

	@Autowired
	private TradeDAO tradeDAO;

	private ContractorTrade trade;

	private List<ContractorTrade> affectedTrades = new ArrayList<ContractorTrade>();
	
	private boolean requiresMaterial = false;
	private boolean requiresService = false;
	private List<ContractorType> conTypes;

	public ContractorTradeAction() {
		this.subHeading = getText("ContractorTrades.title");
	}
	
	// TODO Check the security here
	public String execute() throws Exception {
		super.execute();
		sortTrades();
		return SUCCESS;
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
			
			boolean onsite = contractor.isOnsiteServices();
			boolean offsite = contractor.isOffsiteServices();
			boolean material = contractor.isMaterialSupplier();
			
			boolean product = trade.getTrade().getProductI();
			boolean service = trade.getTrade().getServiceI();
			
			if (!material && product) {
				requiresMaterial = true;				
			} else if (!onsite && !offsite && service) {
				requiresService = true;
			}

			affectedTrades = findAffectedTrades();
		}
		sortTrades();
		return "trade";
	}

	public String saveTradeAjax() {
		for (ContractorTrade t : findAffectedTrades()) {
			contractor.getTrades().remove(t);
			tradeDAO.remove(t);
		}

		trade.setContractor(contractor);
		trade.setAuditColumns(permissions);
		tradeDAO.save(trade);

		if (!contractor.getTrades().contains(trade))
			contractor.getTrades().add(trade);

		// PICS-2364 if selected trade has contractorCount of 0 send email to auditors
		if (trade.getTrade().getContractorCount() == 0) {
			EmailQueue emailQueue = new EmailQueue();
			emailQueue.setSubject("New Trade Selected.");
			emailQueue.setToAddresses("auditors@picsauditing.com");
			emailQueue.setBody("Trade: " + trade.getTrade().getId() + "-" + trade.getTrade().getName()
					+ " Contractor: " + contractor.getId() + "-" + contractor.getName());

			EmailSender sender = new EmailSender();
			sender.sendNow(emailQueue);
		}
		
		contractor.addAccountTypes(conTypes);
		accountDao.save(contractor);

		sortTrades();
		return "trade";
	}

	public String removeTradeAjax() {
		contractor.getTrades().remove(trade);		
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
	
	public String getClassification() {
		StringBuilder sb = new StringBuilder();
		List<Trade> list = tradeDAO.findListByTrade(trade.getTrade().getId(), 0);
		for (Trade t:list) {
			if (sb.length() > 0) sb.append(" > ");
			sb.append(t.getName().toString());
		}
		return sb.toString();
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

	public boolean isRequiresMaterial() {
		return requiresMaterial;
	}

	public void setRequiresMaterial(boolean requiresMaterial) {
		this.requiresMaterial = requiresMaterial;
	}

	public boolean isRequiresService() {
		return requiresService;
	}

	public void setRequiresService(boolean requiresService) {
		this.requiresService = requiresService;
	}

	public List<ContractorType> getConTypes() {
		return conTypes;
	}

	public void setConTypes(List<ContractorType> conTypes) {
		this.conTypes = conTypes;
	}

}
