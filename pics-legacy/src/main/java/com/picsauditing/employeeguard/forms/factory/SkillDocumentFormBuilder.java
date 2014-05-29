package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.viewmodel.model.SkillInfo;

import java.util.Date;

public class SkillDocumentFormBuilder {

	public SkillDocumentForm build(SkillInfo skillInfo, ProfileDocument profileDocument) {
		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();
		skillDocumentForm.setSkillInfo(skillInfo);
		skillDocumentForm.setProof(getProof(skillInfo, profileDocument));

		if (profileDocument != null) {
			skillDocumentForm.setDocumentId(profileDocument.getId());
		}

		return skillDocumentForm;
	}

	public SkillDocumentForm build(AccountSkillProfile accountSkillProfile) {
		SkillDocumentForm skillDocumentForm = new SkillDocumentForm();

		Date endDate = accountSkillProfile.getEndDate();
		if (accountSkillProfile.getSkill().getSkillType().isTraining() && endDate != null) {
			skillDocumentForm.setVerified(endDate.after(new Date()));
		}

		return skillDocumentForm;
	}

	// TODO: Change to enum value for front-end
	private String getProof(SkillInfo skillInfo, ProfileDocument profileDocument) {
		if (skillInfo.getSkillType() != null && skillInfo.getSkillType().isCertification() && profileDocument != null) {
			return profileDocument.getFileName();
		} else if (skillInfo.getSkillType() != null && skillInfo.getSkillType().isTraining() && skillInfo.getEndDate() != null) {
			return "I certify that I have met all requirements.";
		}

		return "None";
	}
}
