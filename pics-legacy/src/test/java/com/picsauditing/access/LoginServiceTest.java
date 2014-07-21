package com.picsauditing.access;

import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LoginServiceTest {
    private LoginService loginService;

    private String username = "joesixpack";
    private String password = "8675309";
    private String key = "abc123";

    @Mock
    private UserService userService;
    @Mock
    private User user;
    @Mock
    private AppUser appUser;
    @Mock
    private Profile profile;
    @Mock
    private ContractorAccount account;
    @Mock
    private AppUserService appUserService;
    @Mock
    private ProfileEntityService profileService;
    @Mock
    private UserDAO userDAO;

    @Mock
    private LdapService ldapService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loginService = new LoginService();

        loginService.userService = userService;
        setupNormalUser();

        when(user.getAccount()).thenReturn(account);
        when(account.getId()).thenReturn(123);
        when(appUser.getId()).thenReturn(123);

        Whitebox.setInternalState(loginService, "appUserService", appUserService);
        Whitebox.setInternalState(loginService, "profileService", profileService);
        Whitebox.setInternalState(loginService, "userService", userService);
        Whitebox.setInternalState(loginService, "userDAO", userDAO);

    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_NotContractorWithPreloginUrl() throws Exception {
        when(account.isContractor()).thenReturn(false);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect("not empty", user);

        assertThat(HomePageType.PreLogin, is(equalTo(homePageType)));

    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_NotContractorWithNoPreloginUrl() throws Exception {
        when(account.isContractor()).thenReturn(false);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(null, user);

        assertThat(HomePageType.HomePage, is(equalTo(homePageType)));

    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_DeclinedContractor() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Declined);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(null, user);

        assertThat(HomePageType.Declined, is(equalTo(homePageType)));
    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_DeactivatedContractor() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Deactivated);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(null, user);

        assertThat(HomePageType.Deactivated, is(equalTo(homePageType)));
    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_ActiveDoneContractorWithPreLoginUrl() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Active);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect("not empty", user);

        assertThat(HomePageType.PreLogin, is(equalTo(homePageType)));
    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_NotDoneContractorWithPreLoginUrl() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Pending);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect("not empty", user);

        assertThat(HomePageType.ContractorRegistrationStep, is(equalTo(homePageType)));
    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_NotDoneContractorWithNoPreLoginUrl() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Pending);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(null, user);

        assertThat(HomePageType.ContractorRegistrationStep, is(equalTo(homePageType)));
    }

    @Test
    public void testPostLoginHomePageTypeForRedirect_ActiveDoneContractorWithNoPreLoginUrl() throws Exception {
        when(account.isContractor()).thenReturn(true);
        when(account.getStatus()).thenReturn(AccountStatus.Active);

        HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(null, user);

        assertThat(HomePageType.ContractorRegistrationStep, is(equalTo(homePageType)));
    }

    @Test
    public void testLoginNormally_HappyPathShouldNotThrowErrors() throws Exception {

        loginService.loginNormally(username, password);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testLoginNormally_NonExistentUserShouldThrowAccountNotFoundException() throws Exception {
        when(userService.loadUserByUsername(username)).thenReturn(null);

        loginService.loginNormally(username, password);
    }

    @Test(expected = AccountLockedException.class)
    public void testLoginNormally_LockedUserShouldThrowAccountLockedException() throws Exception {
        when(user.isLocked()).thenReturn(true);

        loginService.loginNormally(username, password);
    }

    @Test(expected = AccountInactiveException.class)
    public void testLoginNormally_InactiveUserShouldThrowAccountInactiveException() throws Exception {
        when(userService.isUserActive(user)).thenReturn(false);

        loginService.loginNormally(username, password);
    }

    @Test(expected = FailedLoginException.class)
    public void testLoginNormally_FailedLogin() throws Exception {
        when(user.isEncryptedPasswordEqual(password)).thenReturn(false);
        when(user.getFailedAttempts()).thenReturn(1);

        loginService.loginNormally(username, password);

        verify(user).setFailedAttempts(2);
        verify(user, never()).setLockUntil(any(Date.class));
    }

    @Test(expected = FailedLoginAndLockedException.class)
    public void testLoginNormally_FailedLoginAndLocked() throws Exception {
        when(user.isEncryptedPasswordEqual(password)).thenReturn(false);
        when(user.getFailedAttempts()).thenReturn(LoginService.MAX_FAILED_ATTEMPTS + 1);

        loginService.loginNormally(username, password);

        verify(user).setFailedAttempts(0);
        verify(user).setLockUntil(any(Date.class));
    }

    @Test(expected = PasswordExpiredException.class)
    public void testLoginNormally_ExpiredPasswordShouldThrowPasswordExpiredException() throws Exception {
        when(userService.isPasswordExpired(user)).thenReturn(true);

        loginService.loginNormally(username, password);
    }

    public void passwordResetSetUp() {
        when(appUserService.findByUsername(anyString())).thenReturn(appUser);
        when(profileService.findByAppUserId(anyInt())).thenReturn(null);
        when(userService.findByAppUserId(anyInt())).thenReturn(user);
    }

    @Test
    public void testLoginForResetPassword_HappyPathShouldNotThrowErrors() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        loginService.loginForResetPassword(username, key);
    }


    @Test(expected = InvalidResetKeyException.class)
    public void testLoginForResetPassword_InvalidResetKeyShouldThrowInvalidResetKeyException() throws Exception {
        passwordResetSetUp();
        when(user.getResetHash()).thenReturn("notAMatch");

        loginService.loginForResetPassword(username, key);
    }

    @Test
    public void testLoginForResetPassword_IfKeyMatchesPrepareUserForLoginAfterReset() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        loginService.loginForResetPassword(username, key);

        verify(appUser).setResetHash(null);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testLoginForResetPassword_NonExistentUserShouldThrowAccountNotFoundException() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        when(userService.findByAppUserId(anyInt())).thenReturn(null);

        loginService.loginForResetPassword(username, key);
    }

    @Test(expected = AccountLockedException.class)
    public void testLoginForResetPassword_LockedUserShouldThrowAccountLockedException() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        when(user.isLocked()).thenReturn(true);

        loginService.loginForResetPassword(username, key);
    }

    @Test(expected = AccountInactiveException.class)
    public void testLoginForResetPassword_InactiveUserShouldThrowAccountInactiveException() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        when(userService.isUserActive(user)).thenReturn(false);

        loginService.loginForResetPassword(username, key);
    }

    @Test(expected = PasswordExpiredException.class)
    public void testLoginForResetPassword_ExpiredPasswordShouldThrowPasswordExpiredException() throws Exception {
        passwordResetSetUp();
        when(appUser.getResetHash()).thenReturn(key);

        when(userService.isPasswordExpired(user)).thenReturn(true);

        loginService.loginForResetPassword(username, key);
    }

    @Test
    public void testGetUserForUserName() throws LoginException {
        String userName = "tswift";
        int appUserId = 22;
        when(appUserService.findByUsername(userName)).thenReturn(
                AppUser.builder()
                        .id(appUserId)
                        .build()
        );

        when(userDAO.findUserByAppUserID(appUserId)).thenReturn(
                User.builder()
                        .userName(userName)
                        .build()
        );

        User user = loginService.getUserForUserName(userName);

        assertNotNull(user);
        assertEquals(userName, user.getUsername());
    }

    @Test(expected = LoginException.class)
    public void testGetUserForUserName_NoAppUserFound() throws LoginException {
        when(appUserService.findByUsername(anyString())).thenReturn(null);

        loginService.getUserForUserName(anyString());
    }


    private void setupNormalUser() {
        when(user.isLocked()).thenReturn(false);
        when(user.isEncryptedPasswordEqual(password)).thenReturn(true);
        when(user.getResetHash()).thenReturn(key);
        when(userService.loadUserByUsername(anyString())).thenReturn(user);
        when(userService.isUserActive(user)).thenReturn(true);
        when(userService.isPasswordExpired(user)).thenReturn(false);
    }
}
