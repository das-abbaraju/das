package com.picsauditing.eula.service;

import com.picsauditing.access.LoginService;
import com.picsauditing.dao.EulaAgreementDao;
import com.picsauditing.dao.EulaDao;
import com.picsauditing.persistence.model.Eula;
import com.picsauditing.persistence.model.EulaAgreement;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.authentication.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.security.auth.login.LoginException;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class EulaServiceTest {
    private static final String USER_NAME = "tswift";
    private static final Integer USER_ID = 42;
    private static final Integer EULA_ID = 13;
    private static final String USER_PASSWORD = "sparksFly32";

    private static final Answer<Eula> FIND_EULA_BY_COUNTRY = new Answer<Eula>() {
        @Override
        public Eula answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            String country = (String) args[0];

            return getMockLoginEula(country);
        }
    };

    @Mock
    private EulaAgreementDao eulaAgreementDao;
    @Mock
    private EulaDao eulaDao;
    @Mock
    private LoginService loginService;
    @Mock
    private AuthenticationService authenticationService;

    private EulaService eulaService;
    private User user;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eulaService = new EulaService();
        user = User.builder().id(USER_ID).build();
        Whitebox.setInternalState(eulaService, "eulaAgreementDao", eulaAgreementDao);
        Whitebox.setInternalState(eulaService, "eulaDao", eulaDao);
        Whitebox.setInternalState(eulaService, "loginService", loginService);
        Whitebox.setInternalState(eulaService, "authenticationService", authenticationService);

        doAnswer(FIND_EULA_BY_COUNTRY).when(eulaDao).findByCountry(anyString());
    }

    @Test
    public void testGetLoginEulaAgreement() throws Exception {
        when(eulaAgreementDao.findByUserAndEulaId((USER_ID.longValue()), EULA_ID.longValue())).thenReturn(getMockEulaAgreement());

        EulaAgreement loginEulaAgreement = eulaService.getLoginEulaAgreement(user, getMockLoginEula());

        assertNotNull(loginEulaAgreement);
    }

    @Test
    public void testGetLoginEulaAgreement_shouldReturnNullIfNoEulaAgreement() throws Exception {
        when(eulaAgreementDao.findByUserAndEulaId((USER_ID.longValue()), EULA_ID.longValue())).thenReturn(null);

        EulaAgreement loginEulaAgreement = eulaService.getLoginEulaAgreement(user, getMockLoginEula());

        assertNull(loginEulaAgreement);
    }

    @Test
    public void testAcceptLoginEula_NewEulaAgreement() {
        ArgumentCaptor<EulaAgreement> eulaAgreementCaptor = ArgumentCaptor.forClass(EulaAgreement.class);

        eulaService.acceptLoginEula(user, "US");

        verify(eulaAgreementDao).insertEulaAgreement(eulaAgreementCaptor.capture());

        EulaAgreement persistedEulaAgreement = eulaAgreementCaptor.getValue();
        assertNotNull(persistedEulaAgreement);
        assertEquals(USER_ID.longValue(), persistedEulaAgreement.userId());
        assertEquals(EULA_ID.longValue(), persistedEulaAgreement.eulaId());
    }

    @Test
    public void testAcceptLoginEula_UpdateEulaAgreement() {
        when(eulaAgreementDao.findByUserAndEulaId(USER_ID.longValue(), EULA_ID.longValue())).thenReturn(getMockEulaAgreement());

        ArgumentCaptor<EulaAgreement> eulaAgreementCaptor = ArgumentCaptor.forClass(EulaAgreement.class);

        eulaService.acceptLoginEula(user, "US");

        verify(eulaAgreementDao).updateEulaAgreement(eulaAgreementCaptor.capture(), any(Date.class), eq(user));

        EulaAgreement persistedEulaAgreement = eulaAgreementCaptor.getValue();
        assertNotNull(persistedEulaAgreement);
        assertEquals(USER_ID.longValue(), persistedEulaAgreement.userId());
        assertEquals(EULA_ID.longValue(), persistedEulaAgreement.eulaId());
    }

    @Test
    public void testDoPreloginVerification_NormalLogin() throws LoginException {
        when(loginService.getUserForUserName(USER_NAME)).thenReturn(user);

        eulaService.doPreloginVerification(USER_NAME, USER_PASSWORD);

        verify(loginService).doPreLoginVerification(user, USER_NAME, USER_PASSWORD);
        verify(authenticationService, never()).doPreLoginVerificationEG(anyString(), anyString());
    }

    @Test
    public void testDoPreloginVerification_EgLogin() throws LoginException {
        when(loginService.getUserForUserName(USER_NAME)).thenReturn(null);

        eulaService.doPreloginVerification(USER_NAME, USER_PASSWORD);

        verify(loginService, never()).doPreLoginVerification(any(User.class), anyString(), anyString());
        verify(authenticationService).doPreLoginVerificationEG(USER_NAME, USER_PASSWORD);
    }

    @Test
    public void testExtractCountryIso_US() {
        String loginEulaUrl = "/eulas/login/US.action";

        String country = eulaService.extractCountryIso(loginEulaUrl);

        assertEquals("US", country);
    }

    @Test
    public void testExtractCountryIso_CA() {
        String loginEulaUrl = "/eulas/login/CA.action";

        String country = eulaService.extractCountryIso(loginEulaUrl);

        assertEquals("CA", country);
    }

    @Test
    public void testExtractCountryIso_Malformed() {
        String loginEulaUrl = "/eulas/CA.action";

        String country = eulaService.extractCountryIso(loginEulaUrl);

        assertEquals(null, country);
    }

    private static Eula getMockLoginEula() {
        return getMockLoginEula("US");
    }

    private static Eula getMockLoginEula(String country) {
        return Eula.createFrom(EULA_ID, "login", 1, country, "foo", USER_ID);
    }

    private static EulaAgreement getMockEulaAgreement() {
        return EulaAgreement.createFrom(USER_ID, EULA_ID);
    }

}