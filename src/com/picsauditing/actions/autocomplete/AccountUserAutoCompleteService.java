package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class AccountUserAutoCompleteService extends AbstractAutocompleteService<User> {

	@Autowired
	private UserDAO userDAO;

	private static final Logger logger = LoggerFactory.getLogger(AccountUserAutoCompleteService.class);

	@Override
	protected Collection<User> getItemsForSearch(String search, Permissions permissions) {
		Collection<User> results = Collections.emptyList();
		try {
			results = userDAO.findByGroupAndUserName(User.GROUP_MARKETING, search);
		} catch (Exception e) {
			logger.error("An error occurred while searching for account users.", e);
		}

		return results;
	}

	@Override
	protected Object getKey(User user) {
		if (user != null) {
			return user.getId();
		}

		return 0;
	}

	@Override
	protected Object getValue(User user, Permissions permissions) {
		if (user != null) {
			return user.getName();
		}

		return Strings.EMPTY_STRING;
	}

	@Override
	protected Collection<User> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int userId = NumberUtils.toInt(searchKey);
		if (userId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(userDAO.find(userId));
	}

}
