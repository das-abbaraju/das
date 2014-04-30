package com.picsauditing.access.user;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.provisioning.ProductSubscriptionService;
import com.picsauditing.service.user.UserService;
import com.picsauditing.web.NameSpace;
import com.picsauditing.web.SessionInfoProvider;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

final class UserModeProviderImpl implements UserModeProvider {

	@Autowired
	private ProductSubscriptionService productSubscriptionService;
	@Autowired
	private UserService userService;

	@Override
	public final Set<UserMode> getAvailableUserModes(final int appUserId) {
		return addEmployeeMode(addAdminMode(new HashSet<UserMode>(), appUserId), appUserId);
	}

	private Set<UserMode> addAdminMode(final Set<UserMode> userModes, final int appUserId) {
		User user = userService.findByAppUserId(appUserId);
		if (user != null) {
			userModes.add(UserMode.ADMIN);
		}

		return userModes;
	}

	private Set<UserMode> addEmployeeMode(final Set<UserMode> userModes, final int appUserId) {
		if (productSubscriptionService.isEmployeeGUARDEmployeeUser(appUserId)) {
			userModes.add(UserMode.EMPLOYEE);
		}

		return userModes;
	}

	@Override
	public UserMode getCurrentUserMode(final Permissions permissions) {
		NameSpace nameSpace = SessionInfoProviderFactory.getSessionInfoProvider().getNamespace();

		String uri = SessionInfoProviderFactory.getSessionInfoProvider().getURI();

		if (checkUserIsInEmployeeMode(permissions, nameSpace, uri)) {
			return UserMode.EMPLOYEE;
		}

		return UserMode.ADMIN;
	}

	private boolean checkUserIsInEmployeeMode(Permissions permissions, NameSpace nameSpace, String uri) {
		return (nameSpace == NameSpace.EMPLOYEEGUARD
				|| (uri != null && uri.contains(SessionInfoProvider.EMPLOYEEGUARD_NAMESPACE)))
				&& (permissions.getAvailableUserModes().contains(UserMode.EMPLOYEE));
	}
}
