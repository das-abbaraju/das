package com.picsauditing.service.addressverifier;

import com.picsauditing.featuretoggle.Features;
import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;

public class StrikeIronAddressVerificationService extends AddressVerificationService {
    public static final String FEATURE_DISABLED_MESSAGE = "StrikeIron verification feature is disabled.";

    private GlobalAddressVerifier globalAddressVerifier;

    @Override
    public AddressResponseHolder verify(AddressRequestHolder addressRequest) throws AddressVerificationException {
        Address verifiedAddress = null;

        if (featureEnabled()) {
            try {
                globalAddressVerifier = getGlobalAddressVerifier(addressRequest);
                verifiedAddress = globalAddressVerifier.execute();
            } catch (Exception e) {
                throw new AddressVerificationException(e.getMessage(), e, addressRequest);
            }
            return buildAddressResponse(verifiedAddress);
        }
        else {
            return buildFeatureDisabledResponse();
        }
    }

    private AddressResponseHolder buildAddressResponse(Address verifiedAddress) {

        return AddressResponseHolder.builder()
                .addressLine1(verifiedAddress.getStreetAddressLines())
                .city(verifiedAddress.getLocality())
                .stateOrProvince(verifiedAddress.getProvince())
                .zipOrPostalCode(verifiedAddress.getPostalCode())
                .country(verifiedAddress.getCountry())
                .resultStatus(parseResultCode(verifiedAddress.getStatusNbr()))
                .statusDescription(verifiedAddress.getStatusDescription())
                .confidencePercentage(verifiedAddress.getConfidencePercentage())
                .build();
    }

    private AddressResponseHolder buildFeatureDisabledResponse() {
        AddressResponseHolder addressResponse = new AddressResponseHolder();
        addressResponse.setResultStatus(ResultStatus.SUCCESS);
        addressResponse.setStatusDescription(FEATURE_DISABLED_MESSAGE);
        return addressResponse;
    }

    private boolean featureEnabled() {
        try {
            return Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private GlobalAddressVerifier getGlobalAddressVerifier(AddressRequestHolder address) {
        if (globalAddressVerifier == null) {
            return new GlobalAddressVerifier(
                    address.getAddressBlob(),
                    address.getCountry(),
                    address.getZipOrPostalCode()
            );

        } else {
            return globalAddressVerifier;
        }
    }

    protected static ResultStatus parseResultCode(int resultCode) {
        if (resultCode >= 200 && resultCode <= 299) {
            return ResultStatus.SUCCESS;
        } else if (resultCode >= 300 && resultCode <= 399) {
            return ResultStatus.DATA_NOT_FOUND;
        } else if (resultCode >= 400 && resultCode <= 499) {
            return ResultStatus.INVALID_INPUT;
        }

        return ResultStatus.INTERNAL_ERROR;
    }


}
