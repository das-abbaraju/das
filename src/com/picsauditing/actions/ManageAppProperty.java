package com.picsauditing.actions;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageAppProperty extends PicsActionSupport {
	protected AppPropertyDAO appPropertyDAO;
	
	protected String newProperty;
	protected String newValue;
	protected List<AppProperty> all;
	
	public ManageAppProperty(AppPropertyDAO appPropertyDAO) {
		this.appPropertyDAO = appPropertyDAO;
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		tryPermissions(OpPerms.DevelopmentEnvironment);
		
		if (button != null) {
			if (Strings.isEmpty(newProperty))
				addActionError("Missing/empty property field");
			if (Strings.isEmpty(newValue))
				addActionError("Missing/empty value field");
			
			if (getActionErrors().size() > 0)
				return SUCCESS;
			
			if ("Save".equalsIgnoreCase(button)) {
				AppProperty appProp = appPropertyDAO.find(newProperty);
				if (appProp == null)
					appProp = new AppProperty();
				
				appProp.setProperty(newProperty);
				appProp.setValue(newValue);
				appPropertyDAO.save(appProp);
			}
			
			if ("Remove".equalsIgnoreCase(button)) {
				appPropertyDAO.remove(newProperty);
			}
			
			return redirect("ManageAppProperty.action");
		}
		
		return SUCCESS;
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
	
	public List<AppProperty> getAll() {
		if (all == null)
			all = appPropertyDAO.findAll();
		
		return all;
	}
	
	public String getSafe(String property) {
		return property.replace(".", "\\\\.");
	}
}