package com.picsauditing.actions;

import com.picsauditing.service.addressverifier.AddressHolder;
import com.picsauditing.service.addressverifier.AddressVerificationService;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressVerificationTest extends PicsActionSupport {
    @Autowired
    private AddressVerificationService addressVerificationService;

    private AddressHolder address;

    public String execute() throws Exception {

        if (address != null) {
            address = addressVerificationService.verify(address);
        }

        return SUCCESS;
    }

    public AddressHolder getAddress() {
        return address;
    }

    public void setAddress(AddressHolder address) {
        this.address = address;
    }
}
