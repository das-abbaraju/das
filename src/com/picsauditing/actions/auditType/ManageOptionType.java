package com.picsauditing.actions.auditType;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOptionType;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
// TODO Create an abstract super action class ManageOptionComponent that ManageOptionGroup and ManageOptionValue uses
public class ManageOptionType extends PicsActionSupport implements Preparable {
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	private AuditQuestion question;
	// TODO you don't need the new AOT every time (editAjax)
	private AuditOptionType type = new AuditOptionType();

	@Override
	public void prepare() throws Exception {
		// TODO Remove the prepare method and change it to use the xworks setType method "type=1"
		int typeID = getParameter("typeID");
		if (typeID > 0)
			type = auditQuestionDAO.findOptionType(typeID);

		// TODO 
		int questionID = getParameter("questionID");
		if (questionID > 0)
			question = auditQuestionDAO.find(questionID);
	}

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Edit)
	public String save() throws Exception {
		if (type.getName() == null || type.getName().toString().isEmpty())
			addActionError("Missing name");

		type.setAuditColumns(permissions);
		auditQuestionDAO.save(type);

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAudits, type = OpType.Delete)
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
