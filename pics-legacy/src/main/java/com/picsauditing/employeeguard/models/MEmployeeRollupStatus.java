package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.services.status.SkillStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MEmployeeRollupStatus {

	@Expose
	Integer completed=0;
	@Expose
	Integer expiring=0;
	@Expose
	Integer expired=0;

	private Set<MContractorEmployeeManager.MContractorEmployee> completedSet = new HashSet<>();
	private Set<MContractorEmployeeManager.MContractorEmployee> expiringSet = new HashSet<>();
	private Set<MContractorEmployeeManager.MContractorEmployee> expiredSet = new HashSet<>();

	Map<SkillStatus, Set> skillStatusSetMap = new HashMap<SkillStatus, Set>(){{
		put(SkillStatus.Completed, completedSet); put(SkillStatus.Expiring, expiringSet); put(SkillStatus.Expired, expiredSet);
	}};

	public Integer getCompleted() {
		return completed;
	}
	public Integer getExpiring() {
		return expiring;
	}
	public Integer getExpired() {
		return expired;
	}


	public Set<MContractorEmployeeManager.MContractorEmployee> getCompletedSet() {
		return completedSet;
	}
	public Set<MContractorEmployeeManager.MContractorEmployee> getExpiringSet() {
		return expiringSet;
	}
	public Set<MContractorEmployeeManager.MContractorEmployee> getExpiredSet() {
		return expiredSet;
	}

	public void addToCompleted(MContractorEmployeeManager.MContractorEmployee mEmployee){
		completedSet.add(mEmployee);
	}

	public void addToExpiring(MContractorEmployeeManager.MContractorEmployee mEmployee){
		expiringSet.add(mEmployee);
	}

	public void addToExpired(MContractorEmployeeManager.MContractorEmployee mEmployee){
		expiredSet.add(mEmployee);
	}

	public void updateCompletedCount(){
		this.completed = completedSet.size();
	}
	public void updateExpiringCount(){
		this.expiring = expiringSet.size();
	}
	public void updateExpiredCount(){
		this.expired = expiredSet.size();
	}

	public Set<MContractorEmployeeManager.MContractorEmployee> getSkillStatusSet(SkillStatus skillStatus){
		return skillStatusSetMap.get(skillStatus);
	}
}
