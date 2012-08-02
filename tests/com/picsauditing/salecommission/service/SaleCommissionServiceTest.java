package com.picsauditing.salecommission.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceCommission;
import com.picsauditing.salecommission.service.strategy.ActivateInvoiceCommissionStrategy;
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*","org.apache.xerces.*" })
public class SaleCommissionServiceTest extends TestCase {
	
	@Mock Invoice invoice;
	
	ActivateInvoiceCommissionStrategy strategy;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		strategy = new ActivateInvoiceCommissionStrategy();
	}

	@Test
	public void testRegistrationInvoiceCommission(){
		
//		when(strategy.getInvoiceCommission(any(Invoice.class))).thenReturn(invoiceCommission);
//
//		InvoiceCommission ic = strategy.getInvoiceCommission(new Invoice());
//
//		assertEquals(50.0, ic.getPoints());
	}

}