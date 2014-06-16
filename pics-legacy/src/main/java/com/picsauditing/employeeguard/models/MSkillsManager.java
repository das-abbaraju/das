package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.picsauditing.employeeguard.entities.AccountSkill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MSkillsManager {
	private Map<Integer,MSkill> lookup = new HashMap<>();

	public static Set<MSkill> newSkillCollection(){
		return new HashSet<MSkill>();
	}

	public MSkill fetchModel(int id){
		return lookup.get(id);
	}

	public MSkill attachWithModel(AccountSkill skill){
		int id = skill.getId();

		if(lookup.get(id)==null){
			lookup.put(id, new MSkill(skill));
		}

		return lookup.get(id);
	}

	public static class MSkill{
		@Expose
		private int id;
		@Expose
		private String name;
		@Expose
		@SerializedName("skill_type")
		private String skillType;
		@Expose
		@SerializedName("rule_type")
		private String ruleType;
		@Expose
		@SerializedName("interval_type")
		private String intervalType;
		@Expose
		private String description;

		private AccountSkill skill;

		public MSkill(AccountSkill skill) {
			this.skill = skill;
		}

		public MSkill copyId(){
			id=skill.getId();
			return this;
		}

		public MSkill copyName(){
			name=skill.getName();
			return this;
		}

		public MSkill copyDescription(){
			description=skill.getDescription();
			return this;
		}
/*
		public MSkill copySkillType(){
			skillType=skill.getSkillType().toString();
			return this;
		}

		public MSkill copySkillType(){
			skillType=skill.getSkillType().toString();
			return this;
		}

		public MSkill copySkillType(){
			skillType=skill.getSkillType().toString();
			return this;
		}*/

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getSkillType() {
			return skillType;
		}

		public String getRuleType() {
			return ruleType;
		}

		public String getIntervalType() {
			return intervalType;
		}

		public String getDescription() {
			return description;
		}
	}
}
