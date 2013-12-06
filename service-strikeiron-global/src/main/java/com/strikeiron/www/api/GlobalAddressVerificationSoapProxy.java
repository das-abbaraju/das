package com.strikeiron.www.api;

import com.strikeiron.www.Address;

public class GlobalAddressVerificationSoapProxy implements GlobalAddressVerificationSoap {
    private String _endpoint = null;
    private GlobalAddressVerificationSoap globalAddressVerificationSoap = null;

    public GlobalAddressVerificationSoapProxy() {
        _initGlobalAddressVerificationSoapProxy();
    }

    public GlobalAddressVerificationSoapProxy(String endpoint) {
        _endpoint = endpoint;
        _initGlobalAddressVerificationSoapProxy();
    }

    private void _initGlobalAddressVerificationSoapProxy() {
        try {
            globalAddressVerificationSoap = (new GlobalAddressVerificationLocator()).getGlobalAddressVerificationSoap();
            if (globalAddressVerificationSoap != null) {
                if (_endpoint != null)
                    ((javax.xml.rpc.Stub) globalAddressVerificationSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
                else
                    _endpoint = (String) ((javax.xml.rpc.Stub) globalAddressVerificationSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
            }

        } catch (javax.xml.rpc.ServiceException serviceException) {
        }
    }

    public String getEndpoint() {
        return _endpoint;
    }

    public void setEndpoint(String endpoint) {
        _endpoint = endpoint;
        if (globalAddressVerificationSoap != null)
            ((javax.xml.rpc.Stub) globalAddressVerificationSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

    }

    public GlobalAddressVerificationSoap getGlobalAddressVerificationSoap() {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        return globalAddressVerificationSoap;
    }

    public void basicVerify(String unregisteredUserEmail, String userID, String password, String streetAddressLines, String countrySpecificLocalityLine, String country, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder basicVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.basicVerify(unregisteredUserEmail, userID, password, streetAddressLines, countrySpecificLocalityLine, country, basicVerifyResult, SISubscriptionInfo);
    }

    public void basicVerifyBatch(String unregisteredUserEmail, String userID, String password, BasicAddress[] addresses, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder basicVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.basicVerifyBatch(unregisteredUserEmail, userID, password, addresses, basicVerifyBatchResult, SISubscriptionInfo);
    }

    public void advancedVerify(String unregisteredUserEmail, String userID, String password, Address address, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder advancedVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.advancedVerify(unregisteredUserEmail, userID, password, address, formattedAddressWithOrganization, removeDiacritics, streetWithHNo, countryOfOrigin, resultCountryPreference, lineSeparator, parsedInput, preferredLanguage, capitalization, advancedVerifyResult, SISubscriptionInfo);
    }

    public void advancedVerifyBatch(String unregisteredUserEmail, String userID, String password, Address[] addresses, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder advancedVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.advancedVerifyBatch(unregisteredUserEmail, userID, password, addresses, formattedAddressWithOrganization, removeDiacritics, streetWithHNo, countryOfOrigin, resultCountryPreference, lineSeparator, parsedInput, preferredLanguage, capitalization, advancedVerifyBatchResult, SISubscriptionInfo);
    }

    public void getSupportedCountries(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfCountryHolder getSupportedCountriesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.getSupportedCountries(unregisteredUserEmail, userID, password, getSupportedCountriesResult, SISubscriptionInfo);
    }

    public void getStatusCodesForMethod(String unregisteredUserEmail, String userID, String password, String methodName, com.strikeiron.www.api.holders.SIWsOutputOfMethodStatusRecordHolder getStatusCodesForMethodResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.getStatusCodesForMethod(unregisteredUserEmail, userID, password, methodName, getStatusCodesForMethodResult, SISubscriptionInfo);
    }

    public void getStatusCodes(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfMethodStatusRecordHolder getStatusCodesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.getStatusCodes(unregisteredUserEmail, userID, password, getStatusCodesResult, SISubscriptionInfo);
    }

    public void getServiceInfo(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfServiceInfoRecordHolder getServiceInfoResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.getServiceInfo(unregisteredUserEmail, userID, password, getServiceInfoResult, SISubscriptionInfo);
    }

    public void getRemainingHits(String unregisteredUserEmail, String userID, String password, javax.xml.rpc.holders.IntHolder licenseStatusCode, javax.xml.rpc.holders.StringHolder licenseStatus, javax.xml.rpc.holders.IntHolder licenseActionCode, javax.xml.rpc.holders.StringHolder licenseAction, javax.xml.rpc.holders.IntHolder remainingHits, javax.xml.rpc.holders.BigDecimalHolder amount) throws java.rmi.RemoteException {
        if (globalAddressVerificationSoap == null)
            _initGlobalAddressVerificationSoapProxy();
        globalAddressVerificationSoap.getRemainingHits(unregisteredUserEmail, userID, password, licenseStatusCode, licenseStatus, licenseActionCode, licenseAction, remainingHits, amount);
    }


}