package com.picsauditing.actions.report;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.ReportFilterAccount;

/**
 * Used by operators to search for new contractors
 * 
 * @author Trevor
 * 
 */
public class ReportNewContractorSearch extends ReportAccount {
	protected int id;
	protected boolean inParentCorporation = false;
	protected boolean filterInParentCorporation = true;

	private ContractorAccountDAO contractorAccountDAO;
	private FacilityChanger facilityChanger;

	public ReportNewContractorSearch(ContractorAccountDAO contractorAccountDAO, FacilityChanger facilityChanger) {
		this.skipPermissions = true;
		this.filtered = true;
		this.facilityChanger = facilityChanger;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.SearchContractors);
		
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

		if (permissions.isOperator()) {
			// Anytime we query contractor accounts as an operator,
			// get the flag color/status at the same time
			sql.addJoin("LEFT JOIN flags ON flags.conID = a.id AND flags.opID = " + permissions.getAccountId());
			sql.addField("flags.flag");
			sql.addField("lower(flags.flag) AS lflag");
			sql.addJoin("LEFT JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID = "
					+ permissions.getAccountId());
			sql.addField("gc.genID");
			sql.addField("gc.workStatus");
		}

		if (inParentCorporation) {
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

		if ((accountName == null || ReportFilterAccount.DEFAULT_NAME.equals(accountName) || accountName.length() < 3)
				&& (trade == null || trade.length == 0)) {
			this.addActionMessage("Please enter a contractor name with atleast 3 characters or select a trade");
			return SUCCESS;
		}
		if (this.orderBy == null || orderBy.length() == 0)
			this.orderBy = "a.name";

		sql.addField("a.contact");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.phone");
		sql.addField("a.phone2");
		sql.addWhere("a.active = 'Y'");
		return super.execute();
	}

	public boolean isInParentCorporation() {
		return inParentCorporation;
	}

	public void setInParentCorporation(boolean inParentCorporation) {
		filtered = true;
		// Add the where clause in the execute method, after we get permissions
		this.inParentCorporation = inParentCorporation;
	}

	public boolean isFilterInParentCorporation() {
		return filterInParentCorporation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
