package com.picsauditing.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.State;

@SuppressWarnings("serial")
public class ReportFilterAccount extends ReportFilter {
	public static final String DEFAULT_NAME = "- Company Name - ";
	public static final String DEFAULT_ZIP = "- Zip -";
	public static final String DEFAULT_CITY = "- City -";

	// /////// Filter Visibility /////////////
	protected boolean showAccountName = true;
	protected boolean showAddress = true;
	protected boolean showStatus = false;
	protected boolean showType = false;
	protected boolean showPrimaryInformation = false;
	protected boolean showTradeInformation = false;

	// /////// Parameter Values /////////////////
	protected String startsWith;
	protected String accountName = DEFAULT_NAME;
	protected String city = DEFAULT_CITY;
	protected String[] state;
	protected String[] country;
	protected String zip = DEFAULT_ZIP;
	protected String[] type;
	protected AccountStatus[] status;
	protected boolean primaryInformation = false;
	protected boolean tradeInformation = false;

	protected Permissions permissions = null;

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		if (permissions.isPicsEmployee()) {
			showStatus = true;
		}
	}

	// Getters for search lists
	public String[] getTypeList() {
		return new String[] { "Assessment", "Corporate", "Operator" };
	}

	public ArrayList<String> getFlagStatusList() throws Exception {
		return FlagColor.getValuesWithDefault();
	}

	public List<State> getStateList() {
		StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
		List<State> result;
		if (!Strings.isEmpty(permissions.getCountry())) {
			Set<String> accountCountries = new HashSet<String>();
			accountCountries.add(permissions.getCountry());
			result = stateDAO.findByCountries(accountCountries, false);
		}	
		else
			result = stateDAO.findAll();

		return result;
	}

	public List<Country> getCountryList() {
		CountryDAO countryDAO = (CountryDAO) SpringUtils.getBean("CountryDAO");
		return countryDAO.findAll();
	}

	public boolean isShowAccountName() {
		return showAccountName;
	}

	public void setShowAccountName(boolean showAccountName) {
		this.showAccountName = showAccountName;
	}

	public boolean isShowAddress() {
		return showAddress;
	}

	public void setShowAddress(boolean showAddress) {
		this.showAddress = showAddress;
	}

	public boolean isShowStatus() {
		return showStatus;
	}

	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
	}

	public boolean isShowType() {
		return showType;
	}

	public void setShowType(boolean showType) {
		this.showType = showType;
	}

	public boolean isShowPrimaryInformation() {
		return showPrimaryInformation;
	}

	public void setShowPrimaryInformation(boolean showPrimaryInformation) {
		this.showPrimaryInformation = showPrimaryInformation;
	}

	public boolean isShowTradeInformation() {
		return showTradeInformation;
	}

	public void setShowTradeInformation(boolean showTradeInformation) {
		this.showTradeInformation = showTradeInformation;
	}

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String[] getState() {
		return state;
	}

	public void setState(String[] state) {
		this.state = state;
	}

	public String[] getCountry() {
		return country;
	}

	public void setCountry(String[] country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String[] getType() {
		return type;
	}

	public void setType(String[] type) {
		this.type = type;
	}

	public AccountStatus[] getStatus() {
		return status;
	}

	public void setStatus(AccountStatus[] status) {
		this.status = status;
	}

	public AccountStatus[] getStatusList() {
		return AccountStatus.values();
	}

	public boolean isPrimaryInformation() {
		return primaryInformation;
	}

	public void setPrimaryInformation(boolean primaryInformation) {
		this.primaryInformation = primaryInformation;
	}

	public boolean isTradeInformation() {
		return tradeInformation;
	}

	public void setTradeInformation(boolean tradeInformation) {
		this.tradeInformation = tradeInformation;
	}

	public Permissions getPermissions() {
		return permissions;
	}
}
