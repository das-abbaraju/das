package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AuditCalendar extends PicsActionSupport {
	private ContractorAuditDAO contractorAuditDAO;

	private JSONArray json;
	private Date start;
	private Date end;

	public AuditCalendar(ContractorAuditDAO contractorAuditDAO) {
		this.contractorAuditDAO = contractorAuditDAO;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equals("audits")) {
				json = new JSONArray();
				List<ContractorAudit> audits = contractorAuditDAO.findScheduledAudits(0, start, end);
				for (ContractorAudit audit : audits) {
					JSONObject o = new JSONObject();
					o.put("id", audit.getId());
					o.put("title", audit.getContractorAccount().getName() + " (" + audit.getAuditor().getName() + ")");
					o.put("start", formatDate(audit.getScheduledDate(), "MM/dd/yyyy HH:mm"));
					o.put("allDay", false);
					o.put("url", "ScheduleAudit.action?auditID=" + audit.getId());
					if (!audit.isConductedOnsite())
						o.put("className", "cal-webcam");
					json.add(o);
				}

				return SUCCESS;
			}
		}

		return SUCCESS;
	}

	public JSONArray getJson() {
		return json;
	}

	public void setJson(JSONArray json) {
		this.json = json;
	}

	public void setStart(String start) {
		this.start = DateBean.parseDate(start);
	}

	public void setEnd(String end) {
		this.end = DateBean.parseDate(end);
	}
}
