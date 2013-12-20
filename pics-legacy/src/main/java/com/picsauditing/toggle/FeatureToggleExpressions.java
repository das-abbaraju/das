package com.picsauditing.toggle;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.List;
import java.util.Set;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.AppVersion;

public abstract class FeatureToggleExpressions extends Script {

	public boolean releaseToUserAudienceLevel(BetaPool audienceLevel) {
		return BetaPool.isUserBetaTester(permissions(), audienceLevel);
	}

	public boolean releaseToUserAudienceLevel(int audienceLevel) {
		BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(audienceLevel);
		return BetaPool.isUserBetaTester(permissions(), betaPool);
	}

	public boolean releaseToApplicationAudienceLevel(BetaPool audienceLevel) {
		return releaseToApplicationAudienceLevel(audienceLevel.ordinal());
	}

	public boolean releaseToApplicationAudienceLevel(int audienceLevel) {
		if (audienceLevel == 0) {
			return false;
		}
		try {
			return applicationBetaLevel() >= audienceLevel;
		} catch (FeatureToggleException e) {
			return false;
		}
	}

	public boolean userIsMemberOf(Integer userGroup) {
		if (userGroup == null) {
			return false;
		}
		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}
		for (Integer group : permissions.getAllInheritedGroupIds()) {
			if (userGroup.equals(group)) {
				return true;
			}
		}
		return false;
	}

	public boolean userIsMemberOfAny(List<Integer> userGroups) {
		if (userGroups == null || userGroups.isEmpty()) {
			return false;
		}

		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}

		Set<Integer> groupIds = permissions.getAllInheritedGroupIds();
		for (int groupId : userGroups) {
			if (groupIds.contains(groupId)) {
				return true;
			}
		}
		return false;
	}

	public boolean userIsMemberOfAll(List<Integer> userGroups) {
		if (userGroups == null || userGroups.isEmpty()) {
			return false;
		}

		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}

		Set<Integer> groupIds = permissions.getAllInheritedGroupIds();
		for (int userGroup : userGroups) {
			if (!groupIds.contains(userGroup)) {
				return false;
			}
		}
		
		return true;
	}

	public AppVersion versionOf(String featureModuleOrApp) throws FeatureToggleException {
		if ("PICSORG".equalsIgnoreCase(featureModuleOrApp)) {
			return versionOfPicsOrg();
		} else if ("BPROC".equalsIgnoreCase(featureModuleOrApp)) {
			return versionOfBackProcs();
		} else {
			throw new FeatureToggleException("Unknown feature, module or application for version check");
		}
	}

	public AppVersion versionOfBackProcs() throws FeatureToggleException {
		Binding binding = getBinding();
		AppPropertyDAO appPropertyDAO = (AppPropertyDAO) binding.getVariable("appPropertyDAO");
		String version = appPropertyDAO.getProperty("VERSION.BPROC");
		try {
			return new AppVersion(version);
		} catch (Exception e) {
			throw new FeatureToggleException(e.getMessage());
		}
	}

	public AppVersion versionOfPicsOrg() throws FeatureToggleException {
		try {
			return AppVersion.current;
		} catch (Exception e) {
			throw new FeatureToggleException(e.getMessage());
		}
	}

	public float applicationBetaLevel() throws FeatureToggleException {
		Binding binding = getBinding();
		AppPropertyDAO appPropertyDAO = (AppPropertyDAO) binding.getVariable("appPropertyDAO");
		String version = appPropertyDAO.getProperty(AppProperty.BETA_LEVEL);
		try {
			return Float.parseFloat(version);
		} catch (Exception e) {
			throw new FeatureToggleException(e.getMessage());
		}
	}

	private Permissions permissions() {
		Binding binding = getBinding();
		return (Permissions) binding.getVariable("permissions");
	}
	
	public boolean hasPermission(String opPermsName) throws FeatureToggleException {
	
		OpPerms opPerms;
		try {
			opPerms = OpPerms.valueOf(opPermsName);
		} catch (Exception e) {
			throw new FeatureToggleException("No such permission: "+opPermsName);
		}
		return permissions().has(opPerms);
	}

    public AppVersion appVersion(int major, int minor) throws FeatureToggleException {
        return new AppVersion(major, minor);
    }

    public AppVersion appVersion(int major, int minor, int patch) throws FeatureToggleException {
        return new AppVersion(major, minor, patch);
    }

}
