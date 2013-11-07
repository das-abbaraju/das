package com.strikeiron.www;

import com.AddressDoctor.validator2.addBatch.Batch.*;
import com.strikeiron.www.api.GlobalAddressVerificationLocator;
import com.strikeiron.www.api.GlobalAddressVerificationSoap;
import com.strikeiron.www.api.Listing;
import com.strikeiron.www.api.holders.SISubscriptionInfoHolder;
import com.strikeiron.www.api.holders.SIWsOutputOfListingHolder;

import javax.xml.rpc.ServiceException;

public class GlobalAddressVerifier {
    protected static final String USER_NAME = "picsauditing@strikeiron.com";
    protected static final String PASSWORD = "pics$567";
    protected static final String ERROR_MESSAGE = "StrikeIron Service is down";

    private GlobalAddressVerificationLocator locator;

    public GlobalAddressVerifier(GlobalAddressVerificationLocator locator) {
        this.locator = locator;
    }

    public GlobalAddressVerifier() {
        locator = new GlobalAddressVerificationLocator();
    }

    public Address verifyAddress(String streetAddressLines, String locality, String province, String country, String postalCode) throws ServiceException {
        Address result = new Address();

        SIWsOutputOfListingHolder siResponse = new SIWsOutputOfListingHolder();
        SISubscriptionInfoHolder subInfo = new SISubscriptionInfoHolder();;

        try {
            GlobalAddressVerificationSoap siService = locator.getGlobalAddressVerificationSoap();
            Address address = new Address();
            address.setStreetAddressLines(streetAddressLines);
            address.setLocality(locality);
            address.setProvince(province);
            address.setCountry(country);
            address.setPostalCode(postalCode);

            siService.advancedVerify(null, USER_NAME, PASSWORD, address, true, true, true,
                    CountryOfOriginType.COO_USA, CountryType.ISO_2, LineSeparatorType.LST_NO_SEPARATOR, ParsedInputType.ONLY_FOR_P,
                    PreferredLanguageType.PFL_LANG_EN, CapitalizationType.MIXED_CASE, siResponse, subInfo
            );

            Listing serviceResult = siResponse.value.getServiceResult();
            result.setStreetAddressLines(serviceResult.getStreetName());
            result.setLocality(serviceResult.getLocality());
            result.setProvince(serviceResult.getProvince());
            result.setCountry(serviceResult.getCountry());
            result.setPostalCode(serviceResult.getPostalCode());
            result.setConfidencePercentage(serviceResult.getResultPercentage());

            result.setStatusNbr(siResponse.value.getServiceStatus().getStatusNbr());
            result.setStatusDescription(siResponse.value.getServiceStatus().getStatusDescription());
        } catch (ServiceException e) {
            result.setStatusNbr(500);
            result.setStatusDescription(ERROR_MESSAGE);
            throw e;
        } finally {
            return result;
        }
    }
}
