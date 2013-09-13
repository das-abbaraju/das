package com.picsauditing.actions.contractors;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ContractorBadge extends ContractorActionSupport {
	public static int MEMBERSHIP_TAG_QUESTION = 11219;

	@Override
	public String execute() throws Exception {
		if (contractor == null) {
			findContractor();
		}

		id = contractor.getId();
		account = contractor;
		setSubHeading("PICS Membership Tag");

		return contractorBadgeToggle();
	}

	private String contractorBadgeToggle() {
		FeatureToggle featureToggle = SpringUtils.getBean("FeatureToggle");
		if (featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_BADGE)) {
			return SUCCESS;
		} else {
			return "failed";
		}
	}

	public String save() throws Exception {
		AuditData data = new AuditData();
		AuditQuestion membershipQuestion = dao.find(AuditQuestion.class, MEMBERSHIP_TAG_QUESTION);
		data.setQuestion(membershipQuestion);
		data.setAuditColumns(permissions);
		data.setAnswer(DateBean.toDBFormat(new Date()));
		data.setAudit(findPQF());

		dao.save(data);

		return setUrlForRedirect("ContractorBadge.action?contractor=" + contractor.getId());
	}

	public boolean isTaskCompleted() {
		for (AuditData data : findPQF().getData()) {
			if (data.getQuestion().getId() == MEMBERSHIP_TAG_QUESTION) {
				return true;
			}
		}

		return false;
	}

	public String getHash() {
		byte[] base64Encoded = Base64.encodeBase64(getContractorIdNameHash().getBytes());

		return new String(base64Encoded);
	}

	private ContractorAudit findPQF() {
		for (ContractorAudit pqf : contractor.getAudits()) {
			if (pqf.getAuditType().isPicsPqf())
				return pqf;
		}

		return null;
	}

	private String getContractorIdNameHash() {
		return String.format("%d:%s", contractor.getId(), contractor.getName());
	}
}