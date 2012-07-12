package com.picsauditing.actions;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAppProperty extends PicsActionSupport {
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	public static final String SAVE = "save";
	public static final String SAVE_ADD = "save_add";

	private AppProperty property = new AppProperty();
	private String newProperty;
	private String newValue;
	private List<AppProperty> all;

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	@Override
	public String execute() throws Exception {
		return list();
	}

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String create() throws Exception {
		String action = "create";

		if ("POST".equals(getRequest().getMethod())) {
			if (Strings.isEmpty(newProperty)) {
				addActionError("Missing property field");
			}

			if (Strings.isEmpty(newValue)) {
				addActionError("Missing value field");
			}

			if (hasActionErrors()) {
				return action;
			}

			property.setProperty(newProperty);
			property.setValue(newValue);
			appPropertyDAO.save(property);

			updateActionToRedirectDependingOnParameters(action);
		}

		return action;
	}

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String edit() throws Exception {
		String action = "edit";

		if ("POST".equals(getRequest().getMethod())) {
			if (Strings.isEmpty(newValue)) {
				addActionError("Missing value field");
			}

			if (hasActionErrors()) {
				return action;
			}

			property.setValue(newValue);
			appPropertyDAO.save(property);

			updateActionToRedirectDependingOnParameters(action);
		}

		return action;
	}

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String list() throws Exception {
		return "list";
	}

	public List<AppProperty> getAll() {
		if (all == null) {
			all = appPropertyDAO.findAll();
		}

		return all;
	}

	public String getSafe(String property) {
		return property.replace(".", "\\\\.");
	}

	public AppProperty getProperty() {
		return property;
	}

	public void setProperty(AppProperty property) {
		this.property = property;
	}

	public String getNewProperty() {
		return newProperty;
	}

	public void setNewProperty(String newProperty) {
		this.newProperty = newProperty;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	private void updateActionToRedirectDependingOnParameters(String action) throws Exception {
		if (getRequest().getParameter(SAVE) != null) {
			action = setUrlForRedirect("ManageAppProperty!edit.action?property=" + property.getProperty());
		}

		if (getRequest().getParameter(SAVE_ADD) != null) {
			action = setUrlForRedirect("ManageAppProperty!create.action");
		}
	}
}