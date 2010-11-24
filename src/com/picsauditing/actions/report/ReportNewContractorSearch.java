package com.picsauditing.actions.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.Strings;

/**
 * Used by operators to search for new contractors
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ReportNewContractorSearch extends ReportAccount {

	protected int id;
	private ContractorAccountDAO contractorAccountDAO;
	private OperatorAccountDAO operatorAccountDAO;

	private FacilityChanger facilityChanger;
	private List<FlagCriteriaOperator> opCriteria;
	private OperatorAccount operator = null;
	private Map<Integer, FlagColor> byConID = new HashMap<Integer, FlagColor>();

	public ReportNewContractorSearch(ContractorAccountDAO contractorAccountDAO, FacilityChanger facilityChanger,
			OperatorAccountDAO operatorAccountDAO) {
		this.skipPermissions = true;
		this.filteredDefault = true;
		this.facilityChanger = facilityChanger;
		this.contractorAccountDAO = contractorAccountDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();

		// getFilter().setPrimaryInformation(true);
		// getFilter().setTradeInformation(true);
		getFilter().setShowMinorityOwned(true);

		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		getFilter().setShowInsuranceLimits(true);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowOpertorTagName(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowAddress(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowIndustries(true);

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

		if (permissions.isOperator()) {
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
						+ permissions.getAccountId() + " AND myf.corporateID NOT IN("+Strings.implode(Account.PICS_CORPORATE)+")) ";
			if (permissions.isCorporate())
				whereQuery += "a.id IN (SELECT subID FROM generalcontractors gc "
						+ "JOIN facilities f ON gc.genID = f.opID AND f.corporateID = " + permissions.getAccountId()
						+ ") ";
			sql.addWhere(whereQuery);
		}

		sql.addJoin("JOIN invoice_fee fee on fee.id = c.membershipLevelID");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.country");
		sql.addField("a.phone");
		sql.addField("c.score");

		if (permissions.getAccountStatus().isDemo())
			sql.addWhere("a.status IN ('Active','Demo')");
		else
			sql.addWhere("a.status = 'Active'");

		if (!Strings.isEmpty(getOrderBy()))
			sql.addOrderBy(getOrderBy());
		else
			sql.addOrderBy("fee.defaultAmount, a.creationDate DESC");

		if (getFilter().getFlagStatus() != null && getFilter().getFlagStatus().length > 0) {
			try {

				Set<FlagColor> flagColors = new HashSet<FlagColor>();
				for (String flagColor : getFilter().getFlagStatus()) {
					flagColors.add(FlagColor.valueOf(flagColor));
				}

				getFilter().setFlagStatus(null);
				// Get the data right now for all contractors
				buildQuery();
				// TODO keep getting results until we get a full page's worth
				// report.setLimit(500);
				run(sql);
				calculateOverallFlags();
				
				String conIDs = "0";
				for (Integer conID : byConID.keySet()) {
					if (flagColors.contains(getOverallFlag(conID)))
						conIDs += "," + conID;
				}
				sql.addWhere("a.id IN (" + conIDs + ")");
				
			} catch (Exception e) {
				System.out.println("Error in SQL");
			}
		}
	}

	@Override
	public String execute() throws Exception {

		if (button == null) {
			runReport = false;
			return super.execute();
		}

		if (id > 0) {
			try {
				ContractorAccount contractor = contractorAccountDAO.find(id);
				facilityChanger.setPermissions(permissions);
				facilityChanger.setContractor(id);
				facilityChanger.setOperator(permissions.getAccountId());
				if (button.equals("remove")) {
					permissions.tryPermission(OpPerms.RemoveContractors);
					facilityChanger.remove();
					addActionMessage("Successfully removed " + contractor.getName());
				}
				if (button.equals("add")) {
					permissions.tryPermission(OpPerms.AddContractors);
					facilityChanger.add();
					addActionMessage("Successfully added <a href='ContractorView.action?id=" + id + "'>"
							+ contractor.getName() + "</a>");
				}
			} catch (Exception e) {
				addActionError(e.getMessage());
			}
			return SUCCESS;
		}

		String accountName = getFilter().getAccountName();
		if ((accountName == null || ReportFilterAccount.DEFAULT_NAME.equals(accountName) || accountName.length() < 3)
				&& (getFilter().getTrade() == null || getFilter().getTrade().length == 0)) {
			this.addActionError("Please enter a contractor name with at least 3 characters or select a trade");
			return SUCCESS;
		}

		buildQuery();
		run(sql);
		return returnResult();
	}
	
	@Override
	protected String returnResult() throws IOException {
		calculateOverallFlags();
		return super.returnResult();
	}

	@Override
	protected void addExcelColumns() {
		if (permissions.isOperator()) {
			calculateOverallFlags();

			for (BasicDynaBean d : data) {
				Integer conID = Integer.parseInt(d.get("id").toString());
				d.set("flag", byConID.get(conID).toString());
			}
		}

		super.addExcelColumns();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private void calculateOverallFlags() {
		if(permissions.isCorporate())
			return;
		
		if (byConID.size() > 0)
			return;

		if (data.size() == 0)
			return;

		byConID.clear();

		Map<Integer, String> conIDs = new HashMap<Integer, String>(); 
		for (BasicDynaBean d : data) {
			String worksfor =  "";
			if(d.get("genID") == null)
				worksfor = "false";
			conIDs.put(Integer.parseInt(d.get("id").toString()), worksfor);
		}

		// TODO Maybe we could query and then trim this result here depending on the flag color filter
		
		List<ContractorAccount> contractors = contractorAccountDAO.findByContractorIds(conIDs.keySet());

		for (ContractorAccount contractor : contractors) {
			if (contractor.getFlagCriteria().size() == 0) {
				byConID.put(contractor.getId(), FlagColor.Clear);
			} else {
				FlagDataCalculator calculator = new FlagDataCalculator(contractor.getFlagCriteria());
				calculator.setOperatorCriteria(opCriteria);
				if(!conIDs.get(contractor.getId()).isEmpty())
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

}