package com.picsauditing.service.addressverifier;

import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;

import javax.xml.rpc.ServiceException;

public class StrikeIronAddressVerificationService extends AddressVerificationService {
    private GlobalAddressVerifier globalAddressVerifier;

    public AddressHolder verify(AddressHolder address) throws AddressVerificationException {
        Address verified = null;
        AddressHolder correctedAddress = new AddressHolder();

        try {
            globalAddressVerifier = getGlobalAddressVerifier(address);
            verified = globalAddressVerifier.execute();
        } catch (Exception e) {
            throw new AddressVerificationException(e.getMessage(), e, address);
        }
        if (verified.getStatusNbr() >= 500) {
            throw new AddressVerificationException(verified.getStatusDescription(), address);
        }

        correctedAddress.setAddressLine1(verified.getStreetAddressLines());
        correctedAddress.setCity(verified.getLocality());
        correctedAddress.setStateOrProvince(verified.getProvince());
        correctedAddress.setZipOrPostalCode(verified.getPostalCode());
        correctedAddress.setCountry(verified.getCountry());

        correctedAddress.setResultStatus(parseResultCode(verified.getStatusNbr()));
        correctedAddress.setStatusDescription(verified.getStatusDescription());
        correctedAddress.setConfidencePercent(verified.getConfidencePercentage());

        return correctedAddress;
    }

    private GlobalAddressVerifier getGlobalAddressVerifier(AddressHolder address) {
        if (globalAddressVerifier == null) {
            return new GlobalAddressVerifier(
                    address.getAddressLine1() + " " + address.getAddressLine2(),
                    address.getCity(),
                    address.getStateOrProvince(),
                    address.getCountry(),
                    address.getZipOrPostalCode());
        }
        else {
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
