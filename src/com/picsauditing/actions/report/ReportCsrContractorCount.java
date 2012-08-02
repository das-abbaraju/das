package com.picsauditing.actions.report;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class ReportCsrContractorCount extends ReportAccount {

	@Autowired
	protected UserDAO userDAO = null;

	protected List<User> csrs = null;
	protected int[] csrIds;

	public void prepare() throws Exception {
		super.prepare();
		
		getFilter().setShowAccountName(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowStatus(false);
		getFilter().setShowOperator(false);
		getFilter().setShowTrade(false);
		getFilter().setShowLocation(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowRiskLevel(false);
		getFilter().setShowProductRiskLevel(false);
		getFilter().setShowService(false);
		getFilter().setShowRegistrationDate(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowSoleProprietership(false);
	}

	public void buildQuery() {
		if(!filterOn(csrIds)) {
			if(permissions.hasGroup(User.GROUP_MANAGER)) {
				csrIds = new int[getCsrs().size()];
				int i = 0;
				for(User u : getCsrs()) {
					csrIds[i] = u.getId();
					i++;
				}
			}
			else {
				csrIds = new int[1];
				csrIds[0] = permissions.getUserId();
			}
		}
		
		sql = new SelectAccount();
		sql.addField("u.name as csr");
		sql.addField("a.countrySubdivision as countrySubdivision");
		sql.addField("count(a.name) as cnt");
		sql.addJoin("JOIN contractor_info c");
		sql.addJoin("JOIN users u ON c.welcomeAuditor_id = u.id");
		String opIds = " u.id IN (" + csrIds[0];
		for (int i = 1; i < csrIds.length; i++) {
			opIds += "," + csrIds[i];
		}
		opIds += ")";
		sql.addWhere(opIds);
		sql.addWhere("a.status = 'Active'");
		sql.addWhere("a.id = c.id");
		sql.addGroupBy("u.id, a.countrySubdivision");
		
		orderByDefault = "u.name,a.countrySubdivision DESC";
		filteredDefault = true;
		
		addFilterToSQL();
	}

	public List<User> getCsrs() {
		if(csrs == null) {
			csrs = new ArrayList<User>();
			csrs = userDAO.findByGroup(User.GROUP_CSR);
		}
		return csrs;
	}
	public int[] getCsrIds() {
		return csrIds;
	}
	public void setCsrIds(int[] csrIds) {
		this.csrIds = csrIds;
	}
}