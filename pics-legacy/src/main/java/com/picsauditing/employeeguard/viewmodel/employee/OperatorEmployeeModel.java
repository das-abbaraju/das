package com.picsauditing.employeeguard.viewmodel.employee;

import com.google.gson.Gson;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.viewmodel.IdNameTitleModel;
import com.picsauditing.employeeguard.viewmodel.RoleModel;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorEmployeeSkillModel;
import com.picsauditing.jpa.entities.JSONable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;

public class OperatorEmployeeModel implements JSONable {
	private final int id;
	private final String name;
	private final String image;
	private final List<IdNameTitleModel> companies;
	private final SkillStatus overallStatus;
	private final List<ProjectDetailModel> projects;
	private final List<RoleModel> roles;
	private final List<OperatorEmployeeSkillModel> skills;

	public OperatorEmployeeModel(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.image = builder.image;
		this.companies = Utilities.unmodifiableList(builder.companies);
		this.overallStatus = builder.overallStatus;
		this.projects = Utilities.unmodifiableList(builder.projects);
		this.roles = Utilities.unmodifiableList(builder.roles);
		this.skills = Utilities.unmodifiableList(builder.skills);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<IdNameTitleModel> getCompanies() {
		return companies;
	}

	public SkillStatus getOverallStatus() {
		return overallStatus;
	}

	public List<ProjectDetailModel> getProjects() {
		return projects;
	}

	public List<RoleModel> getRoles() {
		return roles;
	}

	public List<OperatorEmployeeSkillModel> getSkills() {
		return skills;
	}

	@Override
	public JSONObject toJSON(boolean full) {
		try {
			return (JSONObject) new JSONParser().parse(new Gson().toJson(this));
		} catch (Exception e) {
			// whatever
		}

		return new JSONObject();
	}

	@Override
	public void fromJSON(JSONObject o) {

	}

	public static class Builder {
		private int id;
		private String name;
		private String image;
		private List<IdNameTitleModel> companies;
		private SkillStatus overallStatus;
		private List<ProjectDetailModel> projects;
		private List<RoleModel> roles;
		private List<OperatorEmployeeSkillModel> skills;

		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder image(String image) {
			this.image = image;
			return this;
		}

		public Builder companies(List<IdNameTitleModel> companies) {
			this.companies = companies;
			return this;
		}

		public Builder overallStatus(SkillStatus overallStatus) {
			this.overallStatus = overallStatus;
			return this;
		}

		public Builder projects(List<ProjectDetailModel> projects) {
			this.projects = projects;
			return this;
		}

		public Builder roles(List<RoleModel> roles) {
			this.roles = roles;
			return this;
		}

		public Builder skills(List<OperatorEmployeeSkillModel> skills) {
			this.skills = skills;
			return this;
		}

		public OperatorEmployeeModel build() {
			return new OperatorEmployeeModel(this);
		}
	}
}
