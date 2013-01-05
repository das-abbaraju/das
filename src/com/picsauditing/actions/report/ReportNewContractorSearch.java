package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.annotations.Before;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

/**
 * Used by operators to search for new contractors
 */
@SuppressWarnings("serial")
public class ReportNewContractorSearch extends ReportAccount {
	@Autowired
	private ContractorAccountDAO contractorAccountDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private FacilityChanger facilityChanger;
	@Autowired
	private EmailSender emailSender;

	private List<FlagCriteriaOperator> opCriteria;
	private OperatorAccount operator;
	private ContractorAccount contractor;
	private Map<Integer, FlagColor> byConID = new HashMap<Integer, FlagColor>();

	private final Logger logger = LoggerFactory.getLogger(ReportNewContractorSearch.class);

	public ReportNewContractorSearch() {
		this.skipPermissions = true;
		this.filteredDefault = true;
		this.searchForNew = true;
	}

	@Before
	public void startup() throws Exception {
		if (operator == null)
			operator = operatorAccountDAO.find(permissions.getAccountId());

		if (operator != null && operator.getFlagCriteriaInherited() != null)
			opCriteria = operator.getFlagCriteriaInherited();
		else
			opCriteria = new ArrayList<FlagCriteriaOperator>();
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.SearchContractors);

		if (!permissions.isOperatorCorporate())
			throw new NoRightsException("Operator or Corporate");
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		if (permissions.isOperatorCorporate()) {
			// Anytime we query contractor accounts as an operator,
			// get the flag color/status at the same time
			sql.addJoin("LEFT JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = "
					+ permissions.getAccountId());
			sql.addField("gc.genID");
			sql.addField("gc.workStatus");
			sql.addField("gc.flag");
			sql.addField("lower(gc.flag) AS lflag");
		}

		if (getFilter().isInParentCorporation()) {
			String whereQuery = "";
			if (permissions.isOperator())
				whereQuery += "a.id IN (SELECT subID FROM generalcontractors gc "
						+ "JOIN facilities f ON gc.genID = f.opID "
						+ "JOIN facilities myf ON f.corporateID = myf.corporateID AND myf.opID = "
						+ permissions.getAccountId() + " AND myf.corporateID NOT IN("
						+ Strings.implode(Account.PICS_CORPORATE) + ")) ";
			if (permissions.isCorporate())
				whereQuery += "a.id IN (SELECT subID FROM generalcontractors gc "
						+ "JOIN facilities f ON gc.genID = f.opID AND f.corporateID = " + permissions.getAccountId()
						+ ") ";
			sql.addWhere(whereQuery);
		}

		sql.addField("a.city");
		sql.addField("a.countrySubdivision");
		sql.addField("a.country");
		sql.addField("a.phone");
		sql.addField("c.score");
		sql.addField("c.showInDirectory");

		sql.addAudit(AuditType.PQF);

		if (permissions.getAccountStatus().isDemo())
			sql.addWhere("a.status IN ('Active', 'Requested', 'Demo')");
		else
			sql.addWhere("a.status IN ('Active', 'Requested')");

		if (!Strings.isEmpty(getOrderBy()))
			sql.addOrderBy(getOrderBy());
		else
			sql.addOrderBy("a.creationDate DESC");

