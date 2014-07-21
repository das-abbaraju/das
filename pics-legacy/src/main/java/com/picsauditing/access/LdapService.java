package com.picsauditing.access;

import com.picsauditing.featuretoggle.Features;
import com.picsauditing.service.user.IdpUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import javax.security.auth.login.FailedLoginException;


public class LdapService {

    public static final String PICS_CORP = "@PICS.CORP";
    public static final String PICSAD = "picsad";

    private static final Logger logger = LoggerFactory.getLogger(LdapService.class);

    @Autowired
    private ActiveDirectoryLdapAuthenticationProvider ldapActiveDirectoryAuthProvider;

    @Autowired
    private IdpUserService idpUserService;

    public boolean doLdapLoginAuthentication(String idp, String username, String password) throws FailedLoginException {
        switch (idp) {
            case PICSAD:
                return doPicsLdapAuthentication(username, password);
        }
        return false;
    }

    private String appendPicsLdapDomain(String username) {
        StringBuilder picsDomain = new StringBuilder();
        picsDomain.append(username);
        picsDomain.append(PICS_CORP);
        return picsDomain.toString();
    }

    private boolean doPicsLdapAuthentication(String userName, String password) throws FailedLoginException {
        if (Features.USE_LDAP_AUTHENTICATION.isActive()) {
            String picsLdapDomain = appendPicsLdapDomain(userName);
            try {
                ldapActiveDirectoryAuthProvider.setConvertSubErrorCodesToExceptions(true);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(picsLdapDomain, password);
                Authentication result = ldapActiveDirectoryAuthProvider.authenticate(authentication);
                return result != null ? result.isAuthenticated() : false;
            } catch (AuthenticationException ace) {
                logger.error("Bad LDAP Credentials for user: " + userName);
                throw new FailedLoginException("Bad Credentials for user: " + userName);
            }
        }
        return false;
    }
    public boolean isValidIdp(String idp) {
        if (StringUtils.isEmpty(idp)) {
            return false;
        }
        switch (idp) {
            case PICSAD:
                return true;
            default:
                return false;
        }
    }
}
