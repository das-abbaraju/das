package com.picsauditing.service.account;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AddressVerification;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.account.AddressVerificationStatus;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.ResultStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class AddressServiceTest {

    private AddressService addressService;
    private ContractorAccount account;
    private AddressResponseHolder addressHolderResponse;
    private User user;

    @Mock
    ContractorAccountDAO contractorAccountDao;
    private AddressVerification addressVerification;


    @Before
    public void setUp() throws Exception {
        addressService = new AddressService();
        account = new ContractorAccount();
        addressVerification = new AddressVerification();
        account.setAddressVerification(addressVerification);;

        MockitoAnnotations.initMocks(this);
        user = User.builder().id(1).build();

        Whitebox.setInternalState(addressService, "contractorAccountDao", contractorAccountDao);
    }

    @Test
    public void testSaveAddressFieldsFromVerifiedAddress_accountAddressShouldBeUpdated() throws Exception {
        buildAddressHolderResponse(ResultStatus.SUCCESS);
        addressService.saveAddressFieldsFromVerifiedAddress(account, addressHolderResponse, user);

        assertEquals("555 Main", account.getAddress());
        assertEquals("Ste 140", account.getAddress2());
        assertEquals("Irvine", account.getCity());
        assertEquals("99999", account.getZip());
        assertEquals("US", account.getCountry().getIsoCode());
        assertEquals("US-CA", account.getCountrySubdivision().getIsoCode());
    }

    @Test
    public void testSaveAddressFieldsFromVerifiedAddress_addressVerificationShouldBeCreated() throws Exception {
        buildAddressHolderResponse(ResultStatus.SUCCESS);
        addressService.saveAddressFieldsFromVerifiedAddress(account, addressHolderResponse, user);

        assertEquals("Account", account.getAddressVerification().getEntityType());
        assertEquals(DateBean.setToStartOfDay(new Date()), account.getAddressVerification().getVerificationDate());
        assertEquals(AddressVerificationStatus.PASSED_VALIDATION, account.getAddressVerification().getStatus());
    }

    @Test
    public void testSaveAddressFieldsFromVerifiedAddress_addressVerificationShouldBeCreatedAndMarkedFailed() throws Exception {
        buildAddressHolderResponse(ResultStatus.DATA_NOT_FOUND);
        addressService.saveAddressFieldsFromVerifiedAddress(account, addressHolderResponse, user);

        assertEquals("Account", account.getAddressVerification().getEntityType());
        assertEquals(DateBean.setToStartOfDay(new Date()), account.getAddressVerification().getVerificationDate());
        assertEquals(AddressVerificationStatus.FAILED_VALIDATION, account.getAddressVerification().getStatus());
    }

    @Test
    public void testSaveAddressFieldsFromVerifiedAddress_addressVerificationAndAccountShouldBeSaved() throws Exception {
        buildAddressHolderResponse(ResultStatus.SUCCESS);
        addressService.saveAddressFieldsFromVerifiedAddress(account, addressHolderResponse, user);

        verify(contractorAccountDao).save(addressVerification);
        verify(contractorAccountDao).save(account);
    }

    private void buildAddressHolderResponse(ResultStatus resultStatus) {
        addressHolderResponse = AddressResponseHolder.builder()
                .addressLine1("555 Main")
                .addressLine2("Ste 140")
                .city("Irvine")
                .zipOrPostalCode("99999")
                .country("US")
                .stateOrProvince("CA")
                .resultStatus(resultStatus)
                .build();
    }
}
