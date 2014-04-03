package com.picsauditing.service.account;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.account.AddressVerificationStatus;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.ResultStatus;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class AddressService {
    public static final int CONFIDENCE_PERCENT_THRESHOLD = 80;
    @Autowired
    private ContractorAccountDAO contractorAccountDao;

    public boolean saveAddressFieldsFromVerifiedAddress(Account account, AddressResponseHolder addressResponseHolder, User user) {
        updateAccountAddress(account, addressResponseHolder);

        AddressVerification addressVerification = createOrUpdateAddressVerification(account, addressResponseHolder, user);

        contractorAccountDao.save(addressVerification);
        account.setAddressVerification(addressVerification);
        contractorAccountDao.save(account);

        return true;
    }

    private Account updateAccountAddress(Account account, AddressResponseHolder addressResponseHolder) {
        account.setAddress(addressResponseHolder.getAddressLine1());
        account.setAddress2(addressResponseHolder.getAddressLine2());
        account.setCity(addressResponseHolder.getCity());
        account.setZip(addressResponseHolder.getZipOrPostalCode());

        if (!Strings.isEmpty(addressResponseHolder.getCountry())) {
            Country country = new Country(addressResponseHolder.getCountry());
            account.setCountry(country);

            if (!Strings.isEmpty(addressResponseHolder.getStateOrProvince())) {
                CountrySubdivision countrySubdivision = new CountrySubdivision(
                        addressResponseHolder.getCountry() + "-" + addressResponseHolder.getStateOrProvince());
                account.setCountrySubdivision(countrySubdivision);
            }

        }
        return account;
    }

    private AddressVerification createOrUpdateAddressVerification(Account account, AddressResponseHolder addressResponseHolder, User user) {
        AddressVerification addressVerification = account.getAddressVerification() == null
                ? new AddressVerification(): account.getAddressVerification();
        addressVerification.setAuditColumns(user);
        addressVerification.setEntityType(Account.class.getSimpleName());
        addressVerification.setVerificationDate(DateBean.setToStartOfDay(new Date()));

        if (addressResponseHolder.getResultStatus() == ResultStatus.SUCCESS) {
            addressVerification.setStatus(AddressVerificationStatus.PASSED_VALIDATION);
        } else {
            addressVerification.setStatus(AddressVerificationStatus.FAILED_VALIDATION);
        }
        return addressVerification;
    }
}
