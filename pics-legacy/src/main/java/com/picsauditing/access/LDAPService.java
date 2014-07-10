package com.picsauditing.access;

import com.picsauditing.featuretoggle.Features;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import javax.security.auth.login.FailedLoginException;


public class LDAPService {

    public static final String PICS_CORP = "@PICS.CORP";

    private static final Logger logger = LoggerFactory.getLogger(LDAPService.class);

    public static final String PICSAD = "picsad";

    @Autowired
    private ActiveDirectoryLdapAuthenticationProvider ldapActiveDirectoryAuthProvider;

    public boolean doLDAPLoginAuthentication(String identityProvider, String username, String password) throws FailedLoginException {
        if (Features.USE_LDAP_AUTHENTICATION.isActive()) {
            switch (identityProvider) {
                case PICSAD:
                    return doPICSLdapAuthentication(username, password);
            }
        }
        return false;
    }

    private String getPICSLdapUser(String username) {
        StringBuilder ldapUser = new StringBuilder();
        ldapUser.append(username);
        ldapUser.append(PICS_CORP);
        return ldapUser.toString();
    }

    private boolean doPICSLdapAuthentication(String username, String password) {
        ldapActiveDirectoryAuthProvider.setConvertSubErrorCodesToExceptions(true);
        String ldapUser = getPICSLdapUser(username);
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(ldapUser, password);
            Authentication result = ldapActiveDirectoryAuthProvider.authenticate(authentication);
            return result != null ? result.isAuthenticated() : false;
        } catch (AuthenticationException ace) {
            logger.error("Bad LDAP Credentials for user: " + username);
        }
        return false;
    }
}
