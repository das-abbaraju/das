package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.forms.employee.SkillDocumentForm;
import com.picsauditing.employeeguard.forms.employee.SkillInfo;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.util.Strings;

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

    // TODO: Change to enum value for front-end
    private String getProof(SkillInfo skillInfo, ProfileDocument profileDocument) {
        if (skillInfo.getSkillType().isCertification() && profileDocument != null) {
            return profileDocument.getFileName();
        } else if (skillInfo.getSkillType().isTraining() && Strings.isNotEmpty(skillInfo.getEndDate())) {
            return "I certify that I have meet all requirements.";
        }

        return "None";
    }

}
