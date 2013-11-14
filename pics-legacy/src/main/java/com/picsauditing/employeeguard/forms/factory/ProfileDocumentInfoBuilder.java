package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.forms.employee.ProfileDocumentInfo;
import com.picsauditing.employeeguard.services.calculator.DocumentStatusCalculator;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileDocumentInfoBuilder {
	public List<ProfileDocumentInfo> buildList(final List<ProfileDocument> documents) throws Exception {
		if (CollectionUtils.isEmpty(documents)) {
			return Collections.emptyList();
		}

		DocumentStatusCalculator calculator = new DocumentStatusCalculator();

		List<ProfileDocumentInfo> profileDocumentInfos = new ArrayList<>();
		for (ProfileDocument document : documents) {
			ProfileDocumentInfo profileDocumentInfo = new ProfileDocumentInfo();
			profileDocumentInfo.setId(document.getId());
			profileDocumentInfo.setName(document.getName());
			profileDocumentInfo.setAdded(document.getStartDate());
			profileDocumentInfo.setExpires(document.getEndDate());
			profileDocumentInfo.setDoesNotExpire(document.isDoesNotExpire());
			profileDocumentInfo.setStatus(calculator.calculate(document.getEndDate()));

			profileDocumentInfos.add(profileDocumentInfo);
		}

		return profileDocumentInfos;
	}


}
