package com.picsauditing.actions.auditType;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditQuestionOptionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.WorkFlowDAO;
import com.picsauditing.jpa.entities.AuditQuestionOption;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ManageQuestionOptions extends ManageQuestion implements Preparable {
	protected List<AuditQuestionOption> options;
	protected AuditQuestionOptionDAO auditQuestionOptionDAO;

	public ManageQuestionOptions(AuditTypeDAO auditTypeDao, AuditCategoryDAO auditCategoryDao,
			AuditQuestionDAO auditQuestionDao, AuditDataDAO auditDataDAO, AuditDecisionTableDAO ruleDAO,
			WorkFlowDAO wfDAO, AuditQuestionOptionDAO auditQuestionOptionDAO) {
		super(auditTypeDao, auditCategoryDao, auditQuestionDao, auditDataDAO, ruleDAO, wfDAO);
		this.auditQuestionOptionDAO = auditQuestionOptionDAO;
	}

	@Override
	public void prepare() throws Exception {
		loadPermissions();
		id = this.getParameter("id");
		if (id > 0) {
			this.load(id);
			options = (List<AuditQuestionOption>) auditQuestionOptionDAO.findWhere(AuditQuestionOption.class,
					" t.auditQuestion.id = " + id, 0, "t.number");
		}
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		throw new Exception("Need to implement saving AuditQuestionOptions");
		
//		if ("save".equalsIgnoreCase(button)) {
//			for (AuditQuestionOption aQOption : options) {
//				if (aQOption != null && !Strings.isEmpty(aQOption.getOptionName())) {
//					aQOption.setAuditColumns(permissions);
//					auditQuestionOptionDAO.save(aQOption);
//				}
//			}
//			options = (List<AuditQuestionOption>) auditQuestionOptionDAO.findWhere(AuditQuestionOption.class,
//					" t.auditQuestion.id = " + id, 0, "t.number");
//		}
		// return SUCCESS;
	}

	public List<AuditQuestionOption> getOptions() {
		return options;
	}

	public void setOptions(List<AuditQuestionOption> options) {
		this.options = options;
	}
}
