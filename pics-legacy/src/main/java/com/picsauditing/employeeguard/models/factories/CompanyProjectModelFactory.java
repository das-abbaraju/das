package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.IdNameComposite;
import com.picsauditing.employeeguard.models.SkillStatusInfo;
import com.picsauditing.employeeguard.models.SkillStatusModel;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.*;

public class CompanyProjectModelFactory {

	public Set<CompanyProjectModel> create(final Map<Project, SkillStatus> projectStatusMap,
										   final Map<Project, Set<SkillStatusModel>> projectSkillsMap) {

		List<CompanyProjectModel> companyProjectModels = new ArrayList<>();
		for (Project project : projectStatusMap.keySet()) {
			companyProjectModels.add(create(project, projectStatusMap.get(project), projectSkillsMap.get(project)));
		}

		Collections.sort(companyProjectModels);

		return new LinkedHashSet<>(companyProjectModels);
	}

	public CompanyProjectModel create(final Project project, final SkillStatus status,
									  final Set<SkillStatusModel> skillStatusModels) {

		CompanyProjectModel companyProjectModel = new CompanyProjectModel();

		companyProjectModel.setId(project.getId());
		companyProjectModel.setName(project.getName());
		companyProjectModel.setStatus(status);
		companyProjectModel.setSkills(PicsCollectionUtil.sortSet(skillStatusModels));

		return companyProjectModel;
	}

	public class CompanyProjectModel implements IdNameComposite, SkillStatusInfo, Comparable<CompanyProjectModel> {

		private int id;
		private String name;
		private SkillStatus status;
		private Set<SkillStatusModel> skills;

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

		@Override
		public int compareTo(CompanyProjectModel that) {
			return this.name.compareTo(that.name);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CompanyProjectModel that = (CompanyProjectModel) o;

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
