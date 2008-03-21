package com.picsauditing.actions.operators;

import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.Utilities;

public class FacilitiesGet extends ActionSupport 
{
	protected Facilities facilityUtility = null;
	protected String filter = null;
	protected List<BasicDynaBean> facilities = null; 
	protected boolean shouldIncludePICS = false;
	

	public FacilitiesGet( Facilities facilityUtility )
	{
		this.facilityUtility = facilityUtility;
	}
	
	
	public String execute() throws Exception
	{
		String where = null;
		if (filter != null && filter.length() > 3) {
			where = "a.id IN (SELECT accountID FROM users WHERE name LIKE '%"+Utilities.escapeQuotes(filter)+"%' OR username LIKE '%"+Utilities.escapeQuotes(filter)+"%' OR email LIKE '%"+Utilities.escapeQuotes(filter)+"%')";
		}
		
		facilities = facilityUtility.listAll(where == null ? "" : where);
		
		return SUCCESS;
	}

	public String getFilter() {
		return filter;
	}


	public void setFilter(String filter) {
		this.filter = filter;
	}


	public List<BasicDynaBean> getFacilities() {
		return facilities;
	}


	public void setFacilities(List<BasicDynaBean> facilities) {
		this.facilities = facilities;
	}


	public boolean isShouldIncludePICS() {
		return shouldIncludePICS;
	}


	public void setShouldIncludePICS(boolean shouldIncludePICS) {
		this.shouldIncludePICS = shouldIncludePICS;
	}
	


}
