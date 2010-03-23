package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.jboss.util.Strings;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.ReportFilterAccount;

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
	private FlagCriteriaContractorDAO flagCriteriaContractorDAO;
	private OperatorAccountDAO operatorAccountDAO;
	
	private FacilityChanger facilityChanger;
	private List<FlagCriteriaOperator> opCriteria;
	private Map<Integer, FlagColor> overallFlags;
	private OperatorAccount operator = null;

	public ReportNewContractorSearch(ContractorAccountDAO contractorAccountDAO, FacilityChanger facilityChanger,
			OperatorAccountDAO operatorAccountDAO, FlagCriteriaContractorDAO flagCriteriaContractorDAO) {
		this.skipPermissions = true;
		this.filteredDefault = true;
		this.facilityChanger = facilityChanger;
		this.contractorAccountDAO = contractorAccountDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.flagCriteriaContractorDAO = flagCriteriaContractorDAO;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		getFilter().setShowInsuranceLimits(true);

		operator = operatorAccountDAO.find(permissions.getAccountId());
		opCriteria = new ArrayList<FlagCriteriaOperator>();
		
		if (operator != null && operator.getFlagCriteriaInherited() != null) {
			opCriteria.addAll(operator.getFlagCriteriaInherited());
		}
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
				|| ReportFilterAccount.DEFAULT_NAME.equals(getFilter().getAccountName()) || getFilter()
				.getAccountName().length() < 3)
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
		overallFlags = new HashMap<Integer, FlagColor>();
		List<Integer> conIDs = new ArrayList<Integer>();
		FlagDataCalculator calculator;

		for (BasicDynaBean d : data) {
			conIDs.add(Integer.parseInt(d.get("id").toString()));
		}
		
		List<FlagCriteriaContractor> allConCriterias = flagCriteriaContractorDAO.findByContractorList(conIDs);
		Collections.sort(conIDs);
		Collections.sort(allConCriterias, new ByContractorID());
		
		int position = 0;
		Set<FlagCriteriaContractor> conCriteria;
		
		for (int conID : conIDs) {
			conCriteria = new HashSet<FlagCriteriaContractor>();
			FlagColor overallColor = FlagColor.Green;
			
			while (position < allConCriterias.size() && allConCriterias.get(position).getContractor().getId() == conID) {
				conCriteria.add(allConCriterias.get(position));
				position++;
			}
			
			if (conCriteria.size() > 0) {
				calculator = new FlagDataCalculator(conCriteria);
				calculator.setOperator(operator);
				calculator.setOperatorCriteria(opCriteria);
				List<FlagData> conFlags = calculator.calculate();
				
				for (FlagData conFlag : conFlags) {
					if (conFlag.getFlag().equals(FlagColor.Red)) {
						overallColor = FlagColor.Red;
						break;
					} else if (conFlag.getFlag().equals(FlagColor.Amber))
						overallColor = FlagColor.Amber;
				}
			}
			
			overallFlags.put(conID, overallColor);
		}
	}

	public FlagColor getOverallFlag(int contractorID) {
		if (overallFlags == null)
			calculateOverallFlags();

		return overallFlags.get(contractorID);
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
	
	private class ByContractorID implements Comparator<FlagCriteriaContractor> {
		public int compare(FlagCriteriaContractor o1, FlagCriteriaContractor o2) {
			return o1.getContractor().getId() - o2.getContractor().getId();
		}
	}
}