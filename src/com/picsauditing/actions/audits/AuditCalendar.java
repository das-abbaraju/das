package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.PicsDateFormat;

@SuppressWarnings("serial")
public class AuditCalendar extends PicsActionSupport {
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;

	private Date start;
	private Date end;

	public Map<String, Integer> auditorCount = new HashMap<String, Integer>();


	@SuppressWarnings("unchecked")
	public String audits() {
		json = new JSONObject();
		JSONArray events = new JSONArray();
		List<ContractorAudit> audits = contractorAuditDAO.findScheduledAudits(0, start, end, permissions);
		int totalCount = 0;
		
		for (ContractorAudit audit : audits) {
			if (auditorCount.get(audit.getAuditor().getName()) == null)
				auditorCount.put(audit.getAuditor().getName(), 0);
			auditorCount.put(audit.getAuditor().getName(), auditorCount.get(audit.getAuditor().getName()) + 1);
			totalCount++;
			JSONObject o = new JSONObject();
			o.put("id", audit.getId());
			o.put("title", audit.getContractorAccount().getName() + " (" + audit.getAuditor().getName() + ")");
			o.put("start", formatDate(audit.getScheduledDate(), PicsDateFormat.Datetime));
			o.put("allDay", false);
			if (!permissions.isOperatorCorporate())
				o.put("url", "ScheduleAudit.action?auditID=" + audit.getId());
			if (audit.isConductedOnsite())
				o.put("className", "cal-onsite");
			else if (audit.getContractorAccount().getWebcam() != null)
				o.put("className", "cal-webcam");
			events.add(o);
		}
		
		auditorCount.put(getText("JS.AuditCalendar.Total"), totalCount);
		json.put("events", events);
		json.put("auditorCount", auditorCount);

		return SUCCESS;
	}

	public void setStart(String start) {
		this.start = DateBean.parseDate(start);
	}

	public void setEnd(String end) {
		this.end = DateBean.parseDate(end);
	}
}
