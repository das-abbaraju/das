package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;

public class FacilityBuilder {
    private Facility facility = new Facility();

    public FacilityBuilder operator(OperatorAccount childAccount) {
        facility.setOperator(childAccount);
        return this;
    }

    public FacilityBuilder corporate(OperatorAccount parentAccount) {
        facility.setCorporate(parentAccount);
        return this;
    }

    public Facility build() {
        return facility;
    }
}
