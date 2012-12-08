package com.picsauditing.PICS;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class InputValidator {

    @Autowired
    private UserDAO userDao;
    @Autowired
    private ContractorAccountDAO contractorAccountDao;

    // (?s) turns on single-line mode, which makes '.' also match line terminators (DOTALL)
    public final static String SPECIAL_CHAR_REGEX = "(?s).*[;<>&`\"].*";

    public final static String VALID_USERNAME_REGEX = "[\\w+.@-]{5,100}";

    public boolean isUsernameTaken(String username) {
        return userDao.duplicateUsername(username, 0);
    }

    public boolean isCompanyNameTaken(String companyName) {
        List<ContractorAccount> accounts = contractorAccountDao.findByCompanyName(companyName);

        if (CollectionUtils.isNotEmpty(accounts)) {
            return true;
        }

        return false;
    }

    public boolean containsOnlySafeCharacters(String str) {
    	if (str == null) {
    		return false;
    	}

        if (StringUtils.isEmpty(str)) {
            return true;
        }

        if (str.matches(SPECIAL_CHAR_REGEX)) {
            return false;
        }

        return true;
    }

    public boolean isUsernameValid(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }

        if (username.matches(VALID_USERNAME_REGEX)) {
            return true;
        }

        return false;
    }

}
