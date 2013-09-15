package com.picsauditing.mail;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.search.SelectFilterDate;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterEmail;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportEmailError extends ReportActionSupport {
	@Autowired
	protected EmailQueueDAO emailQueueDAO;
	
	protected EmailQueue preview;
	protected int id;
	
	protected SelectSQL sql = new SelectSQL("email_queue q");
	protected ReportFilterEmail filter = new ReportFilterEmail();

	protected void buildQuery() {
		orderByDefault = "q.creationDate DESC";
		sql.addJoin("LEFT JOIN accounts a ON a.id = q.conID");
		sql.addJoin("LEFT JOIN email_template t ON t.id = q.templateID");
		
		sql.addField("q.emailID");
		sql.addField("q.status");
		sql.addField("q.fromAddress");
		sql.addField("q.toAddresses");
		sql.addField("q.ccAddresses");
		sql.addField("q.conID");
		sql.addField("q.subject");
		sql.addField("q.priority");
		sql.addField("DATE_FORMAT(q.creationDate, '%b %d, %Y %l:%i:%s %p') as created");
		sql.addField("a.name");
		sql.addField("t.templateName");
		sql.addWhere("q.creationDate > DATE_SUB(CURDATE(), INTERVAL 1 WEEK)");
		sql.addWhere("q.status = 'Error'");
		sql.addOrderBy(getOrderBy());
		
		addFilterToSQL();
	}

	@RequiredPermission(value=OpPerms.EmailQueue)
	public String execute() throws Exception {
		report.setLimit(50);
		getFilter().setPermissions(permissions);
		buildQuery();
		run(sql);
		
		return SUCCESS;
	}
	
	@RequiredPermission(value=OpPerms.EmailQueue)
	public String previewAjax() {
		preview = emailQueueDAO.find(id);
		return "preview";
	}
	
	@RequiredPermission(value=OpPerms.EmailQueue, type=OpType.Delete)
	public String delete() {
		emailQueueDAO.remove(id);
		return BLANK;
	}
	
	public void addFilterToSQL() {
		ReportFilterEmail f = getFilter();
		
		String statusList = Strings.implodeForDB(f.getStatus(), ",");
		if (filterOn(statusList)) {
			sql.addWhere("q.status IN (" + statusList + ")");
			setFiltered(true);
		}
		
		String templateNameList = Strings.implodeForDB(f.getTemplateName(), ",");
		if (filterOn(templateNameList)) {
			sql.addWhere("t.templateName IN (" + templateNameList + ")");
			setFiltered(true);
		}
		
		if (filterOn(f.getAccountName(), ReportFilterAccount.getDefaultName())) {
			sql.addWhere("a.name LIKE '%" + f.getAccountName() + "%'");
			setFiltered(true);
		}
		
		if (filterOn(f.getSentDateStart())) {
			report.addFilter(new SelectFilterDate("sentDate1", "q.sentDate >= '?'", 
					DateBean.format(f.getSentDateStart(), "M/d/yy")));
			setFiltered(true);
		}

		if (filterOn(f.getSentDateEnd())) {
			report.addFilter(new SelectFilterDate("sentDate2", "q.sentDate <= '?'", 
					DateBean.format(f.getSentDateEnd(), "M/d/yy")));
			setFiltered(true);
		}
		
		if (filterOn(f.getToAddress(), ReportFilterEmail.getDefaultToAddress())) {
			sql.addWhere("q.toAddresses LIKE '%" + f.getToAddress() + "%'");
			setFiltered(true);
		}
		
		if (filterOn(f.getCustomAPI())) {
			sql.addWhere(f.getCustomAPI());
			setFiltered(true);
		}
	}

	public EmailQueue getPreview() {
		return preview;
	}

	public void setPreview(EmailQueue preview) {
		this.preview = preview;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public ReportFilterEmail getFilter() {
		return filter;
	}
}