package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
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
	private Map<FlagColor, List<ContractorAccount>> byFlagColor;
	private Map<Integer, FlagColor> byConID;

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
		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		getFilter().setShowInsuranceLimits(true);

		operator = operatorAccountDAO.find(permissions.getAccountId());
		
		if (operator != null && operator.getFlagCriteriaInherited() != null)
			opCriteria = operator.getFlagCriteriaInherited();
		else
			opCriteria = new ArrayList<FlagCriteriaOperator>();
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.SearchContractors);
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
						+ permissions.getAccountId() + ") ";
			if (permissions.isCorporate())
				whereQuery += "a.id IN (SELECT subID FROM generalcontractors gc "
						+ "JOIN facilities f ON gc.genID = f.opID AND f.corporateID = " + permissions.getAccountId()
						+ ") ";
			sql.addWhere(whereQuery);
		}

		sql.addJoin("JOIN invoice_fee fee on fee.id = c.membershipLevelID");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.phone");
		sql.addWhere("a.status != 'Deleted'");
		
		if (!Strings.isEmpty(getOrderBy()))
			sql.addOrderBy(getOrderBy());
		else
			sql.addOrderBy("fee.defaultAmount, a.creationDate DESC");
		
		if (getFilter().getFlagStatus() != null && getFilter().getFlagStatus().length > 0) {
			try {
				String[] flagColors = getFilter().getFlagStatus();
				getFilter().setFlagStatus(null);
				buildQuery();
				run(sql);
				calculateOverallFlags();
				List<Integer> conIDs = new ArrayList<Integer>();
				
				for (String flagColor : flagColors) {
					if (flagColor.equals("Green")) {
						for (ContractorAccount con : byFlagColor.get(FlagColor.Green)) {
							conIDs.add(con.getId());
						}
					}
					else if (flagColor.equals("Amber")) {
						for (ContractorAccount con : byFlagColor.get(FlagColor.Amber)) {
							conIDs.add(con.getId());
						}
					}
					else {
						for (ContractorAccount con : byFlagColor.get(FlagColor.Red)) {
							conIDs.add(con.getId());
						}
					}
				}
				
				sql.addWhere("a.id IN (" + Strings.implode(conIDs) + ")");
			} catch (Exception e) {
				System.out.println("Error in SQL");
			}
		}
	}

	@Override
	public String execute() throws Exception {
		if (!permissions.isOperatorCorporate())
			throw new NoRightsException("Operator or Corporate");
		
		// getFilter().setPrimaryInformation(true);
		// getFilter().setTradeInformation(true);
		getFilter().setShowMinorityOwned(true);

		if (button != null && id > 0) {
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

		if ((getFilter().getAccountName() == null
				|| ReportFilterAccount.DEFAULT_NAME.equals(getFilter().getAccountName())
				|| getFilter().getAccountName().length() < 3)
				&& (getFilter().getTrade() == null || getFilter().getTrade().length == 0)) {
			this.addActionMessage("Please enter a contractor name with at least 3 characters or select a trade");
			return SUCCESS;
		}

		return super.execute();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void calculateOverallFlags() {
		byConID = new HashMap<Integer, FlagColor>();
		byFlagColor = new HashMap<FlagColor, List<ContractorAccount>>();
		List<Integer> conIDs = new ArrayList<Integer>();
		List<ContractorAccount> red = new ArrayList<ContractorAccount>();
		List<ContractorAccount> amber = new ArrayList<ContractorAccount>();
		List<ContractorAccount> green = new ArrayList<ContractorAccount>();
		FlagDataCalculator calculator;

		for (BasicDynaBean d : data) {
			if (d.get("flag") == null)
				conIDs.add(Integer.parseInt(d.get("id").toString()));
		}
		
		List<ContractorAccount> contractors = contractorAccountDAO.findByContractorIds(conIDs);
		for (ContractorAccount contractor : contractors) {
			if (contractor.getFlagCriteria().size() > 0) {
				calculator = new FlagDataCalculator(contractor.getFlagCriteria());
				calculator.setOperatorCriteria(opCriteria);
				List<FlagData> conData = calculator.calculate();
				Collections.sort(conData, new ByFlagColor());
				
				if (conData.get(0).getFlag().equals(FlagColor.Red))
					red.add(contractor);
				else if (conData.get(0).getFlag().equals(FlagColor.Amber))
					amber.add(contractor);
				else
					green.add(contractor);
			} else
				green.add(contractor);
		}
		
		byFlagColor.put(FlagColor.Red, red);
		byFlagColor.put(FlagColor.Amber, amber);
		byFlagColor.put(FlagColor.Green, green);
		
		for (ContractorAccount con : red) {
			byConID.put(con.getId(), FlagColor.Red);
		}
		for (ContractorAccount con : amber) {
			byConID.put(con.getId(), FlagColor.Amber);
		}
		for (ContractorAccount con : green) {
			byConID.put(con.getId(), FlagColor.Green);
		}
	}

	public FlagColor getOverallFlag(int contractorID) {
		if (byConID == null)
			calculateOverallFlags();
		
		return byConID.get(contractorID);
	}

	public boolean worksForOperator(int contractorID) {
		// Check the query for an existing flag in the database lookup
		for (BasicDynaBean d : data) {
			if (d.get("id").equals(contractorID)) {
				if (d.get("flag") != null)
					// Since the flag exists, the contractor should be working for the operator
					return true;
			}
		}

		return false;
	}
	
	private class ByFlagColor implements Comparator<FlagData> {
		@Override
		public int compare(FlagData o1, FlagData o2) {
			return o2.getFlag().ordinal() - o1.getFlag().ordinal();
		}
	}
}