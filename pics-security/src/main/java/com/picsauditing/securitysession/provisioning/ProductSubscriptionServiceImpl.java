package com.picsauditing.securitysession.provisioning;

import com.picsauditing.securitysession.entities.Profile;
import com.picsauditing.securitysession.service.ProfileEntityService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductSubscriptionServiceImpl implements ProductSubscriptionService {

	@Autowired
	private ProfileEntityService profileEntityService;

	@Override
	public boolean isEmployeeGUARDEmployeeUser(final int appUserId) {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		return profile != null;
	}
}
