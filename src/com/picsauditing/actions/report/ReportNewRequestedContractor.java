package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportNewRequestedContractor extends ReportActionSupport {
	protected StateDAO stateDAO;
	protected CountryDAO countryDAO;
	protected AccountUserDAO accountUserDAO;

	public ReportNewRequestedContractor(StateDAO stateDAO, CountryDAO countryDAO, AccountUserDAO accountUserDAO) {
		this.stateDAO = stateDAO;
		this.countryDAO = countryDAO;
		this.accountUserDAO = accountUserDAO;
	}

	protected SelectSQL sql = new SelectSQL();

	public SelectSQL getSql() {
		return sql;
	}

	public void setSql(SelectSQL sql) {
		this.sql = sql;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor())
			throw new NoRightsException("PICS or Operator");

		if (permissions.isOperatorCorporate()) {
			sql.addWhere("op.id = " + permissions.getAccountId());
		} else {
			List<State> states = stateDAO.findByCSR(permissions.getUserId());
			List<Country> countries = countryDAO.findByCSR(permissions.getUserId());
			String where = "";
			if (states.size() > 0 || countries.size() > 0) {
				if (states.size() > 0) {
					List<String> state = new ArrayList<String>();
					for (State s : states) {
						state.add(s.getIsoCode());
					}
					where = "(cr.state IN (" + Strings.implodeForDB(state, ",")+")";
					if(countries.size() > 0)
						where += " OR "; 
				}
				if (countries.size() > 0) {
					List<String> country = new ArrayList<String>();
					for (Country c : countries) {
						country.add(c.getIsoCode());
					}
					where += "cr.country IN (" + Strings.implodeForDB(country, ",")+")";
				}
				where += ")";
				sql.addWhere(where);
			}
			else {
				List<AccountUser> accountUsers = accountUserDAO.findByUser(permissions.getUserId());
				if(accountUsers.size() > 0) {
					List<Integer> accUsers = new ArrayList<Integer>();
					for(AccountUser accountUser : accountUsers) {
						accUsers.add(accountUser.getAccount().getId());
					}
					sql.addWhere("op.id IN (" + Strings.implode(accUsers, ",")+")");
				}	
			}
			sql.addWhere("cr.handledBy = 'PICS'");
			sql.addWhere("cr.open = 1");
		}

		sql.setFromTable("contractor_registration_request cr");
		sql.addJoin("JOIN accounts op ON op.id = cr.requestedByID");
		sql.addJoin("LEFT JOIN users u ON u.id = cr.requestedByUserID");
		sql.addJoin("LEFT JOIN users uc ON uc.id = cr.lastContactedBy");
		sql.addJoin("LEFT JOIN accounts con ON con.id = cr.conID");

		sql.addField("cr.id");
		sql.addField("cr.name");
		sql.addField("op.name AS RequestedBy");
		sql.addField("u.name AS RequestedUser");
		sql.addField("cr.deadline");
		sql.addField("uc.name AS ContactedBy");
		sql.addField("cr.lastContactDate");
		sql.addField("cr.contactCount");
		sql.addField("cr.matchCount");
		sql.addField("cr.handledBy");
		sql.addField("con.id AS conID");
		sql.addField("con.name AS contractorName");
		
		sql.addOrderBy("cr.deadline, cr.name");
		this.run(sql);

		return SUCCESS;
	}
}
