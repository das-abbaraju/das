package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;

public class VerifyAudit extends AuditActionSupport {
	private static final long serialVersionUID = -4976847934505647430L;
	private List<AuditData> pqfQuestions = null;

	public VerifyAudit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		return SUCCESS;
	}

	public ArrayList<String> getOshaProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Contradicting Data");
		list.add("Missing 300");
		list.add("Missing 300a");
		list.add("Incomplete");
		list.add("Incorrect Form");
		list.add("Incorrect Year");
		return list;
	}

	public ArrayList<String> getEmrProblems() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("Need EMR");
		list.add("Need Loss Run");
		list.add("Not Insurance Issued");
		list.add("Incorrect Upload");
		list.add("Incorrect Year");
		return list;
	}

	public List<AuditData> getPqfQuestions() {
		if (pqfQuestions == null) {
			pqfQuestions = auditDataDao.findCustomPQFVerifications(conAudit.getId());
		}

		return pqfQuestions;
	}

	public void setPqfQuestions(List<AuditData> pqfQuestions) {
		this.pqfQuestions = pqfQuestions;
	}

	public Comparator<AuditData> getDataComparator() {
		return new Comparator<AuditData>() {
			@Override
			public int compare(AuditData o1, AuditData o2) {
				if( o1 == null ) return -1;
				
				return o1.compareTo(o2);
			}
		};
	}

	
	
	public boolean isShowApproveButton() {
		if (conAudit.getAuditType().isPqf() && getPqfQuestions().size() == 0)
			return true;
		if (conAudit.getPercentVerified() == 100)
			return true;

		return false;
	}
	
	public boolean isShowOsha() {
		AuditData auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 2064);
		if (auditData != null && "Yes".equals(auditData.getAnswer())) 
				return true;

		return false;
	}
}
