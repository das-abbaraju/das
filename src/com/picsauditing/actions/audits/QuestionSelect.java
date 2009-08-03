package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCatOperatorDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCatOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class QuestionSelect extends PicsActionSupport {
	private String questionName;
	Set<AuditQuestion> questions;
	protected AuditQuestionDAO auditQuestionDAO = null;
	protected AuditCatOperatorDAO auditCatOperatorDAO = null;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected List<AuditCatOperator> auditCatOperatorList = null;

	public QuestionSelect(AuditQuestionDAO auditQuestionDAO, AuditCatOperatorDAO auditCatOperatorDAO,
			OperatorAccountDAO operatorAccountDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
		this.auditCatOperatorDAO = auditCatOperatorDAO;
		this.operatorAccountDAO = operatorAccountDAO;
	}

	public String execute() throws Exception {
		String where = "question LIKE '%" + Utilities.escapeQuotes(questionName) + "%' AND t.isVisible = 'Yes'";
		loadPermissions();
		if (permissions.isOperator() || permissions.isCorporate()) {
			where += " AND subCategory.category.auditType.id IN (" + Strings.implode(permissions.getCanSeeAudit(), ",")
					+ ")";
		}
		questions = new TreeSet<AuditQuestion>();
		List<AuditQuestion> questionList = auditQuestionDAO.findWhere(where);
		for (AuditQuestion q : questionList) {
			if (q.getAuditType().isPqf() && !permissions.seesAllContractors()) {
				for (AuditCatOperator auditCatOperator : getAuditCatOperatorList()) {
					if (q.getSubCategory().getCategory() == auditCatOperator.getCategory()) {
						questions.add(q);
					}
				}
			} else {
				questions.add(q);
			}
		}
		return SUCCESS;
	}

	public List<AuditCatOperator> getAuditCatOperatorList() {
		if (auditCatOperatorList == null) {
			auditCatOperatorList = new ArrayList<AuditCatOperator>();
			if (permissions.isOperator() || permissions.isCorporate()) {
				OperatorAccount opAccount = operatorAccountDAO.find(permissions.getAccountId());
				Set<Integer> operator = new HashSet<Integer>();
				operator.add(opAccount.getInheritAuditCategories().getId());
				if (permissions.isCorporate()) {
					for (Facility facility : opAccount.getOperatorFacilities()) {
						operator.add(facility.getOperator().getInheritAuditCategories().getId());
					}
				}
				int[] operatorIds = new int[operator.size()];
				int index = 0;
				for (int i : operator) {
					operatorIds[index] = i;
					index++;
				}
				auditCatOperatorList = auditCatOperatorDAO.find(operatorIds, null);
			}
		}
		return auditCatOperatorList;
	}

	public String getQuestionName() {
		return questionName;
	}

	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}

	public Set<AuditQuestion> getQuestions() {
		return questions;
	}
}