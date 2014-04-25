package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.models.CompanyModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.util.PhotoUtil;

import java.util.List;
import java.util.Map;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.IMAGE_LINK;
import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.buildUrl;

public class OperatorEmployeeModelFactory {

	public OperatorEmployeeModel build(final Employee employee, final SkillStatus employeeStatus,
									   final ProfileDocument image, final List<CompanyModel> companyModels) {

		OperatorEmployeeModel operatorEmployeeModel = new OperatorEmployeeModel();

		operatorEmployeeModel.setId(employee.getId());
		operatorEmployeeModel.setImage(buildImageFileUrl(image));
		operatorEmployeeModel.setSkillStatus(employeeStatus);
		operatorEmployeeModel.setFirstName(employee.getFirstName());
		operatorEmployeeModel.setLastName(employee.getLastName());
		operatorEmployeeModel.setCompanies(companyModels);

		return operatorEmployeeModel;
	}

	private String buildImageFileUrl(final ProfileDocument image) {
		if (image == null) {
			return PhotoUtil.DEFAULT_PHOTO_FILE;
		}

		return buildUrl(IMAGE_LINK, image.getId());
	}

	public class OperatorEmployeeModel {

		private int id;
		private String image;
		private SkillStatus skillStatus;
		private String firstName;
		private String lastName;
		private List<CompanyModel> companies;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getImage() {
			return image;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public SkillStatus getSkillStatus() {
			return skillStatus;
		}

		public void setSkillStatus(SkillStatus skillStatus) {
			this.skillStatus = skillStatus;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public List<CompanyModel> getCompanies() {
			return companies;
		}

		public void setCompanies(List<CompanyModel> companies) {
			this.companies = companies;
		}
	}
}
