package com.picsauditing.actions.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ParameterAware;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;

public class AuditAssignmentUpdate extends PicsActionSupport implements Preparable, ParameterAware {

	protected ContractorAudit contractorAudit = null;
	protected User auditor = null;
	protected User origAuditor = null;
	
	protected ContractorAuditDAO dao = null;
	protected UserDAO userDao = null;

	protected Map parameters = null;
	
	
	
	public AuditAssignmentUpdate( ContractorAuditDAO dao, UserDAO userDao )
	{
		this.dao = dao;
		this.userDao = userDao;
	}
	
	
	
	public String execute() throws Exception 
	{
		// TODO check to see if auditor already has audit scheduled for this date
		
		/*
			String returnStr = "";
			if (!"".equals(auditDate)) {
				String Query = "SELECT * FROM blockedDates WHERE blockedDate='"+DateBean.toDBFormat(auditDate) +"';";
				DBReady();
				ResultSet SQLResult = SQLStatement.executeQuery(Query);
				if 	(SQLResult.next()) {
					returnStr =   "<b>"+SQLResult.getString("description")+"</b> is scheduled on "+auditDate;
					if (!"0".equals(SQLResult.getString("startHour"))) 
						returnStr +=" from "+ SQLResult.getString("startHour")+SQLResult.getString("startAmPm")+
						" to "+ SQLResult.getString("endHour")+ SQLResult.getString("endAmPm")+".";
				} else if (!"".equals(auditor_id) && !"0".equals(auditor_id)) {
					SQLResult.close();
					Query = "SELECT contractor_info.id AS con_id, auditHour, auditAmPm, accounts.name AS name, "+
					"a2.name AS auditor_name FROM contractor_info INNER JOIN accounts ON contractor_info.id=accounts.id "+
					"LEFT OUTER JOIN users a2 ON contractor_info.auditor_id=a2.id WHERE auditDate='"+
					DateBean.toDBFormat(auditDate)+"' AND auditor_id=" + auditor_id;
					SQLResult = SQLStatement.executeQuery(Query);
					if 	(SQLResult.next()) 
						if (!action_id.equals(SQLResult.getString("con_id")))
							returnStr = "<b>"+SQLResult.getString("auditor_name")+"</b> has an audit scheduled on <b>"+
							auditDate+"</b> at <b>"+SQLResult.getString("auditHour")+SQLResult.getString("auditAmPm")+
							"</b> with <b>"+SQLResult.getString("name")+"</b>.";
						else
							returnStr = "";
					else
						returnStr = "";
					SQLResult.close();
				}
				DBClose();
			} else
				returnStr = "";
			return returnStr;

		 */

		auditor = userDao.find(auditor.getId());
		
		if( auditor != null )
		{
			if( origAuditor == null || ( origAuditor != null && ( origAuditor.getId() != auditor.getId() ) ) )
			{
				contractorAudit.setAssignedDate(new Date());
			}
		}
		else
		{
			contractorAudit.setAssignedDate(null);
		}
		
		contractorAudit.setAuditor(auditor);
		
		dao.save(contractorAudit);

		if( contractorAudit.getAssignedDate() != null )
		{
			setMessage(new SimpleDateFormat("MM/dd/yy hh:mm a").format(contractorAudit.getAssignedDate()));
		}

		return SUCCESS;
	}

	
	@Override
	public void prepare() throws Exception {
		
		String[] ids = (String[]) parameters.get("contractorAudit.id");
		
		if( ids != null && ids.length > 0 )
		{
			int auditId = Integer.parseInt(ids[0]);
			contractorAudit = dao.find( auditId );
			origAuditor = contractorAudit.getAuditor();
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
	
}

