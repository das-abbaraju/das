package com.picsauditing.service.registration;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.billing.RegistrationBillingBean;
import com.picsauditing.util.DataScrubber;

import java.util.Date;

public class RegistrationService {

    private static final String DEMO_CONTRACTOR_NAME_MARKER = "^^^";

    private final RegistrationBillingBean billingBean;

    public RegistrationService(RegistrationBillingBean bean) {
        this.billingBean = bean;
    }

    public RegistrationSubmission newRegistration() {
        return new RegistrationSubmission(this);
    }

    void doRegistration(RegistrationSubmission registrationSubmission) {
        User newUser = createUserFrom(registrationSubmission);
        ContractorAccount newAccount = createContractorAccountFrom(registrationSubmission);
        // TODO: Set up AppUser
        // TODO: Link Entities
        // TODO: Persist Entities
    }

    private ContractorAccount createContractorAccountFrom(RegistrationSubmission form) {
        ContractorAccount registrant = new ContractorAccount();
        registrant.setType("Contractor");
        registrant.setCountrySubdivision(form.getCountrySubdivision()); // TODO: Work this out.
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
}
