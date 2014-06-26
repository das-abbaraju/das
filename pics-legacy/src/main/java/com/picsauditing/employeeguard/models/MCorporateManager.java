package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;

import java.util.*;

public class MCorporateManager extends MModelManager{

	private Map<Integer,MCorporate> lookup = new HashMap<>();

	public static Set<MCorporate> newCollection() {
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

	public MCorporate fetchModel(int accountId) {
		return lookup.get(accountId);
	}

	public MCorporate attachWithModel(int accountId, AccountModel accountModel) {

		if (lookup.get(accountId) == null) {
			lookup.put(accountId, new MCorporate(accountId, accountModel));
		}

		return lookup.get(accountId);
	}

	private void addEntityToMap(int accountId, AccountModel accountModel){
		entityMap.put(accountId, accountModel);
	}

	public MCorporate copySite(int accountId, AccountModel accountModel) throws ReqdInfoMissingException {
		MCorporate mCorporate = this.fetchModel(accountId);
		if(mCorporate!=null){
			return mCorporate;
		}

		addEntityToMap(accountId, accountModel);
		MCorporate model = this.attachWithModel(accountId, accountModel);

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

	public static class MCorporate extends MBaseModel {

		@Expose
		private Set<MSkillsManager.MSkill> reqdSkills;

		private Integer accountId;
		private AccountModel accountModel;

		public MCorporate() {
		}

		public MCorporate(Integer accountId, AccountModel accountModel) {
			this.accountId = accountId;
			this.accountModel = accountModel;
		}

		public MCorporate copyId(){
			id=accountModel.getId();
			return this;
		}

		public MCorporate copyName(){
			name=accountModel.getName();
			return this;
		}

		public MCorporate attachSkills() throws ReqdInfoMissingException {
			List<AccountSkill> skills = MModels.fetchCorporateManager().fetchReqdSkills(accountId);
			this.reqdSkills = MModels.fetchSkillsManager().copySkills(skills);
			return this;
		}

		//-- Getters/Setters
		public Set<MSkillsManager.MSkill> getReqdSkills() {
			return reqdSkills;
		}


	}
}
