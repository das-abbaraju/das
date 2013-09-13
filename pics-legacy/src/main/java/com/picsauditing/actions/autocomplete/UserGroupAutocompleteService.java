package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserGroup;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

public class UserGroupAutocompleteService extends AbstractAutocompleteService<User> {

    @Autowired
    private UserDAO dao;

    @Override
    protected Collection<User> getItemsForSearch(String search, Permissions permissions) {
        if (Strings.isEmpty(search)) {
            return Collections.emptyList();
        }

        return dao.findWhere("u.isGroup = 'Yes' AND u.name LIKE '%" + Strings.escapeQuotes(search) + "%'", RESULT_SET_LIMIT);
    }

    @Override
    protected Object getKey(User user) {
        return user.getId();
    }

    @Override
    protected Object getValue(User user, Permissions permissions) {
        return user.getName();
    }

    @Override
    protected Collection<User> getItemsForSearchKey(String searchKey, Permissions permissions) {
        int contractorId = NumberUtils.toInt(searchKey);
        if (contractorId == 0) {
            return Collections.emptyList();
        }

        return Arrays.asList(dao.find(contractorId));
    }

}
