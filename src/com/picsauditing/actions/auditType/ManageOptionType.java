package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOptionType;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ManageOptionType extends PicsActionSupport implements Preparable {
	protected AuditQuestionDAO auditQuestionDAO;
	private AuditQuestion question;
	private AuditOptionType type = new AuditOptionType();

	public ManageOptionType(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@Override
	public void prepare() throws Exception {
		int typeID = getParameter("typeID");
		if (typeID > 0)
			type = auditQuestionDAO.findOptionType(typeID);

		int questionID = getParameter("questionID");
		if (questionID > 0)
			question = auditQuestionDAO.find(questionID);
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String save() throws Exception {
		if (type.getName() == null || type.getName().toString().isEmpty())
			addActionError("Missing name");

		type.setAuditColumns(permissions);
		auditQuestionDAO.save(type);

		return SUCCESS;
	}

	public String delete() throws Exception {
		// TODO Check which questions/answers use this option type!
		return SUCCESS;
	}
	
	public String editAjax() throws Exception {
		return "edit";
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public AuditOptionType getType() {
		return type;
	}

	public void setType(AuditOptionType type) {
		this.type = type;
	}

	// Get all
	public List<AuditOptionType> getAll() {
		return auditQuestionDAO.getAllOptionTypes();
	}

	// Breadcrumbs
	public AuditType getAuditType() {
		if (question != null)
			return question.getAuditType();

		return null;
	}

	public AuditCategory getCategory() {
		if (question != null)
			return question.getCategory();

		return null;
	}
}
