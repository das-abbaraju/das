package com.picsauditing.service.registration;

import com.picsauditing.access.OpPerms;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.billing.RegistrationBillingBean;
import com.picsauditing.util.DataScrubber;
import org.apache.commons.lang.math.NumberUtils;
import org.json.simple.JSONObject;

import java.util.Date;

public class RegistrationService {

    private static final String DEMO_CONTRACTOR_NAME_MARKER = "^^^";

    private final RegistrationBillingBean billingBean;
    private final AppUserService appUserService;
    private final AppUserDAO appUserDAO;

    public RegistrationService(
            RegistrationBillingBean bean,
            AppUserService service,
            AppUserDAO appUserDAO
    ) {
        this.billingBean = bean;
        this.appUserService = service;
        this.appUserDAO = appUserDAO;
    }

    public RegistrationSubmission newRegistration() {
        return new RegistrationSubmission(this);
    }

    void doRegistration(RegistrationSubmission registrationSubmission) {
        User newUser = createUserFrom(registrationSubmission);
        ContractorAccount newAccount = createContractorAccountFrom(registrationSubmission);
        AppUser appUser = createAppUserFrom(newUser);

        
        // TODO: Link Entities
        // TODO: Persist Entities
    }

    private ContractorAccount createContractorAccountFrom(RegistrationSubmission form) {
        ContractorAccount registrant = new ContractorAccount();
        registrant.setType("Contractor");
        registrant.setAddress(form.getAddress());
        registrant.setAddress2(form.getAddress2());
        registrant.setVatId(form.getVatID());
        registrant.setCountrySubdivision(new CountrySubdivision(form.getCountrySubdivision()));
        registrant.setStatus(AccountStatus.Pending);
        registrant.setLocale(form.getLocale());
        registrant.setPhone(form.getPhoneNumber());
        registrant.setPaymentExpires(new Date());
        registrant.setAuditColumns(new User(User.CONTRACTOR));
        registrant.setNameIndex();


        //TODO: Extract NAICS logic into it's own class / service.
        registrant.setNaics(new Naics());
        registrant.getNaics().setCode("0");
        registrant.setNaicsValid(false);

        //TODO: Put this logic somewhere else.
        billingBean.assessInitialFees(registrant);

        if (registrant.getCountry().isUK())
            registrant.setZip(DataScrubber.cleanUKPostcode(registrant.getZip()));

        return checkForDemo(registrant);
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
        registrant.setName(form.getUserFirstName() + " " + form.getUserLastName());
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

        return registrant;
    }

    private AppUser createAppUserFrom(User user) {
        String username = user.getUsername();
        JSONObject appUserResponse = appUserService.createNewAppUser(username, user.getPassword());
        if (appUserResponse != null && "SUCCESS".equals(appUserResponse.get("status").toString())) {
            int appUserID = NumberUtils.toInt(appUserResponse.get("id").toString());
            return appUserDAO.findByAppUserID(appUserID);
        } else {
            throw new RuntimeException("App User Service is Down.");
            // TODO: Find a better way to deal with a non-success.
        }
    }
}
