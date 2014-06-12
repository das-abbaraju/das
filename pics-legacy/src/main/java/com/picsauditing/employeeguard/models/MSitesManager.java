package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSitesManager {

	private Map<Integer,MSite> lookup = new HashMap<>();

	public static class MSite {
		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private MAssignmentsSkillsRoles assignmentsSkillsRoles;
		@Expose
		private Set<MProjectsManager.MProject> projects;


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

		public Set<MProjectsManager.MProject> getProjects() {
			return projects;
		}

		public void setProjects(Set<MProjectsManager.MProject> projects) {
			this.projects = projects;
		}
	}
}
