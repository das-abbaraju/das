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

    private GlobalAddressVerificationLocator locator;
    private String streetAddressLines;
    private String locality;
    private String province;
    private String country;
    private String postalCode;

    public GlobalAddressVerifier(String streetAddressLines, String locality, String province, String country, String postalCode) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HYSTRIX_COMMAND_GROUP))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(THREAD_TIMEOUT_MS)
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE)
                )
        );

        this.streetAddressLines = streetAddressLines;
        this.locality = locality;
        this.province = province;
        this.country = country;
        this.postalCode = postalCode;
        this.locator = new GlobalAddressVerificationLocator();
    }

    @Override
    protected Address run() throws ServiceException, RemoteException {
        Address result = new Address();

        SIWsOutputOfListingHolder siResponse = new SIWsOutputOfListingHolder();
        SISubscriptionInfoHolder subInfo = new SISubscriptionInfoHolder();


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

        return result;
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
