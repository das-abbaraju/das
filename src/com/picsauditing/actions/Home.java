package com.picsauditing.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import org.apache.struts2.ServletActionContext;

import com.ibm.icu.util.Calendar;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.WidgetUserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Widget;
import com.picsauditing.jpa.entities.WidgetUser;

@SuppressWarnings("serial")
public class Home extends ContractorActionSupport {

	private Map<Integer, List<Widget>> columns = new TreeMap<Integer, List<Widget>>();

	private WidgetUserDAO dao;

	public Home(WidgetUserDAO dao, ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
		this.dao = dao;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isContractor()) {
			findContractor();
			if (!contractor.getStatus().isActiveDemo()) {
				addActionError("Your account is inactive. You can't access this page");
				return BLANK;
			}
		} else
			permissions.tryPermission(OpPerms.Dashboard);

		List<WidgetUser> widgetsToShowForUser = dao.findByUser(permissions);

		for (WidgetUser widgetUser : widgetsToShowForUser) {
			Widget widget = widgetUser.getWidget();

			if (widget.getRequiredPermission() != null) {
				if (!permissions.hasPermission(widget.getRequiredPermission()))
					continue;
			}

			// Set custom parameters from the user
			widget.setExpanded(widgetUser.isExpanded());
			widget.setCustomConfig(widgetUser.getCustomConfig());

			// Get the column we want to add the widget to
			int columnID = widgetUser.getColumn();
			List<Widget> column = columns.get(columnID);

			if (column == null) {
				column = new ArrayList<Widget>();
				columns.put(columnID, column);
			}

			column.add(widget);
		}

		return SUCCESS;
	}

	public Map<Integer, List<Widget>> getColumns() {
		return columns;
	}

	public void setColumns(Map<Integer, List<Widget>> columns) {
		this.columns = columns;
	}

	public int getColumnWidth() {
		return Math.round(90 / columns.size());
	}

	public boolean isShowRebranding() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.MARCH);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.YEAR, 2010);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		User u = getUser();

		if (u.getCreationDate() == null || u.getCreationDate().before(cal.getTime())) {

			Cookie[] cookies = ServletActionContext.getRequest().getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("PICS_NEW".equals(cookie.getName())) {
						if ("1".equals(cookie.getValue()))
							return false;
					}
				}
			}

			// The account has not seen the notice
			Cookie PICS_NEW = new Cookie("PICS_NEW", "1");
			PICS_NEW.setMaxAge(3600 * 24 * 30);
			ServletActionContext.getResponse().addCookie(PICS_NEW);
			return true;
		}

		return false;
	}
}
