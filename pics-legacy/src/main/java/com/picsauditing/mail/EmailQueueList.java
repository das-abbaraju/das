package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.NoRightsException;
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
public class EmailQueueList extends ReportActionSupport {
	@Autowired
	protected EmailQueueDAO emailQueueDAO;

	protected List<EmailQueue> emails = null;
	protected List<EmailQueue> emailsInQueue = new ArrayList<EmailQueue>();
	protected EmailQueue preview;
	protected int id;

	protected SelectSQL sql = new SelectSQL("email_queue q");
	protected ReportFilterEmail filter = new ReportFilterEmail();

	protected void buildQuery() {
		orderByDefault = "q.priority DESC, q.emailID";
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

		if (permissions.isOperatorCorporate())
			sql.addWhere("q.createdBy = " + permissions.getUserId());

		sql.addOrderBy(getOrderBy());

		addFilterToSQL();
	}

	@RequiredPermission(value = OpPerms.EmailQueue)
	public String execute() throws Exception {
		report.setLimit(50);
		getFilter().setPermissions(permissions);
		buildQuery();
		run(sql);

		if (data.size() > 0)
			emailsInQueue = emailQueueDAO.getPendingEmails("(t.priority > " + data.get(0).get("priority")
					+ " OR (t.priority = " + data.get(0).get("priority") + " AND t.id < " + data.get(0).get("emailID")
					+ "))", 50);
		return SUCCESS;
	}

	public String previewAjax() throws NoRightsException {
		preview = emailQueueDAO.find(id);

		if (permissions.isOperatorCorporate()) {
			permissions.tryPermission(OpPerms.EmailQueue);
			return "preview";
		}

		if (permissions.isPicsEmployee()) {
			return "preview";
		}

		if (permissions.isContractor()) {
			boolean toContainsEmail = preview.getToAddresses().contains(permissions.getEmail());
			boolean bccContainsEmail = !Strings.isEmpty(preview.getBccAddresses())
					&& preview.getBccAddresses().contains(permissions.getEmail());
			boolean ccContainsEmail = !Strings.isEmpty(preview.getCcAddresses())
					&& preview.getCcAddresses().contains(permissions.getEmail());

			if (toContainsEmail || bccContainsEmail || ccContainsEmail) {
				return "preview";
			}
		}

		throw new NoRightsException(OpPerms.EmailQueue, OpType.View);
	}

	@RequiredPermission(value = OpPerms.EmailQueue, type = OpType.Delete)
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
			report.addFilter(new SelectFilterDate("sentDate1", "q.sentDate >= '?'", DateBean.format(
					f.getSentDateStart(), "M/d/yy")));
			setFiltered(true);
		}

		if (filterOn(f.getSentDateEnd())) {
			report.addFilter(new SelectFilterDate("sentDate2", "q.sentDate <= '?'", DateBean.format(f.getSentDateEnd(),
					"M/d/yy")));
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

	public List<EmailQueue> getEmails() {
		return emails;
	}

	public List<EmailQueue> getEmailsInQueue() {
		return emailsInQueue;
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
