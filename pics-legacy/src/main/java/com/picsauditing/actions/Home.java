package com.picsauditing.actions;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.WidgetUserDAO;
import com.picsauditing.jpa.entities.Widget;
import com.picsauditing.jpa.entities.WidgetUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class Home extends PicsActionSupport {
	@Autowired
	private WidgetUserDAO wdao;

	private Map<Integer, List<Widget>> columns = new TreeMap<Integer, List<Widget>>();

	public String execute() throws Exception {
		List<WidgetUser> widgetsToShowForUser = Collections.emptyList();

		if (permissions.isContractor()) {
			if (!permissions.getAccountStatus().isActiveOrDemo()) {
				addActionError("Your account is inactive. You can't access this page");
				return BLANK;
			}

			return setUrlForRedirect("ContractorView.action");
		} else if (permissions.isOperatorCorporate() && !permissions.hasPermission(OpPerms.Dashboard)) {
			// Redirect operators/corporate accounts without the dashboard
			// permission to the contractor list
            if (permissions.isUsingVersion7Menus()) {
                return setUrlForRedirect("Report.action?report=100");
            }

			return setUrlForRedirect("ContractorList.action?filter.performedBy=Self%20Performed");
		} else if (permissions.isGeneralContractor()) {
			widgetsToShowForUser = wdao.findForGeneralContractor(permissions);
		} else {
			permissions.tryPermission(OpPerms.Dashboard);
			widgetsToShowForUser = wdao.findByUser(permissions);
		}

		setUpWidgets(widgetsToShowForUser);

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

	private void setUpWidgets(List<WidgetUser> widgetsToShowForUser) {
		List<Widget> usedWidgets = new ArrayList<Widget>();

		for (WidgetUser widgetUser : widgetsToShowForUser) {
			Widget widget = widgetUser.getWidget();

			if (usedWidgets.contains(widget))
				continue;

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
			usedWidgets.add(widget);
		}
	}
}
