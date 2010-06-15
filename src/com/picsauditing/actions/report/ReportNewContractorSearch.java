package com.picsauditing.actions.report;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.Strings;

/**
 * Used by operators to search for new contractors
 */
@SuppressWarnings("serial")
public class ReportNewContractorSearch extends ReportAccount {

	protected int id;

	private FacilityChanger facilityChanger;
	private ContractorAccountDAO contractorAccountDAO;

	public ReportNewContractorSearch(ContractorAccountDAO contractorAccountDAO, FacilityChanger facilityChanger) {
		this.skipPermissions = true;
		this.filteredDefault = true;
		this.facilityChanger = facilityChanger;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();

		// getFilter().setPrimaryInformation(true);
		// getFilter().setTradeInformation(true);
		getFilter().setShowMinorityOwned(true);

		if (permissions.getCorporateParent().size() > 0)
			getFilter().setShowInParentCorporation(true);

		orderByDefault = "fee.defaultAmount, gc.flag DESC, a.creationDate DESC";

		getFilter().setShowInsuranceLimits(true);
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

		// Because the flag filters always filter on gc.flag, I had to rename
		// the flags table to gc here
		sql.addJoin("LEFT JOIN flags gc on gc.conID = a.id AND gc.opID = " + permissions.getAccountId());
		sql.addField("gc.flag");
		sql.addField("lower(gc.flag) AS lflag");

		sql.addJoin("LEFT JOIN generalcontractors co on co.subID = a.id AND co.genID = " + permissions.getAccountId());
		sql.addField("co.id coID");
		sql.addField("co.workStatus");

		sql.addJoin("JOIN invoice_fee fee on fee.id = c.membershipLevelID");
		sql.addField("a.city");
		sql.addField("a.state");
		sql.addField("a.country");
		sql.addField("a.phone");

		if (permissions.getAccountStatus().isDemo())
			sql.addWhere("a.status IN ('Active','Demo')");
		else
			sql.addWhere("a.status = 'Active'");

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

	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		checkPermissions();

		if (button == null) {
			// First time on this page, don't run the report
			return SUCCESS;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}