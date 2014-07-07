package com.strikeiron.www;

import com.AddressDoctor.validator2.addBatch.Batch.*;
import com.netflix.hystrix.*;
import com.strikeiron.www.api.*;
import com.strikeiron.www.api.holders.SISubscriptionInfoHolder;
import com.strikeiron.www.api.holders.SIWsOutputOfListingHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class GlobalAddressVerifier extends HystrixCommand<Address> {
    private static final Logger logger = LoggerFactory.getLogger(GlobalAddressVerifier.class);

    protected static final String USER_NAME = "picsauditing@strikeiron.com";
    protected static final String PASSWORD = "pics$567";
    protected static final String ERROR_MESSAGE = "StrikeIron service may be down";
    private static final String HYSTRIX_COMMAND_GROUP = "GlobalAddressVerifier";
    private static final int THREAD_TIMEOUT_MS = 5000;
    private static final int THREAD_POOL_SIZE = 20;
    private final String addressBlob;
    private final String zipOrPostalCode;

    private GlobalAddressVerificationLocator locator;
    private String country;

    public GlobalAddressVerifier(String addressBlob, String country, String zipOrPostalCode) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HYSTRIX_COMMAND_GROUP))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(THREAD_TIMEOUT_MS)
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE)
                )
        );
        this.addressBlob = addressBlob;
        this.country = country;
        this.zipOrPostalCode = zipOrPostalCode;
        this.locator = new GlobalAddressVerificationLocator();
    }

    @Override
    protected Address run() throws ServiceException, RemoteException {
        Address responseAddress = new Address();

        SIWsOutputOfListingHolder siResponse = new SIWsOutputOfListingHolder();
        SISubscriptionInfoHolder subInfo = new SISubscriptionInfoHolder();

        GlobalAddressVerificationSoap siService = locator.getGlobalAddressVerificationSoap();
        Address requestAddress = buildRequestAddress();

        siService.advancedVerify(null, USER_NAME, PASSWORD, requestAddress, true, true, true,
                CountryOfOriginType.COO_USA, CountryType.ISO_2, LineSeparatorType.LST_LF, ParsedInputType.ONLY_FOR_P,
                PreferredLanguageType.PFL_LANG_EN, CapitalizationType.MIXED_CASE, siResponse, subInfo
        );

        buildResponseAddress(responseAddress, siResponse.value);

        return responseAddress;
    }

    private Address buildRequestAddress() {
        Address requestAddress = new Address();
        requestAddress.setFormattedAddressLines(addressBlob);
        requestAddress.setCountry(country);
        requestAddress.setPostalCode(zipOrPostalCode);
        return requestAddress;
    }

    private void buildResponseAddress(Address responseAddress, SIWsOutputOfListing response) {
        Listing serviceResult = response.getServiceResult();
        responseAddress.setStreetAddressLines(serviceResult.getStreetName());
        responseAddress.setLocality(serviceResult.getLocality());
        responseAddress.setProvince(serviceResult.getProvince());
        responseAddress.setCountry(serviceResult.getCountry());
        responseAddress.setPostalCode(serviceResult.getPostalCode());
        responseAddress.setConfidencePercentage(serviceResult.getResultPercentage());
        responseAddress.setFormattedAddressLines(serviceResult.getFormattedAddress());

        responseAddress.setStatusNbr(response.getServiceStatus().getStatusNbr());
        responseAddress.setStatusDescription(response.getServiceStatus().getStatusDescription());
    }

    @Override
    protected Address getFallback() {
        logger.error(ERROR_MESSAGE);
        Address errorResult = new Address();
        errorResult.setStatusNbr(500);
        errorResult.setStatusDescription(ERROR_MESSAGE);

        return errorResult;
    }

    public void setLocator(GlobalAddressVerificationLocator locator) {
        this.locator = locator;
    }
}
