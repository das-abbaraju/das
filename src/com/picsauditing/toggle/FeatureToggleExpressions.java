package com.picsauditing.toggle;

import groovy.lang.Binding;
import groovy.lang.Script;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.BetaPool;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.util.PicsOrganizerVersion;

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

	public boolean userIsMemberOf(String userGroup) {
		if (userGroup == null) {
			return false;
		}
		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}
		for (String group : permissions.getGroupNames()) {
			if (userGroup.equalsIgnoreCase(group)) {
				return true;
			}
		}
		return false;
	}

	public boolean userIsMemberOf(Integer userGroup) {
		if (userGroup == null) {
			return false;
		}
		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}
		for (Integer group : permissions.getGroupIds()) {
			if (userGroup.equals(group)) {
				return true;
			}
		}
		return false;
	}

	public <E> boolean userIsMemberOfAny(List<E> userGroups) {
		if (userGroups == null || userGroups.isEmpty()) {
			return false;
		}

		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}
		Collection<String> groupNames = permissions.getGroupNames();
		Set<Integer> groupIds = permissions.getGroupIds();
		for (Object userGroup : userGroups) {
			if (userGroup instanceof String && groupNames.contains(userGroup)) {
				return true;
			} else if (userGroup instanceof Integer && groupIds.contains(userGroup)) {
				return true;
			}
		}
		return false;
	}

	public <E> boolean userIsMemberOfAll(List<E> userGroups) {
		if (userGroups == null || userGroups.isEmpty()) {
			return false;
		}

		Permissions permissions = permissions();
		if (permissions == null) {
			return false;
		}
		Collection<String> groupNames = permissions.getGroupNames();
		Set<Integer> groupIds = permissions.getGroupIds();
		for (Object userGroup : userGroups) {
			if (userGroup instanceof String && !groupNames.contains(userGroup)) {
				return false;
			} else if (userGroup instanceof Integer && !groupIds.contains(userGroup)) {
				return false;
			}
		}
		return true;
	}

	public float versionOf(String featureModuleOrApp) throws FeatureToggleException {
		if ("PICSORG".equalsIgnoreCase(featureModuleOrApp)) {
			return versionOfPicsOrg();
		} else if ("BPROC".equalsIgnoreCase(featureModuleOrApp)) {
			return versionOfBackProcs();
		} else {
			throw new FeatureToggleException("Unknown feature, module or application for version check");
		}
	}

	public float versionOfBackProcs() throws FeatureToggleException {
		Binding binding = getBinding();
		AppPropertyDAO appPropertyDAO = (AppPropertyDAO) binding.getVariable("appPropertyDAO");
		String version = appPropertyDAO.getProperty("VERSION.BPROC");
		try {
			return Float.parseFloat(version);
		} catch (Exception e) {
			throw new FeatureToggleException(e.getMessage());
		}
	}

	public float versionOfPicsOrg() throws FeatureToggleException {
		try {
			return Float.parseFloat(PicsOrganizerVersion.getVersion());
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

}
