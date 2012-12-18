package com.picsauditing.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multimap;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.FlagColor;

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
	protected boolean showIncludePicsReources = false;
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
	protected boolean includePicsResources = false;
	protected String titleName;

	protected Permissions permissions = null;

	private CountrySubdivisionDAO countrySubdivisionDAOForTests;
	private CountryDAO countryDAOForTests;

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

	public FlagColor[] getFlagStatusList() {
		return FlagColor.values();
	}

	public List<CountrySubdivision> getCountrySubdivisionList() {
		CountrySubdivisionDAO countrySubdivisionDAO = countrySubdivisionDAO();

		List<CountrySubdivision> result;
		if (permissions != null && !Strings.isEmpty(permissions.getCountry())) {
			Set<String> accountCountries = new HashSet<String>();
			accountCountries.add(permissions.getCountry());
			result = countrySubdivisionDAO.findByCountries(accountCountries, false);
		} else {
			result = countrySubdivisionDAO.findAll();
		}

		return result;
	}

	public Multimap<Country, CountrySubdivision> getCountrySubdivisionMap() {
		Multimap<Country, CountrySubdivision> countrySubdivisionMap = null;

		if (permissions != null) {
			CountrySubdivisionDAO countrySubdivisionDAO = countrySubdivisionDAO();

			countrySubdivisionMap = countrySubdivisionDAO.getCountrySubdivisionMap(permissions.getCountry());
		}

		return countrySubdivisionMap;
	}

	public List<Country> getCountryList() {
		CountryDAO countryDAO = countryDAO();
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

	public boolean isShowIncludePicsReources() {
		return showIncludePicsReources;
	}

	public void setShowIncludePicsReources(boolean showIncludePicsReources) {
		this.showIncludePicsReources = showIncludePicsReources;
	}

	public boolean isIncludePicsResources() {
		return includePicsResources;
	}

	public void setIncludePicsResources(boolean includePicsResources) {
		this.includePicsResources = includePicsResources;
	}

	public String getStartsWith() {
		return startsWith;
	}

	public void setStartsWith(String startsWith) {
		this.startsWith = startsWith;
	}

	public String getAccountName() {
		if (Strings.isEmpty(accountName)) {
			accountName = getDefaultName();
		}

		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCity() {
		if (Strings.isEmpty(city)) {
			city = getDefaultCity();
		}

		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		// IE9 always sets the location, even if it doesn't know it, whereas the
		// other browsers don't try to set it. So, to be consisitent, we'll
		// ignore a String[] with one empty string.
		if (location.length > 1 || (location.length == 1 &&  location[0].length() > 0)) {
			this.location = location;
		}
	}

	public String getZip() {
		if (Strings.isEmpty(zip)) {
			zip = getDefaultZip();
		}

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
		if (!viewRequestedStatus()) {
			int size = AccountStatus.values().length - 1;
			AccountStatus[] statusList = new AccountStatus[size];
			int index = 0;

			for (AccountStatus status : AccountStatus.values()) {
				if (status != AccountStatus.Requested) {
					statusList[index] = status;
					index++;
				}
			}

			return statusList;
		} else {
			return AccountStatus.values();
		}
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

	private CountrySubdivisionDAO countrySubdivisionDAO() {
		if (countrySubdivisionDAOForTests == null) {
			return (CountrySubdivisionDAO) SpringUtils.getBean("CountrySubdivisionDAO");
		}
		return countrySubdivisionDAOForTests;
	}

	private CountryDAO countryDAO() {
		if (countryDAOForTests == null) {
			return (CountryDAO) SpringUtils.getBean("CountryDAO");
		}
		return countryDAOForTests;
	}

	private boolean viewRequestedStatus() {
		if (permissions != null) {
			if (permissions.isOperatorCorporate() && permissions.hasPermission(OpPerms.RequestNewContractor)) {
				return true;
			}

			if (permissions.isPicsEmployee()) {
				return true;
			}
		}

		return false;
	}
}
