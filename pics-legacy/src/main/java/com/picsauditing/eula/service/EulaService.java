package com.picsauditing.eula.service;

import com.picsauditing.access.LoginService;
import com.picsauditing.dao.EulaAgreementDao;
import com.picsauditing.dao.EulaDao;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.persistence.model.Eula;
import com.picsauditing.persistence.model.EulaAgreement;
import com.picsauditing.service.authentication.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.LoginException;


public class EulaService {

    @Autowired
    private EulaAgreementDao eulaAgreementDao;
    @Autowired
    private EulaDao eulaDao;
    @Autowired
    private LoginService loginService;
    @Autowired
    private AuthenticationService authenticationService;

    private final Logger logger = LoggerFactory.getLogger(EulaService.class);

    private static final String UNABLE_TO_FIND_EULA_ERROR_MESSAGE = "Unable to find a EULA for country: {0}";


    public EulaAgreement getLoginEulaAgreement(User user, Eula loginEula) {
        return eulaAgreementDao.findByUserAndEulaId(user.getId(), (Long) loginEula.id().get());
    }

    public Eula getLoginEula(Country country) {
        return eulaDao.findByCountry(country.getIsoCode());
    }

    public void doPreloginVerification(String userName, String password) throws LoginException {
        User user = loginService.getUserForUserName(userName);

        if (user != null) {
            loginService.doPreLoginVerification(user, userName, password);
        } else {
            authenticationService.doPreLoginVerificationEG(userName, password);
        }
    }

    public void acceptLoginEula(User user, String country) {
        Eula loginEula = eulaDao.findByCountry(country);
        if (loginEula != null) {
            EulaAgreement eulaAgreement = getLoginEulaAgreement(user, loginEula);

            if (eulaAgreement == null) {
                eulaAgreement = buildEulaAgreement(user, loginEula);
                eulaAgreementDao.insertEulaAgreement(eulaAgreement);
            } else {
                eulaAgreementDao.updateEulaAgreement(eulaAgreement, getCurrentDate(), user);
            }
        } else {
            logger.error(UNABLE_TO_FIND_EULA_ERROR_MESSAGE, country);
        }
    }

    protected java.util.Date getCurrentDate() {
        return new java.util.Date();
    }

    private EulaAgreement buildEulaAgreement(User user, Eula loginEula) {
        return EulaAgreement.createFrom(user.getId(), loginEula);
    }

    public String extractCountryIso(String loginEulaUrl) {
        try {
            return loginEulaUrl.split("/")[3].split("\\.")[0];
        } catch (Exception e) {
            return null;
        }
    }
}
