package com.picsauditing.actions.report;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ReportUserPermissionMatrix extends ReportActionSupport {
	private int accountID;
	private List<User> users;
	private Set<OpPerms> perms;
	private UserDAO userDAO;

	private TableDisplay tableDisplay;

	public ReportUserPermissionMatrix(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		PicsLogger.start("ReportUserPermissionMatrix");
		if (accountID == 0 || !permissions.hasPermission(OpPerms.AllOperators))
			accountID = permissions.getAccountId();

		perms = new TreeSet<OpPerms>();
		users = userDAO.findByAccountID(accountID, "Yes", "");
		for (User user : users) {
			PicsLogger.log("User: " + user.getId() + user.getName());
			for (UserAccess access : user.getPermissions()) {
				PicsLogger.log("  perm " + access.getOpPerm() + " V:" + access.getViewFlag() + " E:"
						+ access.getEditFlag() + " D:" + access.getDeleteFlag() + " G:" + access.getGrantFlag());
				perms.add(access.getOpPerm());
			}
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}

	public List<User> getUsers() {
		return users;
	}

	public Set<OpPerms> getPerms() {
		return perms;
	}

	public TableDisplay getTableDisplay() {
		if (tableDisplay == null) {
			tableDisplay = new TableDisplay(users, perms);
		}

		return tableDisplay;
	}

	public class TableDisplay {

		private Set<User> rows = new LinkedHashSet<User>();
		private Set<OpPerms> cols = new LinkedHashSet<OpPerms>();

		private DoubleMap<User, OpPerms, UserAccess> data = new DoubleMap<User, OpPerms, UserAccess>();

		public TableDisplay(Collection<User> uList, Collection<OpPerms> pList) {
			rows.addAll(uList);

			for (User u : uList) {
				for (UserAccess ua : u.getPermissions()) {
					data.put(u, ua.getOpPerm(), ua);
				}
			}

			for (OpPerms p : pList) {
				for (User u : rows) {
					if (get(u, p) != null) {
						cols.add(p);
						break;
					}
				}
			}
		}

		public UserAccess get(User u, OpPerms p) {
			return data.get(u, p);
		}

		public Set<User> getRows() {
			return rows;
		}

		@SuppressWarnings("unchecked")
		public JSONArray getRowsJSON() {
			JSONArray j = new JSONArray();
			for (final User u : rows) {
				j.add(new JSONObject() {
					{
						put("id", u.getId());
						put("name", u.getName());
					}
				});
			}

			return j;
		}

		public Set<OpPerms> getCols() {
			return cols;
		}

		@SuppressWarnings("unchecked")
		public JSONArray getColsJSON() {
			JSONArray j = new JSONArray();
			for (final OpPerms perm : cols) {
				j.add(new JSONObject() {
					{
						put("id", perm.toString());
						put("name", perm.getDescription());
					}
				});
			}

			return j;
		}
	}

}
