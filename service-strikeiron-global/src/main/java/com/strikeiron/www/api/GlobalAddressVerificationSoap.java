/**
 * GlobalAddressVerificationSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

import com.strikeiron.www.Address;

public interface GlobalAddressVerificationSoap extends java.rmi.Remote {

    /**
     * This method will validate a basic address in one of the supported
     * countries.
     */
    public void basicVerify(String unregisteredUserEmail, String userID, String password, String streetAddressLines, String countrySpecificLocalityLine, String country, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder basicVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * This method will validate a number of basic addresses in supported
     * countries.
     */
    public void basicVerifyBatch(String unregisteredUserEmail, String userID, String password, BasicAddress[] addresses, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder basicVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * This method will validate and format an address in one of the
     * supported countries.
     */
    public void advancedVerify(String unregisteredUserEmail, String userID, String password, Address address, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder advancedVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * This method will validate and format a number of addresses
     * in the supported countries.
     */
    public void advancedVerifyBatch(String unregisteredUserEmail, String userID, String password, Address[] addresses, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder advancedVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * This method returns the countries where addresses can be validated
     */
    public void getSupportedCountries(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfCountryHolder getSupportedCountriesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * Gets all status codes a method in the service might return.
     */
    public void getStatusCodesForMethod(String unregisteredUserEmail, String userID, String password, String methodName, com.strikeiron.www.api.holders.SIWsOutputOfMethodStatusRecordHolder getStatusCodesForMethodResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * Get all statuses this service might return.
     */
    public void getStatusCodes(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfMethodStatusRecordHolder getStatusCodesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    /**
     * Get information about the web service
     */
    public void getServiceInfo(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfServiceInfoRecordHolder getServiceInfoResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException;

    public void getRemainingHits(String unregisteredUserEmail, String userID, String password, javax.xml.rpc.holders.IntHolder licenseStatusCode, javax.xml.rpc.holders.StringHolder licenseStatus, javax.xml.rpc.holders.IntHolder licenseActionCode, javax.xml.rpc.holders.StringHolder licenseAction, javax.xml.rpc.holders.IntHolder remainingHits, javax.xml.rpc.holders.BigDecimalHolder amount) throws java.rmi.RemoteException;
}
