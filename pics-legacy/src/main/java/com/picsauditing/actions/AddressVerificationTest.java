package com.picsauditing.actions;

import com.picsauditing.service.addressverifier.AddressRequestHolder;
import com.picsauditing.service.addressverifier.AddressResponseHolder;
import com.picsauditing.service.addressverifier.AddressVerificationException;
import com.picsauditing.service.addressverifier.AddressVerificationService;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressVerificationTest extends PicsActionSupport {
    @Autowired
    private AddressVerificationService addressVerificationService;

    private AddressRequestHolder addressRequest;

    private AddressResponseHolder addressResponse;

    public String execute() throws Exception {

        if (addressRequest != null) {
            try {
                addressResponse = addressVerificationService.verify(addressRequest);
            } catch (AddressVerificationException e) {
                throw new Exception("Cannot verify address: " + e.getAddress() + " because " + e.getMessage(), e);
            }
            return "address-response";
        }
        return SUCCESS;
    }

    public AddressRequestHolder getAddressRequest() {
        return addressRequest;
    }

    public void setAddressRequest(AddressRequestHolder addressRequest) {
        this.addressRequest = addressRequest;
    }

    public AddressResponseHolder getAddressResponse() {
        return addressResponse;
    }

    public void setAddressResponse(AddressResponseHolder addressResponse) {
        this.addressResponse = addressResponse;
    }
}
