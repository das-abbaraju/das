package com.picsauditing.employeeguard.models;

import com.google.gson.annotations.Expose;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.util.SpringUtils;

import java.util.*;

public class MContractorManager  extends MModelManager{
	private Map<Integer,MContractor> lookup = new HashMap<>();

	public static Set<MContractor> newCollection() {
		return new HashSet<>();
	}

	private Map<Integer, AccountModel> entityMap = new HashMap<>();
	private Map<Integer,List<AccountSkill>> reqdSkillsMap = new HashMap<>();

	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}
	public MContractorManager() {
		operations = new SupportedOperations();
	}

	public List<AccountSkill> fetchReqdSkills(int accountId){
		return reqdSkillsMap.get(accountId);
	}

	public void attachReqdSkills(int accountId, List<AccountSkill> reqdSkills){
		reqdSkillsMap.put(accountId, reqdSkills);
	}

	public MContractor fetchModel(int accountId) {
		return lookup.get(accountId);
	}

	public MContractor attachWithModel(int accountId, AccountModel accountModel) {

		if (lookup.get(accountId) == null) {
			lookup.put(accountId, new MContractor(accountId, accountModel));
		}

		return lookup.get(accountId);
	}

	private void addEntityToMap(int accountId, AccountModel accountModel){
		entityMap.put(accountId, accountModel);
	}

	public MContractor copyContractor(int accountId) throws ReqdInfoMissingException {
		MContractor mContractor = this.fetchModel(accountId);
		if(mContractor!=null)
			return mContractor;

		AccountService accountService = SpringUtils.getBean("AccountService");
		AccountModel accountModel = accountService.getAccountById(accountId);

		return copyContractor(accountId, accountModel);
	}

	public MContractor copyContractor(int accountId, AccountModel accountModel) throws ReqdInfoMissingException {
		MContractor mContractor = this.fetchModel(accountId);
		if(mContractor!=null){
			return mContractor;
		}

		addEntityToMap(accountId, accountModel);
		MContractor model = this.attachWithModel(accountId, accountModel);

		for(MOperations mOperation: mOperations){

			if(mOperation.equals(MOperations.COPY_ID)){
				model.copyId();
			}
			else if(mOperation.equals(MOperations.COPY_NAME)){
				model.copyName();
			}
			else if(mOperation.equals(MOperations.ATTACH_SKILLS)){
				model.attachSkills();
			}

		}

		return model;
	}

	public class SupportedOperations implements MCopyId, MCopyName, MAttachSkills {

		@Override
		public SupportedOperations copyId() {
			mOperations.add(MOperations.COPY_ID);
			return this;
		}

		@Override
		public SupportedOperations copyName() {
			mOperations.add(MOperations.COPY_NAME);
			return this;
		}


		@Override
		public SupportedOperations attachSkills() {
			mOperations.add(MOperations.ATTACH_SKILLS);
			return this;
		}
	}


	public static class MContractor  extends MBaseModel implements MCopyId, MCopyName, MAttachSkills  {
		@Expose
		private Set<MContractorSkillsManager.MContractorSkill> reqdSkills;

		private Integer accountId;
		private AccountModel accountModel;

		public MContractor() {
		}

		public MContractor(Integer accountId, AccountModel accountModel) {
			this.accountId = accountId;
			this.accountModel = accountModel;
		}

		@Override
		public MContractor copyId(){
			id=accountModel.getId();
			return this;
		}

		@Override
		public MContractor copyName(){
			name=accountModel.getName();
			return this;
		}

		@Override
		public MContractor attachSkills() throws ReqdInfoMissingException {
			List<AccountSkill> skills = MModels.fetchContractorManager().fetchReqdSkills(accountId);
			this.reqdSkills = MModels.fetchContractorSkillManager().copySkills(skills);
			return this;
		}

		//-- Getters/Setters


		public Set<MContractorSkillsManager.MContractorSkill> getReqdSkills() {
			return reqdSkills;
		}

		public Integer getAccountId() {
			return accountId;
		}
	}
}