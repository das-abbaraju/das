package com.picsauditing.employeeguard.models;

import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;

public class MModels {

	public static final String MMODELS = "EG_MModels_Key";

	private MSkillsManager mSkillsManager = new MSkillsManager();
	private MContractorSkillsManager mContractorSkillsManager = new MContractorSkillsManager();
	private MRolesManager mRolesManager = new MRolesManager();
	private MGroupsManager mGroupsManager = new MGroupsManager();
	private MProjectsManager mProjectsManager = new MProjectsManager();
	private MSitesManager mSitesManager = new MSitesManager();

	public static MModels newMModels(){
		MModels mModels = new MModels();
		SessionInfoProvider sessionInfoProvider = SessionInfoProviderFactory.getSessionInfoProvider();
		sessionInfoProvider.getRequest().put(MMODELS, mModels);
		return mModels;
	}

	private MModels() {

	}

	public static MModels fetchmModels(){
		if(SessionInfoProviderFactory.getSessionInfoProvider().getRequest().get(MModels.MMODELS)==null){
			return newMModels();
		}

		return (MModels)SessionInfoProviderFactory.getSessionInfoProvider().getRequest().get(MModels.MMODELS);
	}
	public static MSkillsManager fetchSkillsManager(){
		return fetchmModels().getmSkillsManager();
	}

	public static MRolesManager fetchRolesManager(){
		return fetchmModels().getmRolesManager();
	}

	public static MContractorSkillsManager fetchContractorSkillManager(){
		return fetchmModels().getmContractorSkillsManager();
	}

	public static MGroupsManager fetchContractorGroupsManager(){
		return fetchmModels().getmGroupsManager();
	}

	//-- Getters

	public MSitesManager getmSitesManager() {
		return mSitesManager;
	}

	public MProjectsManager getmProjectsManager() {
		return mProjectsManager;
	}

	public MRolesManager getmRolesManager() {
		return mRolesManager;
	}

	public MSkillsManager getmSkillsManager() {
		return mSkillsManager;
	}

	public MContractorSkillsManager getmContractorSkillsManager() {
		return mContractorSkillsManager;
	}

	public MGroupsManager getmGroupsManager() {
		return mGroupsManager;
	}
}
