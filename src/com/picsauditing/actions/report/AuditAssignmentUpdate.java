package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditAssignmentUpdate extends PicsActionSupport implements Preparable, ParameterAware, ServletRequestAware {

	protected ContractorAudit contractorAudit = null;
	protected User auditor = null;
	protected User origAuditor = null;

	protected ContractorAuditDAO dao = null;
	protected UserDAO userDao = null;
	protected EmailBuilder emailBuilder;

	protected Map parameters = null;
	protected Date origScheduledDate = null;
	protected String origLocation = null;

	protected HttpServletRequest request;

	public AuditAssignmentUpdate(ContractorAuditDAO dao, UserDAO userDao, EmailBuilder emailBuilder) {
		this.dao = dao;
		this.userDao = userDao;
		this.emailBuilder = emailBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		if(auditor.getId() == 0) {
			return SUCCESS;
		}
		// TODO check to see if auditor already has audit scheduled for this
		// date

		/*
		 * String returnStr = ""; if (!"".equals(auditDate)) { String Query =
		 * "SELECT * FROM blockedDates WHERE
		 * blockedDate='"+DateBean.toDBFormat(auditDate) +"';"; DBReady();
		 * ResultSet SQLResult = SQLStatement.executeQuery(Query); if
		 * (SQLResult.next()) { returnStr = "<b>"+SQLResult.getString("description")+"</b>
		 * is scheduled on "+auditDate; if
		 * (!"0".equals(SQLResult.getString("startHour"))) returnStr +=" from "+
		 * SQLResult.getString("startHour")+SQLResult.getString("startAmPm")+ "
		 * to "+ SQLResult.getString("endHour")+
		 * SQLResult.getString("endAmPm")+"."; } else if (!"".equals(auditor_id) &&
		 * !"0".equals(auditor_id)) { SQLResult.close(); Query = "SELECT
		 * contractor_info.id AS con_id, auditHour, auditAmPm, accounts.name AS
		 * name, "+ "a2.name AS auditor_name FROM contractor_info INNER JOIN
		 * accounts ON contractor_info.id=accounts.id "+ "LEFT OUTER JOIN users
		 * a2 ON contractor_info.auditor_id=a2.id WHERE auditDate='"+
		 * DateBean.toDBFormat(auditDate)+"' AND auditor_id=" + auditor_id;
		 * SQLResult = SQLStatement.executeQuery(Query); if (SQLResult.next())
		 * if (!action_id.equals(SQLResult.getString("con_id"))) returnStr = "<b>"+SQLResult.getString("auditor_name")+"</b>
		 * has an audit scheduled on <b>"+ auditDate+"</b> at
		 * <b>"+SQLResult.getString("auditHour")+SQLResult.getString("auditAmPm")+ "</b>
		 * with <b>"+SQLResult.getString("name")+"</b>."; else returnStr = "";
		 * else returnStr = ""; SQLResult.close(); } DBClose(); } else returnStr =
		 * ""; return returnStr;
		 * 
		 */
		
		auditor = userDao.find(auditor.getId());

		if (contractorAudit.getAuditType().isScheduled()) {
			if (origAuditor != null && !origAuditor.equals(auditor))
				contractorAudit.setAuditorConfirm(null);

			if ((origScheduledDate != null && !origScheduledDate.equals(contractorAudit.getScheduledDate()))
					|| (origLocation != null && !origLocation.equals(contractorAudit.getAuditLocation()))) {
				contractorAudit.setAuditorConfirm(null);
				contractorAudit.setContractorConfirm(null);
			}
		}
		contractorAudit.setAssignedDate(new Date());
		contractorAudit.setAuditor(auditor);

		if (permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit))
			dao.save(contractorAudit);

		if (contractorAudit.getAssignedDate() != null) {
			output = new SimpleDateFormat("MM/dd/yy hh:mm a").format(contractorAudit.getAssignedDate());
		}
		
		String name = request.getRequestURL().toString();
		String serverName = name.replace(ActionContext.getContext().getName() + ".action", "");

		if (contractorAudit.getAuditType().isScheduled()
				&& contractorAudit.getAuditor() != null
				&& contractorAudit.getScheduledDate() != null) {
			if (contractorAudit.getContractorConfirm() == null) {
				emailBuilder.setTemplate(15);
				emailBuilder.clear();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(contractorAudit);
				emailBuilder.setUser(contractorAudit.getContractorAccount().getUsers().get(0));
				
				String seed = "c" + contractorAudit.getContractorAccount().getId() + "id" + contractorAudit.getId();
				String confirmLink = serverName+"ScheduleAuditUpdate.action?type=c&auditID=" + contractorAudit.getId() + 
					"&key="	+ Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);
				
				EmailQueue email = emailBuilder.build();
				EmailSender.send(email);
			}
			if (contractorAudit.getAuditorConfirm() == null) {
				emailBuilder.setTemplate(14);
				emailBuilder.clear();
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(contractorAudit);
				emailBuilder.setUser(contractorAudit.getAuditor());
				
				String seed = "a" + contractorAudit.getAuditor().getId() + "id" + contractorAudit.getId();
				String confirmLink = serverName+"ScheduleAuditUpdate.action?type=a&auditID=" + contractorAudit.getId() + 
					"&key="	+ Strings.hashUrlSafe(seed);
				emailBuilder.addToken("confirmLink", confirmLink);
				
				EmailQueue email = emailBuilder.build();
				email.setCcAddresses(null);
				EmailSender.send(email);
			}
		
			NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
			Note note = new Note();
			note.setAccount(contractorAudit.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setSummary("Audit Schedule updated");
			note.setNoteCategory(NoteCategory.Audits);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
		}
		return SUCCESS;
	}

	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) parameters.get("contractorAudit.id");

		if (ids != null && ids.length > 0) {
			int auditId = Integer.parseInt(ids[0]);
			contractorAudit = dao.find(auditId);
			origAuditor = contractorAudit.getAuditor();
			origScheduledDate = contractorAudit.getScheduledDate();
			origLocation = contractorAudit.getAuditLocation();
		}
	}

	public ContractorAudit getContractorAudit() {
		return contractorAudit;
	}

	public void setContractorAudit(ContractorAudit contractorAudit) {
		this.contractorAudit = contractorAudit;
	}

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}

	public User getAuditor() {
		return auditor;
	}

	public void setAuditor(User auditor) {
		this.auditor = auditor;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}
