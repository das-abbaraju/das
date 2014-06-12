package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MProjectsManager {
	private Map<Integer,MProject> lookup = new HashMap<>();

	public static class MProject{

		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private MAssignmentsSkillsRoles assignmentsSkillsRoles;
		@Expose
		private Set<MContractorManager.MContractor> companies;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public MAssignmentsSkillsRoles getAssignmentsSkillsRoles() {
			return assignmentsSkillsRoles;
		}

		public void setAssignmentsSkillsRoles(MAssignmentsSkillsRoles assignmentsSkillsRoles) {
			this.assignmentsSkillsRoles = assignmentsSkillsRoles;
		}

		public Set<MContractorManager.MContractor> getCompanies() {
			return companies;
		}

		public void setCompanies(Set<MContractorManager.MContractor> companies) {
			this.companies = companies;
		}
	}


}
