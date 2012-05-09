package com.picsauditing.actions.report;

import com.picsauditing.search.SelectUser;
import com.picsauditing.util.ReportFilterUser;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportUser2 extends ReportActionSupport {

	protected SelectUser sql = new SelectUser();
	private ReportFilterUser filter = new ReportFilterUser();

	@Override
	public String execute() throws Exception {

		sql.addJoin("JOIN accounts a ON a.id = u.accountID");
		sql.addField("a.name as accountName");
		sql.addWhere("u.isActive = 'Yes'");
		sql.addWhere("u.isGroup = 'No'");
		sql.addOrderBy("a.name, u.name");
		addFilterToSQL();

		this.report.setLimit(25);
		this.run(sql);

		return SUCCESS;
	}

	private void addFilterToSQL() {
		ReportFilterUser f = getFilter();

		String search = Strings.escapeQuotes(f.getSearch());
		if (search.length() >= 3) {			
			StringBuffer where = new StringBuffer();
			String phone = Strings.stripPhoneNumber(f.getSearch());
			if (phone.length() > 0)
				where.append("u.phoneIndex LIKE '").append(search).append("%'");

			if (search.contains("@")) {
				where.append(where.length() > 0 ? " OR " : "");
				where.append("u.email LIKE '").append(search).append("%'");
			}
			if (isNumeric(search)){
				where.append(" OR u.id LIKE '").append(search).append("%'");
				where.append(" OR u.id = ").append(search);
			}

			if (where.length() == 0) {
				where.append("u.name LIKE '%").append(search).append("%'");
				where.append("OR a.name LIKE '%").append(search).append("%'");
				where.append("OR u.email LIKE '%").append(search).append("%'");
				where.append("OR u.id LIKE '").append(search).append("%'");
			}			
			sql.addWhere(where.toString());
		} else {
			if (filterOn(f.getStartsWith()))
				sql.addWhere("u.name LIKE '" + Strings.escapeQuotes(f.getStartsWith()) + "%'");

			if (filterOn(f.getContactName()))
				sql.addWhere("u.name LIKE '%" + Strings.escapeQuotes(f.getContactName()) + "%'");

			String phone = Strings.stripPhoneNumber(f.getPhoneNumber());
			if (filterOn(phone))
				sql.addWhere("u.phoneIndex LIKE '" + phone + "%'");

			if (filterOn(f.getEmailAddress()))
				sql.addWhere("u.email LIKE '%" + Strings.escapeQuotes(f.getEmailAddress()) + "%'");

			if (filterOn(f.getUserName()))
				sql.addWhere("u.username LIKE '%" + Strings.escapeQuotes(f.getUserName()) + "%'");

			if (filterOn(f.getCompanyName()))
				sql.addWhere("a.name LIKE '%" + Strings.escapeQuotes(f.getCompanyName()) + "%'");
			
			if (isNumeric(f.getSearch())) {
				if (filterOn(f.getSearch())) {
					sql.addWhere("u.id LIKE '" + f.getSearch() + "%'");
				}
			}			
		}
	}

	public boolean isNumeric(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public ReportFilterUser getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterUser filter) {
		this.filter = filter;
	}

}
