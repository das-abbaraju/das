/**
 * GlobalAddressVerificationSoapStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.strikeiron.www.api;

import com.strikeiron.www.Address;

public class GlobalAddressVerificationSoapStub extends org.apache.axis.client.Stub implements GlobalAddressVerificationSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc[] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[9];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1() {
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("BasicVerify");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetAddressLines"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountrySpecificLocalityLine"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Country"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfListing"), SIWsOutputOfListing.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("BasicVerifyBatch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Addresses"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfBasicAddress"), BasicAddress[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicAddress"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyBatchResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus"), SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AdvancedVerify");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Address"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "Address"), Address.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "FormattedAddressWithOrganization"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemoveDiacritics"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetWithHNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountryOfOrigin"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryOfOriginType"), com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "ResultCountryPreference"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryType"), com.AddressDoctor.validator2.addBatch.Batch.CountryType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LineSeparator"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "LineSeparatorType"), com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "ParsedInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "ParsedInputType"), com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "PreferredLanguage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "PreferredLanguageType"), com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Capitalization"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CapitalizationType"), com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfListing"), SIWsOutputOfListing.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AdvancedVerifyBatch");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Addresses"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfAddress"), Address[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.strikeiron.com", "Address"));
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "FormattedAddressWithOrganization"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemoveDiacritics"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "StreetWithHNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "CountryOfOrigin"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryOfOriginType"), com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "ResultCountryPreference"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryType"), com.AddressDoctor.validator2.addBatch.Batch.CountryType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LineSeparator"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "LineSeparatorType"), com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "ParsedInput"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "ParsedInputType"), com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "PreferredLanguage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "PreferredLanguageType"), com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Capitalization"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CapitalizationType"), com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyBatchResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus"), SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetSupportedCountries");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetSupportedCountriesResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfCountry"), SIWsOutputOfSIWsResultArrayOfCountry.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetStatusCodesForMethod");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesForMethodResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfMethodStatusRecord"), SIWsOutputOfMethodStatusRecord.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetStatusCodes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfMethodStatusRecord"), SIWsOutputOfSIWsResultArrayOfMethodStatusRecord.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetServiceInfo");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetServiceInfoResult"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfServiceInfoRecord"), SIWsOutputOfSIWsResultArrayOfServiceInfoRecord.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"), SISubscriptionInfo.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetRemainingHits");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UnregisteredUserEmail"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "UserID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Password"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

    }

    public GlobalAddressVerificationSoapStub() throws org.apache.axis.AxisFault {
        this(null);
    }

    public GlobalAddressVerificationSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        this(service);
        super.cachedEndpoint = endpointURL;
    }

    public GlobalAddressVerificationSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");
        Class cls;
        javax.xml.namespace.QName qName;
        javax.xml.namespace.QName qName2;
        Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
        Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
        Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
        Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
        Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
        Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
        Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
        Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
        Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
        Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CapitalizationType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryOfOriginType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "CountryType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.CountryType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "LineSeparatorType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "ParsedInputType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://validator2.AddressDoctor.com/addBatch/Batch", "PreferredLanguageType");
        cachedSerQNames.add(qName);
        cls = com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(enumsf);
        cachedDeserFactories.add(enumdf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Address");
        cachedSerQNames.add(qName);
        cls = Address.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "AddressVerificationListingWithStatus");
        cachedSerQNames.add(qName);
        cls = AddressVerificationListingWithStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfAddress");
        cachedSerQNames.add(qName);
        cls = Address[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Address");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "Address");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfAddressVerificationListingWithStatus");
        cachedSerQNames.add(qName);
        cls = AddressVerificationListingWithStatus[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "AddressVerificationListingWithStatus");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "AddressVerificationListingWithStatus");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfBasicAddress");
        cachedSerQNames.add(qName);
        cls = BasicAddress[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicAddress");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicAddress");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfCountry");
        cachedSerQNames.add(qName);
        cls = Country[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Country");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "Country");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfMethodStatusRecord");
        cachedSerQNames.add(qName);
        cls = MethodStatusRecord[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodStatusRecord");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodStatusRecord");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfServiceInfoRecord");
        cachedSerQNames.add(qName);
        cls = ServiceInfoRecord[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ServiceInfoRecord");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "ServiceInfoRecord");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ArrayOfSIWsStatus");
        cachedSerQNames.add(qName);
        cls = SIWsStatus[].class;
        cachedSerClasses.add(cls);
        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus");
        qName2 = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus");
        cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
        cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicAddress");
        cachedSerQNames.add(qName);
        cls = BasicAddress.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Country");
        cachedSerQNames.add(qName);
        cls = Country.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "Listing");
        cachedSerQNames.add(qName);
        cls = Listing.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "MethodStatusRecord");
        cachedSerQNames.add(qName);
        cls = MethodStatusRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "ServiceInfoRecord");
        cachedSerQNames.add(qName);
        cls = ServiceInfoRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo");
        cachedSerQNames.add(qName);
        cls = SISubscriptionInfo.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfListing");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfListing.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfMethodStatusRecord");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfMethodStatusRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfCountry");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfSIWsResultArrayOfCountry.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfMethodStatusRecord");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfSIWsResultArrayOfMethodStatusRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsOutputOfSIWsResultArrayOfServiceInfoRecord");
        cachedSerQNames.add(qName);
        cls = SIWsOutputOfSIWsResultArrayOfServiceInfoRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfAddressVerificationListingWithStatus");
        cachedSerQNames.add(qName);
        cls = SIWsResultArrayOfAddressVerificationListingWithStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfCountry");
        cachedSerQNames.add(qName);
        cls = SIWsResultArrayOfCountry.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfMethodStatusRecord");
        cachedSerQNames.add(qName);
        cls = SIWsResultArrayOfMethodStatusRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfServiceInfoRecord");
        cachedSerQNames.add(qName);
        cls = SIWsResultArrayOfServiceInfoRecord.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultArrayOfSIWsStatus");
        cachedSerQNames.add(qName);
        cls = SIWsResultArrayOfSIWsStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsResultWithStatus");
        cachedSerQNames.add(qName);
        cls = SIWsResultWithStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

        qName = new javax.xml.namespace.QName("http://www.strikeiron.com", "SIWsStatus");
        cachedSerQNames.add(qName);
        cls = SIWsStatus.class;
        cachedSerClasses.add(cls);
        cachedSerFactories.add(beansf);
        cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            Class sf = (Class)
                                    cachedSerFactories.get(i);
                            Class df = (Class)
                                    cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        } else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                    cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                    cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        } catch (Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public void basicVerify(String unregisteredUserEmail, String userID, String password, String streetAddressLines, String countrySpecificLocalityLine, String country, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder basicVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/BasicVerify");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerify"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password, streetAddressLines, countrySpecificLocalityLine, country});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    basicVerifyResult.value = (SIWsOutputOfListing) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyResult"));
                } catch (Exception _exception) {
                    basicVerifyResult.value = (SIWsOutputOfListing) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyResult")), SIWsOutputOfListing.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void basicVerifyBatch(String unregisteredUserEmail, String userID, String password, BasicAddress[] addresses, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder basicVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/BasicVerifyBatch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyBatch"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password, addresses});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    basicVerifyBatchResult.value = (SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyBatchResult"));
                } catch (Exception _exception) {
                    basicVerifyBatchResult.value = (SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "BasicVerifyBatchResult")), SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void advancedVerify(String unregisteredUserEmail, String userID, String password, Address address, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfListingHolder advancedVerifyResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/AdvancedVerify");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerify"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password, address, new Boolean(formattedAddressWithOrganization), new Boolean(removeDiacritics), new Boolean(streetWithHNo), countryOfOrigin, resultCountryPreference, lineSeparator, parsedInput, preferredLanguage, capitalization});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    advancedVerifyResult.value = (SIWsOutputOfListing) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyResult"));
                } catch (Exception _exception) {
                    advancedVerifyResult.value = (SIWsOutputOfListing) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyResult")), SIWsOutputOfListing.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void advancedVerifyBatch(String unregisteredUserEmail, String userID, String password, Address[] addresses, boolean formattedAddressWithOrganization, boolean removeDiacritics, boolean streetWithHNo, com.AddressDoctor.validator2.addBatch.Batch.CountryOfOriginType countryOfOrigin, com.AddressDoctor.validator2.addBatch.Batch.CountryType resultCountryPreference, com.AddressDoctor.validator2.addBatch.Batch.LineSeparatorType lineSeparator, com.AddressDoctor.validator2.addBatch.Batch.ParsedInputType parsedInput, com.AddressDoctor.validator2.addBatch.Batch.PreferredLanguageType preferredLanguage, com.AddressDoctor.validator2.addBatch.Batch.CapitalizationType capitalization, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatusHolder advancedVerifyBatchResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/AdvancedVerifyBatch");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyBatch"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password, addresses, new Boolean(formattedAddressWithOrganization), new Boolean(removeDiacritics), new Boolean(streetWithHNo), countryOfOrigin, resultCountryPreference, lineSeparator, parsedInput, preferredLanguage, capitalization});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    advancedVerifyBatchResult.value = (SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyBatchResult"));
                } catch (Exception _exception) {
                    advancedVerifyBatchResult.value = (SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "AdvancedVerifyBatchResult")), SIWsOutputOfSIWsResultArrayOfAddressVerificationListingWithStatus.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void getSupportedCountries(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfCountryHolder getSupportedCountriesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/GetSupportedCountries");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetSupportedCountries"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    getSupportedCountriesResult.value = (SIWsOutputOfSIWsResultArrayOfCountry) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetSupportedCountriesResult"));
                } catch (Exception _exception) {
                    getSupportedCountriesResult.value = (SIWsOutputOfSIWsResultArrayOfCountry) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetSupportedCountriesResult")), SIWsOutputOfSIWsResultArrayOfCountry.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void getStatusCodesForMethod(String unregisteredUserEmail, String userID, String password, String methodName, com.strikeiron.www.api.holders.SIWsOutputOfMethodStatusRecordHolder getStatusCodesForMethodResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/GetStatusCodesForMethod");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesForMethod"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password, methodName});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    getStatusCodesForMethodResult.value = (SIWsOutputOfMethodStatusRecord) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesForMethodResult"));
                } catch (Exception _exception) {
                    getStatusCodesForMethodResult.value = (SIWsOutputOfMethodStatusRecord) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesForMethodResult")), SIWsOutputOfMethodStatusRecord.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void getStatusCodes(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfMethodStatusRecordHolder getStatusCodesResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/GetStatusCodes");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodes"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    getStatusCodesResult.value = (SIWsOutputOfSIWsResultArrayOfMethodStatusRecord) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesResult"));
                } catch (Exception _exception) {
                    getStatusCodesResult.value = (SIWsOutputOfSIWsResultArrayOfMethodStatusRecord) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetStatusCodesResult")), SIWsOutputOfSIWsResultArrayOfMethodStatusRecord.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void getServiceInfo(String unregisteredUserEmail, String userID, String password, com.strikeiron.www.api.holders.SIWsOutputOfSIWsResultArrayOfServiceInfoRecordHolder getServiceInfoResult, com.strikeiron.www.api.holders.SISubscriptionInfoHolder SISubscriptionInfo) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.strikeiron.com/GetServiceInfo");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetServiceInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    getServiceInfoResult.value = (SIWsOutputOfSIWsResultArrayOfServiceInfoRecord) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetServiceInfoResult"));
                } catch (Exception _exception) {
                    getServiceInfoResult.value = (SIWsOutputOfSIWsResultArrayOfServiceInfoRecord) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "GetServiceInfoResult")), SIWsOutputOfSIWsResultArrayOfServiceInfoRecord.class);
                }
                try {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo"));
                } catch (Exception _exception) {
                    SISubscriptionInfo.value = (com.strikeiron.www.api.SISubscriptionInfo) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "SISubscriptionInfo")), com.strikeiron.www.api.SISubscriptionInfo.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

    public void getRemainingHits(String unregisteredUserEmail, String userID, String password, javax.xml.rpc.holders.IntHolder licenseStatusCode, javax.xml.rpc.holders.StringHolder licenseStatus, javax.xml.rpc.holders.IntHolder licenseActionCode, javax.xml.rpc.holders.StringHolder licenseAction, javax.xml.rpc.holders.IntHolder remainingHits, javax.xml.rpc.holders.BigDecimalHolder amount) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://wsparam.strikeiron.com/StrikeIron/GlobalAddressVerification5/GlobalAddressVerification/GetRemainingHits");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.strikeiron.com", "SILicenseInfo"));

        setRequestHeaders(_call);
        setAttachments(_call);
        try {
            Object _resp = _call.invoke(new Object[]{unregisteredUserEmail, userID, password});

            if (_resp instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) _resp;
            } else {
                extractAttachments(_call);
                java.util.Map _output;
                _output = _call.getOutputParams();
                try {
                    licenseStatusCode.value = ((Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode"))).intValue();
                } catch (Exception _exception) {
                    licenseStatusCode.value = ((Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatusCode")), int.class)).intValue();
                }
                try {
                    licenseStatus.value = (String) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus"));
                } catch (Exception _exception) {
                    licenseStatus.value = (String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseStatus")), String.class);
                }
                try {
                    licenseActionCode.value = ((Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode"))).intValue();
                } catch (Exception _exception) {
                    licenseActionCode.value = ((Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseActionCode")), int.class)).intValue();
                }
                try {
                    licenseAction.value = (String) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction"));
                } catch (Exception _exception) {
                    licenseAction.value = (String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "LicenseAction")), String.class);
                }
                try {
                    remainingHits.value = ((Integer) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits"))).intValue();
                } catch (Exception _exception) {
                    remainingHits.value = ((Integer) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "RemainingHits")), int.class)).intValue();
                }
                try {
                    amount.value = (java.math.BigDecimal) _output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount"));
                } catch (Exception _exception) {
                    amount.value = (java.math.BigDecimal) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://www.strikeiron.com", "Amount")), java.math.BigDecimal.class);
                }
            }
        } catch (org.apache.axis.AxisFault axisFaultException) {
            throw axisFaultException;
        }
    }

}
