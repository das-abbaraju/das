package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MRolesManager {
	private Map<Integer,MRole> lookup = new HashMap<>();

	public static class MRole{

		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		private MAssignments assignments;
		@Expose
		Set<MSkillsManager.MSkill> skills;

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

		public Set<MSkillsManager.MSkill> getSkills() {
			return skills;
		}

		public void setSkills(Set<MSkillsManager.MSkill> skills) {
			this.skills = skills;
		}

		public MAssignments getAssignments() {
			return assignments;
		}

		public void setAssignments(MAssignments assignments) {
			this.assignments = assignments;
		}
	}


}
