package com.picsauditing.service.registration;

import com.picsauditing.access.OpPerms;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.service.account.AccountService;
import com.picsauditing.service.account.events.ContractorEventType;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.service.billing.RegistrationBillingBean;
import com.picsauditing.service.user.UserService;
import com.picsauditing.util.DataScrubber;
import com.picsauditing.util.SapAppPropertyUtil;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Locale;

// Logic extracted from Registration.java
public class RegistrationService {

    private static final String DEMO_CONTRACTOR_NAME_MARKER = "^^^";

    private final RegistrationBillingBean billingBean;
    private final AccountService accountService;
    private final LanguageModel supportedLanguages;
    private final UserService userService;
    private final RegistrationRequestService regReqService;
	private final AuthenticationService authenticationService;
    private final AppUserService appUserService;

    public RegistrationService(
            RegistrationBillingBean bean,
            AccountService accountService,
            LanguageModel supportedLanguages,
            UserService userService,
            RegistrationRequestService regReqService,
			AuthenticationService authenticationService,
            AppUserService service
    ) {
        this.billingBean = bean;
        this.accountService = accountService;
        this.supportedLanguages = supportedLanguages;
        this.userService = userService;
        this.regReqService = regReqService;
		this.authenticationService = authenticationService;
        this.appUserService = service;
    }

    public RegistrationSubmission newRegistration() {
        return new RegistrationSubmission(this);
    }

    RegistrationResult doRegistration(RegistrationSubmission registrationSubmission) {
        try {
            return attemptRegistration(registrationSubmission);
        } catch (Exception e) {
            return new RegistrationResult.RegistrationFailure(e);
        }
    }

    private RegistrationResult.RegistrationSuccess attemptRegistration(RegistrationSubmission registrationSubmission) {
        //Create the entities.
        User newUser = createUserFrom(registrationSubmission);
        ContractorAccount newAccount = createContractorAccountFrom(registrationSubmission);
        AppUser appUser = createAppUserFrom(newUser);

        // Persist the account to get an ID.
        accountService.persist(newAccount);

        //Make connections requiring the ID.
        newUser.setAccount(newAccount);
        newUser.setAppUser(appUser);
        newAccount.getUsers().add(newUser);
        newAccount.setPrimaryContact(newUser);

        //FIXME: What does this mean? Agree to what?? This is poorly named.
        newAccount.setAgreedBy(newUser);
        newAccount.setAgreementDate(new Date());

        if (!newAccount.isDemo()) {
            newAccount.setQbSync(true);
            SapAppPropertyUtil sapAppPropertyUtil = SapAppPropertyUtil.factory();
            if (sapAppPropertyUtil.isSAPBusinessUnitSetSyncTrueEnabledForObject(newAccount)) {
                newAccount.setSapSync(true);
            }
        }

        //Persist the user to get the user ID back.
        userService.persist(newUser);
        //Re-persist the contractor, because we added the user.
        //I'm not actually sure this is necessary. Does the User save cascade?
        accountService.persist(newAccount);

        //This probably doesn't need to be done syncrhonously.
        appUserService.save(appUser);

        //Propagate the registration events.
        publishCreation(newAccount);

        return new RegistrationResult.RegistrationSuccess(newAccount, newUser);
    }

    private void publishCreation(ContractorAccount newAccount) {
        accountService.publishEvent(newAccount, ContractorEventType.Registration);
    }

    private ContractorAccount createContractorAccountFrom(RegistrationSubmission form) {
        ContractorAccount registrant = checkForPreExistingSubmission(form);
        registrant.setName(form.getContractorName());
        registrant.setType("Contractor");
        registrant.setAddress(form.getAddress());
        registrant.setAddress2(form.getAddress2());
        registrant.setCity(form.getCity());
        registrant.setVatId(form.getVatID());
        registrant.setCountry(new Country(form.getCountryISO()));

        if (form.getCountrySubdivision() != null)
            registrant.setCountrySubdivision(new CountrySubdivision(form.getCountrySubdivision()));

        registrant.setStatus(AccountStatus.Pending);
        registrant.setLocale(form.getLocale());
        registrant.setPhone(form.getPhoneNumber());
        registrant.setPaymentExpires(new Date());
        registrant.setAuditColumns(new User(User.CONTRACTOR));
        registrant.setNameIndex();
        registrant.setZip(form.getZip());


        //FIXME: Extract NAICS logic into it's own class / service.
        registrant.setNaics(new Naics());
        registrant.getNaics().setCode("0");
        registrant.setNaicsValid(false);

        //FIXME: Put this logic somewhere else. Maybe another listener? Does this need to be synchronous?
        billingBean.assessInitialFees(registrant);

        if (registrant.getCountry() != null && registrant.getCountry().isUK())
            registrant.setZip(DataScrubber.cleanUKPostcode(registrant.getZip()));

        return checkForDemo(registrant);
    }

    private ContractorAccount checkForPreExistingSubmission(RegistrationSubmission form) {
        if (Strings.isNotEmpty(form.getRegistrationRequestHash()))
            return regReqService.preRegistrationFromKey(form.getRegistrationRequestHash());
        else
            return new ContractorAccount();
    }

    private ContractorAccount checkForDemo(ContractorAccount registrant) {
        if (registrant.getName().contains(DEMO_CONTRACTOR_NAME_MARKER)) {
            registrant.setStatus(AccountStatus.Demo);
            registrant.setName(registrant.getName().replaceAll("^", "").trim());
        }
        return registrant;
    }

    private User createUserFrom(RegistrationSubmission form) {
        User registrant = new User();
        registrant.setEmail(form.getEmail());
        registrant.setFirstName(form.getUserFirstName());
        registrant.setLastName(form.getUserLastName());
        registrant.updateDisplayNameBasedOnFirstAndLastName();
        registrant.setPhone(form.getPhoneNumber());
        registrant.setPassword(form.getPassword());
        registrant.setUsername(form.getUserName());
        registrant.setActive(true);
        registrant.setTimezone(form.getTimeZone());
        registrant.setAuditColumns(new User(User.CONTRACTOR));
        registrant.setIsGroup(YesNo.No);

        registrant.addOwnedPermissions(OpPerms.ContractorAdmin, User.CONTRACTOR);
        registrant.addOwnedPermissions(OpPerms.ContractorSafety, User.CONTRACTOR);
        registrant.addOwnedPermissions(OpPerms.ContractorInsurance, User.CONTRACTOR);
        registrant.addOwnedPermissions(OpPerms.ContractorBilling, User.CONTRACTOR);
        registrant.setLastLogin(new Date());

        registrant.setLocale(closestAvailableLanguage(form.getLocale()));

        return registrant;
    }

    private Locale closestAvailableLanguage(Locale locale) {
        return supportedLanguages.getClosestVisibleLocale(locale);
    }

	// This can probably be done asynchronously, too.
	private AppUser createAppUserFrom(User user) {
		String username = user.getUsername();
		try {
			return authenticationService.createNewAppUser(username, user.getPassword());
		} catch (Exception e) {
			// FIXME: Find a better way to deal with a non-success.
			throw new RuntimeException("Unable to create an AppUser - " + e.getMessage());
		}
	}

}
