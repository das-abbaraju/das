package com.picsauditing.util;

import java.util.ArrayList;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.Industry;

public class ReportFilterAccount extends ReportFilter {
	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_ZIP = "- Zip -";
	public static final String DEFAULT_CITY = "- City -";
	public static final String DEFAULT_VISIBLE = "- Visible -";

	// /////// Filter Visibility /////////////
	protected boolean showAccountName = true;
	protected boolean showIndustry = true;
	protected boolean showAddress = true;
	protected boolean showVisible = false;
	protected boolean showStatus = false;
	protected boolean showPrimaryInformation = false;
	protected boolean showTradeInformation = false;

	// /////// Parameter Values /////////////////
	protected String startsWith;
	protected String accountName = DEFAULT_NAME;
	protected Industry[] industry;
	protected String city = DEFAULT_CITY;
	protected String[] state;
	protected String[] country;
	protected String zip = DEFAULT_ZIP;
	protected String visible;
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
	public Industry[] getIndustryList() {
		return Industry.values();
	}

	public String[] getVisibleOptions() {
		return new String[] { DEFAULT_VISIBLE, "Y", "N" };
	}

	public ArrayList<String> getFlagStatusList() throws Exception {
		return FlagColor.getValuesWithDefault();
	}

	public boolean isShowAccountName() {
		return showAccountName;
	}

	public void setShowAccountName(boolean showAccountName) {
		this.showAccountName = showAccountName;
	}

	public boolean isShowIndustry() {
		return showIndustry;
	}

	public void setShowIndustry(boolean showIndustry) {
		this.showIndustry = showIndustry;
	}

	public boolean isShowAddress() {
		return showAddress;
	}

	public void setShowAddress(boolean showAddress) {
		this.showAddress = showAddress;
	}

	public boolean isShowVisible() {
		return showVisible;
	}

	public void setShowVisible(boolean showVisible) {
		this.showVisible = showVisible;
	}

	public boolean isShowStatus() {
		return showStatus;
	}

	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
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

	public Industry[] getIndustry() {
		return industry;
	}

	public void setIndustry(Industry[] industry) {
		this.industry = industry;
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

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
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
