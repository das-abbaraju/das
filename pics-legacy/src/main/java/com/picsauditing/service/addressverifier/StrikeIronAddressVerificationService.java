package com.picsauditing.service.addressverifier;

import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;

import javax.xml.rpc.ServiceException;

public class StrikeIronAddressVerificationService extends AddressVerificationService {

    private GlobalAddressVerifier globalAddressVerifier;

    public StrikeIronAddressVerificationService() {
        globalAddressVerifier = new GlobalAddressVerifier();
    }

    public StrikeIronAddressVerificationService(GlobalAddressVerifier globalAddressVerifier) {
        this.globalAddressVerifier = globalAddressVerifier;
    }

    public AddressHolder verify(AddressHolder address) throws AddressVerificationException {
        Address verified = null;
        AddressHolder correctedAddress = new AddressHolder();

        try {
            verified = globalAddressVerifier.verifyAddress(
                    address.getAddressLine1() + " " + address.getAddressLine2(),
                    address.getCity(),
                    address.getStateOrProvince(),
                    address.getCountry(),
                    address.getZipOrPostalCode());
        } catch (ServiceException e) {
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
