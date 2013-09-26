package com.picsauditing.jpa.entities;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TransactionTest {

	Transaction classUnderTest;

	@Before
	public void setup() {
		classUnderTest = new EmptyImplementation();
	}

	@Test
	public void updateAmountApplied_void () {
		classUnderTest.setStatus(TransactionStatus.Void);
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Void);
	}

	@Test
	public void updateAmountApplied_paid () {
		classUnderTest.setStatus(TransactionStatus.Paid);
		classUnderTest.setTotalAmount(BigDecimal.ZERO);
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Paid);
	}

	@Test
	public void updateAmountApplied_newlyPaid () {
		classUnderTest.setStatus(TransactionStatus.Unpaid);
		classUnderTest.setTotalAmount(BigDecimal.valueOf(20.00));
		classUnderTest.setAmountApplied(BigDecimal.valueOf(20.00));
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Paid);
	}

	@Test
	public void updateAmountApplied_newlyUnpaid () {
		classUnderTest.setStatus(TransactionStatus.Paid);
		classUnderTest.setTotalAmount(BigDecimal.valueOf(50.00));
		classUnderTest.setAmountApplied(BigDecimal.valueOf(20.00));
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Unpaid);
	}

	@Test
	public void updateAmountApplied_overPaid () {
		classUnderTest.setStatus(TransactionStatus.Unpaid);
		classUnderTest.setTotalAmount(BigDecimal.valueOf(200.00));
		classUnderTest.setAmountApplied(BigDecimal.valueOf(250.00));
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Paid);
	}

	@Test
	public void updateAmountApplied_underPaid () {
		classUnderTest.setStatus(TransactionStatus.Unpaid);
		classUnderTest.setTotalAmount(BigDecimal.valueOf(200.00));
		classUnderTest.setTotalAmount(BigDecimal.valueOf(150.00));
		classUnderTest.updateAmountApplied();
		assertTrue(classUnderTest.getStatus() == TransactionStatus.Unpaid);
	}

	@SuppressWarnings("serial")
	private class EmptyImplementation extends Transaction {
        @Deprecated // Only used for invoices and invoice credit memos
        @Transient
        public List<TransactionItem> getItems() {
            return null;
        }

        @Deprecated // Only used for invoices and invoice credit memos
        @Transient
        public TransactionItem getTaxItem() {
            return null;
        }

        @Deprecated // Only used for invoices and invoice credit memos
        @Transient
        public void updateTotalAmount() {
            return;
        }

		@Deprecated // Only used for invoices and invoice credit memos
		@Transient
		public BigDecimal getTaxlessSubtotal() {
			return null;
		}
	}
}
