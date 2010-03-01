package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
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
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private FacilityChanger facilityChanger;
	private List<FlagCriteriaOperator> opCriteria;
	private Map<Integer, FlagColor> overallFlags;

	public ReportNewContractorSearch(ContractorAccountDAO contractorAccountDAO, FacilityChanger facilityChanger,
			FlagCriteriaOperatorDAO flagCriteriaOperatorDAO) {
		this.skipPermissions = true;
		this.filteredDefault = true;
		this.facilityChanger = facilityChanger;
		this.contractorAccountDAO = contractorAccountDAO;
		this.flagCriteriaOperatorDAO = flagCriteriaOperatorDAO;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		getFilter().setShowInsuranceLimits(true);
		
		if (permissions.isOperatorCorporate())
			opCriteria = flagCriteriaOperatorDAO.findByOperator(permissions.getAccountId());
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
		sql.addOrderBy("fee.defaultAmount, a.creationDate DESC");
	}

	@Override
	public String execute() throws Exception {
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
		
		for (Integer conID : conIDs) {
			Set<FlagCriteriaContractor> conCriteria = contractorAccountDAO.find(conID).getFlagCriteria();
			calculator = new FlagDataCalculator(conCriteria);
			calculator.setOperatorCriteria(opCriteria);
			
			List<FlagData> flags = calculator.calculate();
			// Assume the contractor is fine until we find different
			FlagColor holdFlag = FlagColor.Green;
			
			for (FlagData flag : flags) {
				if (flag.getFlag().equals(FlagColor.Red)) {
					holdFlag = flag.getFlag();
					break;
				} else if (flag.getFlag().equals(FlagColor.Amber)) {
					holdFlag = flag.getFlag();
				}
			}
			
			overallFlags.put(conID, holdFlag);
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
}