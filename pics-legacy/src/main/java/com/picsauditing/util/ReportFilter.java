package com.picsauditing.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;

@SuppressWarnings("serial")
public class ReportFilter extends TranslationActionSupport implements Serializable {

	protected static TranslationService translationService = TranslationServiceFactory.getTranslationService();

	protected String destinationAction = Strings.EMPTY_STRING;
	protected boolean ajax = false;
	protected boolean allowCollapsed = true;
	protected boolean allowMailMerge = false;
	protected boolean allowMailReport = false;
	protected String customAPI;
	protected boolean showAnyOperator = false;
	protected int showSelfPerformedTrade = 2;

	public boolean isAllowMailMerge() {
		return allowMailMerge;
	}

	public void setAllowMailMerge(boolean allowMailMerge) {
		this.allowMailMerge = allowMailMerge;
	}

	public boolean isAllowMailReport() {
		return allowMailReport;
	}

	public void setAllowMailReport(boolean allowMailReport) {
		this.allowMailReport = allowMailReport;
	}

	public boolean isAllowCollapsed() {
		return allowCollapsed;
	}

	public void setAllowCollapsed(boolean allowCollapsed) {
		this.allowCollapsed = allowCollapsed;
	}

	public String getDestinationAction() {
		if (destinationAction.equals("")) {
			return ServletActionContext.getActionMapping().getName();
		}
		return destinationAction;
	}

	public void setDestinationAction(String destinationAction) {
		this.destinationAction = destinationAction;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public void setPermissions(Permissions permissions) {

	}

	public Map<String, String> getYearList() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (int i = DateBean.getCurrentYear(); i >= 2001; i--) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		return map;
	}

	public String getCustomAPI() {
		return customAPI;
	}

	public void setCustomAPI(String customAPI) {
		this.customAPI = customAPI;
	}

	public boolean isShowAnyOperator() {
		return showAnyOperator;
	}

	public void setShowAnyOperator(boolean showAnyOperator) {
		this.showAnyOperator = showAnyOperator;
	}

	public int getShowSelfPerformedTrade() {
		return showSelfPerformedTrade;
	}

	public void setShowSelfPerformedTrade(int showSelfPerformedTrade) {
		this.showSelfPerformedTrade = showSelfPerformedTrade;
	}
}