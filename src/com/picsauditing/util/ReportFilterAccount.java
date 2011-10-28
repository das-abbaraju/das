package com.picsauditing.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.State;

@SuppressWarnings("serial")
public class ReportFilterAccount extends ReportFilter {
	// /////// Filter Visibility /////////////
	protected boolean showAccountName = true;
	protected boolean showIndustry = true;
	protected boolean showAddress = false;
	protected boolean showStatus = false;
	protected boolean showType = false;
	protected boolean showPrimaryInformation = false;
	protected boolean showTradeInformation = false;
	protected boolean showTitleName = false;

	// /////// Parameter Values /////////////////
	protected String startsWith;
	protected String accountName;
	protected String city;
	protected String[] location;
	protected String zip;
	protected String[] type;
	protected AccountStatus[] status;
	protected boolean primaryInformation = false;
	protected boolean tradeInformation = false;
	protected String titleName;

	protected Permissions permissions = null;

	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		if (permissions.isPicsEmployee()) {
			showStatus = true;
		}
	}

	public String[] getTypeList() {
		return new String[] { cache.getText("global.AssessmentCenter", getLocaleStatic()),
				cache.getText("global.Corporate", getLocaleStatic()),
				cache.getText("global.Operator", getLocaleStatic()) };
	}

	public FlagColor[] getFlagStatusList() throws Exception {
		return FlagColor.values();
	}

	public List<State> getStateList() {
		StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");
		List<State> result;
		if (!Strings.isEmpty(permissions.getCountry())) {
			Set<String> accountCountries = new HashSet<String>();
			accountCountries.add(permissions.getCountry());
			result = stateDAO.findByCountries(accountCountries, false);
		} else
			result = stateDAO.findAll();

		return result;
	}

	public Multimap<Country, State> getStateMap() {
		StateDAO stateDAO = (StateDAO) SpringUtils.getBean("StateDAO");

		Multimap<Country, State> stateMap = stateDAO.getStateMap(permissions.getCountry());

		return stateMap;
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
		if (Strings.isEmpty(accountName))
			accountName = getDefaultName();

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCity() {
		if (Strings.isEmpty(city))
			city = getDefaultCity();

		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String getZip() {
		if (Strings.isEmpty(zip))
			zip = getDefaultZip();

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

	public boolean isShowTitleName() {
		return showTitleName;
	}

	public void setShowTitleName(boolean showTitleName) {
		this.showTitleName = showTitleName;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public Permissions getPermissions() {
		return permissions;
	}

	public static String getDefaultName() {
		return String.format("- %s -", cache.getText("global.CompanyName", getLocaleStatic()));
	}

	public static String getDefaultCity() {
		return String.format("- %s -", cache.getText("global.City", getLocaleStatic()));
	}

	public static String getDefaultZip() {
		return String.format("- %s -", cache.getText("global.ZipPostalCode", getLocaleStatic()));
	}
}
