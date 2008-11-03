package com.picsauditing.mail;

import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.util.ReportFilter;
import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.ReportFilterContractor;

public class WizardSession {
	private Map<String, Object> map;

	public WizardSession(Map<String, Object> map) {
		this.map = map;
	}
	
	public boolean clear() {
		map.remove("mailer_ids");
		map.remove("mailer_list_type");
		map.remove("mailer_template_id");
		return true;
	}

	@SuppressWarnings("unchecked")
	public Set<Integer> getIds() {
		if (map.containsKey("mailer_ids"))
			return (Set<Integer>) map.get("mailer_ids");
		return null;
	}

	public void setIds(Set<Integer> ids) {
		map.put("mailer_ids", ids);
	}

	public ListType getListType() {
		if (map.containsKey("mailer_list_type"))
			return (ListType) map.get("mailer_list_type");
		return null;
	}

	public void setListTypes(ListType type) {
		map.put("mailer_list_type", type);
	}

	public ReportFilterContractor getContractorFilter() {
		String filterName = "filter" + ListType.Contractor;
		if (map.containsKey(filterName))
			return (ReportFilterContractor) map.get(filterName);
		return new ReportFilterContractor();
	}
	
	public ReportFilterAudit getAuditFilter() {
		String filterName = "filter" + ListType.Audit;
		if (map.containsKey(filterName))
			return (ReportFilterAudit) map.get(filterName);
		return new ReportFilterAudit();
	}
	
	public void setFilter(ListType type, ReportFilter filter) {
		map.put("filter"+type.toString(), filter);
	}

	public int getTemplateID() {
		if (map.containsKey("mailer_template_id"))
			return (Integer) map.get("mailer_template_id");
		return 0;
	}
	
	public void setTemplateID(int templateID) {
		map.put("mailer_template_id", templateID);
	}
}
