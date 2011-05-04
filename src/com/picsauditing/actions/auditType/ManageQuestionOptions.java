package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionOptionDAO;
import com.picsauditing.jpa.entities.AuditOptionType;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOption;

@SuppressWarnings("serial")
public class ManageQuestionOptions extends PicsActionSupport implements Preparable {
	private AuditQuestionDAO auditQuestionDAO;
	private AuditQuestionOptionDAO auditQuestionOptionDAO;

	private AuditOptionType type;
	private AuditQuestion question;
	private AuditQuestionOption option = new AuditQuestionOption();
	private List<AuditQuestionOption> auditQuestionOptions;

	public ManageQuestionOptions(AuditQuestionDAO auditQuestionDAO, AuditQuestionOptionDAO auditQuestionOptionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditQuestionOptionDAO = auditQuestionOptionDAO;
	}

	@Override
	public void prepare() throws Exception {
		int typeID = getParameter("typeID");
		int questionID = getParameter("questionID");
		int optionID = getParameter("optionID");

		if (typeID > 0)
			type = auditQuestionDAO.findOptionType(typeID);
		if (questionID > 0)
			question = auditQuestionDAO.find(questionID);
		if (optionID > 0)
			option = auditQuestionOptionDAO.find(optionID);
	}

	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		return super.execute();
	}

	public String save() throws Exception {
		if (option.getName() == null)
			addActionError("Missing answer");

		if (getActionErrors().size() == 0) {
			option.setType(type);
			option.setAuditColumns(permissions);
			auditQuestionOptionDAO.save(option);
		}

		return SUCCESS;
	}

	public String delete() throws Exception {
		// TODO check which questions have this answer
		return SUCCESS;
	}

	public AuditOptionType getType() {
		return type;
	}

	public void setType(AuditOptionType type) {
		this.type = type;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}

	public AuditQuestionOption getOption() {
		return option;
	}

	public void setOption(AuditQuestionOption option) {
		this.option = option;
	}

	public List<AuditQuestionOption> getAuditQuestionOptions() {
		return auditQuestionOptions;
	}

	public void setAuditQuestionOptions(List<AuditQuestionOption> auditQuestionOptions) {
		this.auditQuestionOptions = auditQuestionOptions;
	}
}
