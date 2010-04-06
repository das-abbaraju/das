package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AuditCalendar extends PicsActionSupport {
	private ContractorAuditDAO contractorAuditDAO;

	private Date start;
	private Date end;

	public Map<String, Integer> auditorCount = new HashMap<String, Integer>();

	public AuditCalendar(ContractorAuditDAO contractorAuditDAO) {
		this.contractorAuditDAO = contractorAuditDAO;
	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equals("audits")) {
				json = new JSONObject();
				JSONArray events = new JSONArray();
				List<ContractorAudit> audits = contractorAuditDAO.findScheduledAudits(0, start, end, permissions);
				auditorCount.put("Total", 0);
				for (ContractorAudit audit : audits) {
					if (auditorCount.get(audit.getAuditor().getName()) == null)
						auditorCount.put(audit.getAuditor().getName(), 0);
					auditorCount.put(audit.getAuditor().getName(), auditorCount.get(audit.getAuditor().getName()) + 1);
					auditorCount.put("Total", auditorCount.get("Total") + 1);
					JSONObject o = new JSONObject();
					o.put("id", audit.getId());
					o.put("title", audit.getContractorAccount().getName() + " (" + audit.getAuditor().getName() + ")");
					o.put("start", formatDate(audit.getScheduledDate(), "MM/dd/yyyy HH:mm"));
					o.put("allDay", false);
					if (!permissions.isOperatorCorporate())
						o.put("url", "ScheduleAudit.action?auditID=" + audit.getId());
					if (!audit.isConductedOnsite())
						o.put("className", "cal-webcam");
					events.add(o);
				}

				json.put("events", events);
				json.put("auditorCount", auditorCount);

				return SUCCESS;
			}
		}

		return SUCCESS;
	}

	public void setStart(String start) {
		this.start = DateBean.parseDate(start);
	}

	public void setEnd(String end) {
		this.end = DateBean.parseDate(end);
	}
}
