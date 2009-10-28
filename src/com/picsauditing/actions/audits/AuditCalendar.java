package com.picsauditing.actions.audits;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AuditCalendar extends PicsActionSupport implements Preparable {
	private ContractorAuditDAO contractorAuditDAO;

	public JSONArray json;
	private Date start;
	private Date end;

	// Harvey Staal
	// Jesse Cota
	// Mina Mina
	// Rick McGee
	private List<Integer> auditors = Arrays.asList(935, 927, 1029, 9615);

	public AuditCalendar(ContractorAuditDAO contractorAuditDAO) {
		this.contractorAuditDAO = contractorAuditDAO;
	}

	@Override
	public void prepare() throws Exception {

	}

	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equals("audits")) {
				json = new JSONArray();
				for (Integer auditorID : auditors) {
					List<ContractorAudit> audits = contractorAuditDAO.findScheduledAudits(auditorID, start, end);
					for (ContractorAudit audit : audits) {
						JSONObject o = new JSONObject();
						o.put("id", audit.getId());
						o.put("title", audit.getContractorAccount().getName() + " (" + audit.getAuditor().getName()
								+ ")");
						SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
						format.setTimeZone(getUser().getTimezoneObject());
						o.put("start", format.format(audit.getScheduledDate()));
						o.put("allDay", false);
						o.put("url", "ScheduleAudit.action?auditID=" + audit.getId());
						if (!audit.isConductedOnsite())
							o.put("className", "cal-webcam");
						json.add(o);
					}
				}

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
