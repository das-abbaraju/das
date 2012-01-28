package com.picsauditing.actions.auditType;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.i18n.RequiredLanguagesSupport;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public abstract class ManageOptionComponent extends RequiredLanguagesSupport {
	@Autowired
	protected AuditOptionValueDAO auditOptionValueDAO;

	protected AuditOptionGroup group;
	protected AuditQuestion question;

	@Override
	@RequiredPermission(value = OpPerms.ManageAudits)
	public String execute() throws Exception {
		return SUCCESS;
	}

	public abstract String save() throws Exception;

	public abstract String delete() throws Exception;

	public abstract String editAjax() throws Exception;

	public AuditOptionGroup getGroup() {
		return group;
	}

	public void setGroup(AuditOptionGroup group) {
		this.group = group;
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}
}
