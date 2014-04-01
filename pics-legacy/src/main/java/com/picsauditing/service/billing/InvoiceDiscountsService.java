package com.picsauditing.service.billing;

import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.contractor.TopLevelOperatorFinder;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDiscountsService {
    @Autowired
    private InvoiceFeeDAO invoiceFeeDAO;
    @Autowired
    private TopLevelOperatorFinder topLevelOperatorFinder;

    public static final OperatorAccount SUNCOR = OperatorAccount.builder().id(OperatorAccount.SUNCOR).build();

    private static final BigDecimal ZERO = new BigDecimal(0);

    public List<InvoiceItem> applyDiscounts(ContractorAccount contractor, List<InvoiceItem> items) {
        return applySuncor2014Discounts(items, contractor);
    }

    private List<InvoiceItem> applySuncor2014Discounts(List<InvoiceItem> items, ContractorAccount contractor) {
        List<InvoiceItem> discountsToBeAdded = new ArrayList<>();

        if (eligibleForSuncorDiscount(contractor)) {
            for (InvoiceItem invoiceItem: items) {
                InvoiceFee invoiceFee = invoiceItem.getInvoiceFee();

                InvoiceFee discountInvoiceFee = findSuncor2014DiscountFee(invoiceFee);
                if (discountInvoiceFee != null) {
                    discountsToBeAdded.add(createDiscount(discountInvoiceFee));
                }
            }
        }
        return discountsToBeAdded;
    }

    private boolean eligibleForSuncorDiscount(ContractorAccount contractor) {
        if (contractor.getCountry().isUS() || contractor.getCountry().isCanada()) {
            List<OperatorAccount> topLevelOperators = topLevelOperatorFinder.findAllTopLevelOperators(contractor);
            return topLevelOperators.size() <= 1 && topLevelOperators.contains(SUNCOR);
        } else {
            return false;
        }
    }

    private InvoiceFee findSuncor2014DiscountFee(InvoiceFee invoiceFee) {
        return findSuncor2014DiscountFee(invoiceFee.getFeeClass(), invoiceFee.getMinFacilities());
    }

    private InvoiceFee findSuncor2014DiscountFee(FeeClass feeClass, int minFacilities) {
        return invoiceFeeDAO.findDiscountByNumberOfOperatorsAndClass(feeClass,
                minFacilities, OperatorAccount.SUNCOR);
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee, BigDecimal amount) {
        return InvoiceItem.builder()
                .invoiceFee(invoiceFee)
                .amount(amount)
                .originalAmount(ZERO)
                .build();
    }

    private InvoiceItem createDiscount(InvoiceFee invoiceFee) {
        return createDiscount(invoiceFee, invoiceFee.getAmount().negate());
    }

}
