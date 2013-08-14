package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

/**
 * Created with IntelliJ IDEA.
 * User: MDo
 * Date: 8/14/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvoiceFeeCountryTable extends AbstractTable {

    public InvoiceFeeCountryTable() {
        super("invoice_fee_country");
        addFields(InvoiceFeeCountry.class);
    }

    protected void addJoins() {
    }
}