		if (getFilter().getFlagStatus() != null && getFilter().getFlagStatus().length > 0) {
			try {

				Set<FlagColor> flagColors = new HashSet<FlagColor>();
				for (String flagColor : getFilter().getFlagStatus()) {
					flagColors.add(FlagColor.valueOf(flagColor));
				}

				getFilter().setFlagStatus(null);

				// Get the data right now for all contractors
				// this will build up the contractor ids we need
				run(sql);
				calculateOverallFlags();

				String conIDs = "0";
				for (Integer conID : byConID.keySet()) {
					if (flagColors.contains(getOverallFlag(conID)))
						conIDs += "," + conID;
				}
				sql.addWhere("a.id IN (" + conIDs + ")");

			} catch (Exception e) {
				logger.error("Error in SQL: {}", e.getMessage());
			}
		}
	}

	@Override
	public String execute() throws Exception {
		if (ActionContext.getContext().getSession().get("actionMessage") != null) {
			addActionMessage(ActionContext.getContext().getSession().get("actionMessage").toString());
			ActionContext.getContext().getSession().remove("actionMessage");
		}

		getFilter().setShowInsuranceLimits(true);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowOpertorTagName(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowMinorityOwned(true);
		getFilter().setShowLocation(true);
		getFilter().setPermissions(permissions);

		if (!permissions.isOperator())
			getFilter().setShowFlagStatus(false);

		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		if (button == null) {
			runReport = false;
			return super.execute();
		}

		String accountName = getFilter().getAccountName();
		if ((accountName == null || ReportFilterAccount.getDefaultName().equals(accountName) || accountName.length() < 3)
				&& (getFilter().getTrade() == null || getFilter().getTrade().length == 0)) {
			this.addActionError(getText("NewContractorSearch.error.SelectTradeOrContractorName"));
			return SUCCESS;
		}
		// Default to showing self-performed
		if (filterOn(getFilter().getTrade()) && getFilter().getShowSelfPerformedTrade() == 2) {
			getFilter().setShowSelfPerformedTrade(1);
		}

		buildQuery();
		run(sql);
		return returnResult();
	}

	@RequiredPermission(value = OpPerms.AddContractors)
	public String add() throws Exception {
		facilityChanger.setPermissions(permissions);
		facilityChanger.setContractor(contractor);
		facilityChanger.setOperator(operator);
		facilityChanger.add();

		if (operator.isGeneralContractor() && !contractor.isAutoApproveRelationships()) {
			addActionMessage(getTextParameterized("NewContractorSearch.ContractorNeedsToApprove", contractor.getName()));
		} else {
			addActionMessage(getTextParameterized("NewContractorSearch.message.SuccessfullyAdded", contractor.getId()
					+ "", contractor.getName()));
		}

		// Automatically upgrading Contractor per discussion
		// 2/15/2011
		if (contractor.getAccountLevel().isBidOnly() && !operator.isAcceptsBids()) {
			// See also ReportBiddingContractors/ContractorDashboard
			// Upgrade
			contractor.setAccountLevel(AccountLevel.Full);
			contractor.setRenew(true);

			auditBuilder.buildAudits(contractor);

			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf()) {
					for (ContractorAuditOperator cao : cAudit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							cao.changeStatus(AuditStatus.Pending, permissions);
							auditDataDAO.save(cao);
						}
					}

					auditBuilder.recalculateCategories(cAudit);
					auditPercentCalculator.recalcAllAuditCatDatas(cAudit);
					auditPercentCalculator.percentCalculateComplete(cAudit);
					auditDataDAO.save(cAudit);
				}
			}

			contractor.incrementRecalculation();
			contractor.setAuditColumns(permissions);
			contractorAccountDAO.save(contractor);

			// Sending a Email to the contractor for upgrade
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(73); // Trial Contractor
			// Account Approval
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
			emailBuilder.addToken("permissions", permissions);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setFromAddress(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
			emailQueue.setViewableById(Account.PicsID);
			emailSender.send(emailQueue);
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.RemoveContractors)
	public String remove() throws Exception {
		facilityChanger.setPermissions(permissions);
		facilityChanger.setContractor(contractor);
		facilityChanger.setOperator(operator);
		facilityChanger.remove();

		ActionContext
				.getContext()
				.getSession()
				.put("actionMessage",
						getText("NewContractorSearch.message.SuccessfullyRemoved",
								new Object[] { contractor.getName() }));

		return setUrlForRedirect("NewContractorSearch.action?filter.performedBy=Self Performed&filter.primaryInformation=true"
				+ "&filter.tradeInformation=true");
	}

	@Override
	protected String returnResult() throws IOException {
		calculateOverallFlags();
		return super.returnResult();
	}

	@Override
	protected void addExcelColumns() {
		if (permissions.isOperatorCorporate()) {
			calculateOverallFlags();

			for (BasicDynaBean d : data) {
				Integer conID = Integer.parseInt(d.get("id").toString());

				if (byConID == null) {
					continue;
				}

				FlagColor flagColor = byConID.get(conID);
				if (flagColor == null) {
					continue;
				}

				d.set("flag", flagColor.toString());
			}
		}

		super.addExcelColumns();
	}

	private void calculateOverallFlags() {
		if (!permissions.isOperator())
			return;

		if (byConID.size() > 0)
			return;

		if (data.size() == 0)
			return;

		byConID.clear();

		Map<Integer, String> conIDs = new HashMap<Integer, String>();
		for (BasicDynaBean d : data) {
			String worksfor = "";
			if (d.get("genID") == null)
				worksfor = "false";
			conIDs.put(Integer.parseInt(d.get("id").toString()), worksfor);
		}

		// TODO Maybe we could query and then trim this result here depending on
		// the flag color filter

		List<ContractorAccount> contractors = contractorAccountDAO.findByContractorIds(conIDs.keySet());

		for (ContractorAccount contractor : contractors) {
			if (contractor.getFlagCriteria().size() == 0) {
				byConID.put(contractor.getId(), FlagColor.Clear);
			} else {
				FlagDataCalculator calculator = new FlagDataCalculator(contractor.getFlagCriteria());
				calculator.setOperatorCriteria(opCriteria);
				if (!conIDs.get(contractor.getId()).isEmpty())
					calculator.setWorksForOperator(false);
				calculator.setOperator(operator);
				FlagColor flagColor = getWorstColor(calculator.calculate());
				byConID.put(contractor.getId(), flagColor);
			}
		}
	}

	/**
	 * We may want to consider moving this into FlagDataCalculator
	 * 
	 * @param flagData
	 * @return
	 */
	private FlagColor getWorstColor(List<FlagData> flagData) {
		if (flagData == null)
			return null;
		FlagColor worst = FlagColor.Green;
		for (FlagData flagDatum : flagData) {
			if (flagDatum.getFlag().isRed())
				return flagDatum.getFlag();
			if (flagDatum.getFlag().isAmber())
				worst = flagDatum.getFlag();
		}

		return worst;
	}

	public FlagColor getOverallFlag(int contractorID) {
		return byConID.get(contractorID);
	}

	public boolean worksForOperator(int contractorID) {
		// Check the query for an existing flag in the database lookup
		for (BasicDynaBean d : data) {
			if (d.get("id").equals(contractorID)) {
				if (d.get("flag") != null)
					// Since the flag exists, the contractor should be working
					// for the operator
					return true;
			}
		}
		return false;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public boolean isHasInsuranceCriteria() {
		if (getFilter().isShowInsuranceLimits()) {
			return filterOn(getFilter().getGlEachOccurrence(), ReportFilterContractor.getDefaultAmount())
					|| filterOn(getFilter().getGlGeneralAggregate(), ReportFilterContractor.getDefaultAmount())
					|| filterOn(getFilter().getAlCombinedSingle(), ReportFilterContractor.getDefaultAmount())
					|| filterOn(getFilter().getWcEachAccident(), ReportFilterContractor.getDefaultAmount())
					|| filterOn(getFilter().getExEachOccurrence(), ReportFilterContractor.getDefaultAmount());
		}

		return false;
	}
}
