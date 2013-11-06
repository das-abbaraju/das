package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.forms.employee.CompanySkillInfo;
import com.picsauditing.employeeguard.forms.employee.CompanySkillsForm;
import com.picsauditing.employeeguard.forms.factory.CompanySkillsFormBuilder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CompanySkillsFormBuilderFactory {
	private static CompanySkillsFormBuilder companySkillsFormBuilder = Mockito.mock(CompanySkillsFormBuilder.class);

	public static CompanySkillsFormBuilder getCompanySkillsFormBuilder() {
		Mockito.reset(companySkillsFormBuilder);

		CompanySkillsForm companySkillsForm = new CompanySkillsForm();

		List<CompanySkillInfo> companySkillInfoList = new ArrayList<>();
		CompanySkillInfo companySkillInfo = new CompanySkillInfo();
		companySkillInfoList.add(companySkillInfo);

		companySkillsForm.setCompanySkillInfoList(companySkillInfoList);
		when(companySkillsFormBuilder.build(any(Profile.class))).thenReturn(companySkillsForm);

		return companySkillsFormBuilder;
	}
}
