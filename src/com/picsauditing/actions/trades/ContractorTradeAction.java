package com.picsauditing.actions.trades;

import java.util.ArrayList;
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
		this.currentStep = ContractorRegistrationStep.Trades;
	}

	// TODO Check the security here
	public String execute() throws Exception {
		super.execute();
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

		return "trade";
	}

	public String removeTradeAjax() {
		contractor.getTrades().remove(trade);
		tradeDAO.remove(trade);
		trade = null;

		return "trade";
	}
	
	public String quickTrade() throws Exception {
		return "quick";
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

	public List<Trade> getTradeClassification() {
		List<Trade> list = tradeDAO.findListByTrade(trade.getTrade().getId(), -1);
		/*
		 * TODO: This is the only instance I have found to exclude TOP. Find out if the other usages need changing.
		 */
		list.remove(Trade.TOP);

		return list;
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

	public int getHalf() {
		if (contractor != null && contractor.getTrades().size() > 0)
			return (int) Math.floor((double) contractor.getTrades().size() / 2.0);

		return 0;
	}
	
	/**
	 * @return Next ContractorRegistrationStep, according to the ContractorRegistrationStep enum order
	 */
	@Override
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (contractor.getTrades().size() > 0)
			return ContractorRegistrationStep.values()[ContractorRegistrationStep.Trades.ordinal() + 1];

		return null;
	}

	@Override
	public String nextStep() throws Exception {
		findContractor();
		contractor.setTradesUpdated(new Date());
		tradeDAO.save(contractor);

		if (!getRegistrationStep().isDone())
			this.redirect(ContractorRegistrationStep.Risk.getUrl(contractor.getId()));

		return SUCCESS;
	}
}