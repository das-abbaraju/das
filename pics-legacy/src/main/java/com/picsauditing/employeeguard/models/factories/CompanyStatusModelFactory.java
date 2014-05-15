package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.models.IdNameComposite;
import com.picsauditing.employeeguard.models.SkillStatusInfo;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.*;

public class CompanyStatusModelFactory {

	public Set<CompanyStatusModel> create(final Map<AccountModel, SkillStatus> accountStatusMap,
										  final Map<AccountModel, Set<SkillStatusModel>> accountSkillsMap,
										  final Map<AccountModel, Set<CompanyProjectModelFactory.CompanyProjectModel>> accountProjectsMap) {

		List<CompanyStatusModel> companyStatusModels = new ArrayList<>();
		for (AccountModel accountModel : accountStatusMap.keySet()) {
			companyStatusModels.add(create(accountModel, accountStatusMap.get(accountModel),
					accountSkillsMap.get(accountModel), accountProjectsMap.get(accountModel)));
		}

		Collections.sort(companyStatusModels);

		return new LinkedHashSet<>(companyStatusModels);
	}

	public CompanyStatusModel create(final AccountModel accountModel,
									 final SkillStatus status,
									 final Set<SkillStatusModel> skills,
									 final Set<CompanyProjectModelFactory.CompanyProjectModel> projectModels) {

		CompanyStatusModel companyStatusModel = new CompanyStatusModel();

		companyStatusModel.setId(accountModel.getId());
		companyStatusModel.setName(accountModel.getName());
		companyStatusModel.setStatus(status);
		companyStatusModel.setSkills(skills);
		companyStatusModel.setProjects(projectModels);

		return companyStatusModel;
	}

	public class CompanyStatusModel implements IdNameComposite, SkillStatusInfo, Comparable<CompanyStatusModel> {

		private int id;
		private String name;
		private SkillStatus status;
		private Set<SkillStatusModel> skills;
		private Set<CompanyProjectModelFactory.CompanyProjectModel> projects;

		@Override
		public int getId() {
			return id;
		}

		@Override
		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public SkillStatus getStatus() {
			return status;
		}

		@Override
		public void setStatus(SkillStatus status) {
			this.status = status;
		}

		public Set<SkillStatusModel> getSkills() {
			return skills;
		}

		public void setSkills(Set<SkillStatusModel> skills) {
			this.skills = skills;
		}

		public Set<CompanyProjectModelFactory.CompanyProjectModel> getProjects() {
			return projects;
		}

		public void setProjects(Set<CompanyProjectModelFactory.CompanyProjectModel> projects) {
			this.projects = projects;
		}

		@Override
		public int compareTo(CompanyStatusModel that) {
			return this.name.compareToIgnoreCase(that.name);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CompanyStatusModel that = (CompanyStatusModel) o;

			if (id != that.id) return false;
			if (name != null ? !name.equals(that.name) : that.name != null) return false;
			if (status != that.status) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (name != null ? name.hashCode() : 0);
			result = 31 * result + (status != null ? status.hashCode() : 0);
			return result;
		}
	}
}
