package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceDiscountsService {
    @Autowired
    private InvoiceFeeDAO invoiceFeeDAO;
    @Autowired
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public static final OperatorAccount SUNCOR = OperatorAccount.builder().id(OperatorAccount.SUNCOR).build();

    public List<InvoiceItem> applyDiscounts(ContractorAccount contractor, List<InvoiceItem> items) {
        return applySuncor2014Discounts(items, contractor);
    }

    private List<InvoiceItem> applySuncor2014Discounts(List<InvoiceItem> items, ContractorAccount contractor) {
        List<InvoiceItem> discountsToBeAdded = new ArrayList<>();
        List<OperatorAccount> topLevelOperators = topLevelOperatorFinder.findAllTopLevelOperators(contractor);

        if (topLevelOperators.size() <= 1 && topLevelOperators.contains(SUNCOR)) {
            for (InvoiceItem invoiceItem: items) {
                InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();

                InvoiceFee discountInvoiceFee = invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(invoiceFee.getFeeClass(),
                        invoiceFee.getMinFacilities(), OperatorAccount.SUNCOR);
                if (discountInvoiceFee != null) {

                    discountsToBeAdded.add(createDiscount(discountInvoiceFee));
                }
            }
        }
        return discountsToBeAdded;
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee) {
        InvoiceItem discount = new InvoiceItem();

        discount.setInvoiceFee(invoiceFee);
        discount.setAmount(invoiceFee.getAmount().negate());
        discount.setOriginalAmount(new BigDecimal(0));
        return discount;
    }
}
