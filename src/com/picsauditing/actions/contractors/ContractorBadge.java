package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.math.NumberUtils;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.BetaPool;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;

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
		Map<String, String> toggles = permissions.getToggles();
		BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(NumberUtils.toInt(toggles.get("Toggle.Badge"), 0));

		if (BetaPool.isUserBetaTester(permissions, betaPool))
			return SUCCESS;
		else
			return "failed";
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
			if (pqf.getAuditType().isPqf())
				return pqf;
		}

		return null;
	}

	private String getContractorIdNameHash() {
		return String.format("%d:%s", contractor.getId(), contractor.getName());
	}
}