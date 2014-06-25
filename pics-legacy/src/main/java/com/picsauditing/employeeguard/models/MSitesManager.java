package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

import java.util.*;

public class MSitesManager extends MModelManager{

	private Map<Integer,MSite> lookup = new HashMap<>();

	public static Set<MSite> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, AccountModel> entityMap = new HashMap<>();
	private Map<Integer,List<AccountSkill>> reqdSkillsMap = new HashMap<>();

	public List<AccountSkill> fetchReqdSkills(int accountId){
		return reqdSkillsMap.get(accountId);
	}

	public void attachReqdSkills(int accountId, List<AccountSkill> reqdSkills){
		reqdSkillsMap.put(accountId, reqdSkills);
	}

	public MSite fetchModel(int accountId) {
		return lookup.get(accountId);
	}

	public MSite attachWithModel(int accountId, AccountModel accountModel) {

		if (lookup.get(accountId) == null) {
			lookup.put(accountId, new MSite(accountId, accountModel));
		}

		return lookup.get(accountId);
	}

	private void addEntityToMap(int accountId, AccountModel accountModel){
		entityMap.put(accountId, accountModel);
	}

	public MSite copySite(int accountId, AccountModel accountModel) throws ReqdInfoMissingException {
		MSite mSite = this.fetchModel(accountId);
		if(mSite!=null){
			return mSite;
		}

		addEntityToMap(accountId, accountModel);
		MSite model = this.attachWithModel(accountId, accountModel);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_REQD_SKILLS)){
				model.attachSkills();
			}

		}

		return model;
	}

	public static class MSite extends MBaseModel {


		@Expose
		private Set<MSkillsManager.MSkill> reqdSkills;

		@Expose
		private MAssignments assignments;

		private Integer accountId;
		private AccountModel accountModel;

		public MSite() {
		}

		public MSite(Integer accountId, AccountModel accountModel) {
			this.accountId = accountId;
			this.accountModel = accountModel;
		}

		public MSite copyId(){
			id=accountModel.getId();
			return this;
		}

		public MSite copyName(){
			name=accountModel.getName();
			return this;
		}

		public MSite attachSkills() throws ReqdInfoMissingException {
			List<AccountSkill> skills = MModels.fetchSitesManager().fetchReqdSkills(accountId);
			this.reqdSkills = MModels.fetchSkillsManager().copySkills(skills);
			return this;
		}

		//-- Getters/Setters
		public Set<MSkillsManager.MSkill> getReqdSkills() {
			return reqdSkills;
		}

		public MAssignments getAssignments() {
			return assignments;
		}

		public void setAssignments(MAssignments assignments) {
			this.assignments = assignments;
		}
	}
}
