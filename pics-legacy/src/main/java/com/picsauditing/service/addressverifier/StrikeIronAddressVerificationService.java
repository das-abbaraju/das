package com.picsauditing.service.addressverifier;

import com.strikeiron.www.Address;
import com.strikeiron.www.GlobalAddressVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.rpc.ServiceException;

public class StrikeIronAddressVerificationService extends AddressVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(StrikeIronAddressVerificationService.class);
    public static final String ERROR_MESSAGE = "Error verifying address: {}";

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
            logger.error(ERROR_MESSAGE, address.toString());
            logger.error(e.getMessage());
            throw new AddressVerificationException(e.getMessage());
        }
        if (verified.getStatusNbr() >= 500) {
            logger.error(ERROR_MESSAGE, address.toString());
            logger.error(verified.getStatusDescription());
            throw new AddressVerificationException(verified.getStatusDescription());
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
