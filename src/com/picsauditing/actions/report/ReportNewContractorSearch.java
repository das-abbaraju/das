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
	private Map<FlagColor, List<Integer>> byFlagColor;
	private Map<Integer, FlagColor> byConID;
	private boolean sortByFlags = false;
	private int page = 1;

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
				String[] flagColors = getFilter().getFlagStatus();
				getFilter().setFlagStatus(null);
				// Get the data right now for all contractors
				buildQuery();
				run(sql);
				calculateOverallFlags();
				List<Integer> conIDs = new ArrayList<Integer>();
				
				// Get contractors that end up with specified flags
				for (String flagColor : flagColors) {
					if (flagColor.equals("Green")) {
						for (Integer conID : byFlagColor.get(FlagColor.Green)) {
							conIDs.add(conID);
						}
					}
					else if (flagColor.equals("Amber")) {
						for (Integer conID : byFlagColor.get(FlagColor.Amber)) {
							conIDs.add(conID);
						}
					}
					else {
						for (Integer conID : byFlagColor.get(FlagColor.Red)) {
							conIDs.add(conID);
						}
					}
				}
				
				// Limit the query to the contractors whose flags were chosen.
				if(conIDs.size() == 0)
					conIDs.add(0);
				sql.addWhere("a.id IN (" + Strings.implode(conIDs) + ")");
			} catch (Exception e) {
				System.out.println("Error in SQL");
			}
		}
		
		if (getOrderBy() != null && getOrderBy().startsWith("flag")) {
			setOrderBy("");
			sortByFlags = true;
		} else
			sortByFlags = false;
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
	
	@Override
	protected void addExcelColumns() {
		if (permissions.isOperator()) {
			if (byConID == null || byConID.size() == 0)
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
	
	public boolean isSortByFlags() {
		return sortByFlags;
	}
	
	public int getPageIndex() {
		return (page - 1) * 100 + 1;
	}

	public void calculateOverallFlags() {
		List<Integer> conIDs = new ArrayList<Integer>();
		List<Integer> red = new ArrayList<Integer>();
		List<Integer> amber = new ArrayList<Integer>();
		List<Integer> green = new ArrayList<Integer>();
		FlagDataCalculator calculator;

		for (BasicDynaBean d : data) {
			conIDs.add(Integer.parseInt(d.get("id").toString()));
		}
		
		if (conIDs.size() > 0) {
			List<ContractorAccount> contractors = contractorAccountDAO.findByContractorIds(conIDs);
			for (ContractorAccount contractor : contractors) {
				if (contractor.getFlagCriteria().size() > 0) {
					calculator = new FlagDataCalculator(contractor.getFlagCriteria());
					calculator.setOperatorCriteria(opCriteria);
					List<FlagData> conData = calculator.calculate();
					
					if (conData.size() > 0) {
						Collections.sort(conData, new ByFlagColor());
					
						if (conData.get(0).getFlag().equals(FlagColor.Red)) {
							red.add(contractor.getId());
							continue;
						}
						else if (conData.get(0).getFlag().equals(FlagColor.Amber)) {
							amber.add(contractor.getId());
							continue;
						}
					}
				}
				
				green.add(contractor.getId());
			}
			
			byConID = new HashMap<Integer, FlagColor>();
			byFlagColor = new HashMap<FlagColor, List<Integer>>();
			
			byFlagColor.put(FlagColor.Red, red);
			byFlagColor.put(FlagColor.Amber, amber);
			byFlagColor.put(FlagColor.Green, green);
			
			for (Integer conID : red) {
				byConID.put(conID, FlagColor.Red);
			}
			for (Integer conID : amber) {
				byConID.put(conID, FlagColor.Amber);
			}
			for (Integer conID : green) {
				byConID.put(conID, FlagColor.Green);
			}
		}
	}

	public FlagColor getOverallFlag(int contractorID) {
		if (byConID == null || byConID.size() == 0)
			calculateOverallFlags();
		
		return byConID.get(contractorID);
	}
	
	public List<BasicDynaBean> getOrderedByFlag() {
		page = getShowPage();
		
		try {
			report.setLimit(100000);
			setShowPage(1);
			buildQuery();
			run(sql);
		} catch (Exception e) {
			System.out.println("Error running SQL in getOrderedByFlag()");
		}
		// Green, Amber, then Red
		List<BasicDynaBean> green = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> amber = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> red = new ArrayList<BasicDynaBean>();
		
		if (byFlagColor == null)
			calculateOverallFlags();
		
		for (BasicDynaBean d : data) {
			if (byFlagColor.get(FlagColor.Green).contains((Integer) d.get("id")))
				green.add(d);
			else if (byFlagColor.get(FlagColor.Amber).contains((Integer) d.get("id")))
				amber.add(d);
			else
				red.add(d);
		}
		
		Collections.sort(green, new ByContractorName());
		Collections.sort(amber, new ByContractorName());
		Collections.sort(red, new ByContractorName());
		
		green.addAll(amber);
		green.addAll(red);
		
		report.setLimit(100);
		
		return green.subList(100 * (page - 1),
			(100 * page < green.size()) ? (100 * page) : green.size() );
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
	
	private class ByContractorName implements Comparator<BasicDynaBean> {
		public int compare(BasicDynaBean o1, BasicDynaBean o2) {
			return ((String) o1.get("name")).compareToIgnoreCase((String) o2.get("name"));
		}
	}
}