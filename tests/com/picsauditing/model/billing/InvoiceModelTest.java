package com.picsauditing.model.billing;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.dao.InvoiceCommissionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.salecommission.invoice.strategy.CommissionAudit;
import com.picsauditing.util.Strings;

public class InvoiceModelTest {

	private InvoiceModel invoiceModel;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private Invoice invoice;
	@Mock
	private InvoiceCommissionDAO invoiceCommissionDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		invoiceModel = new InvoiceModel();

		Whitebox.setInternalState(invoiceModel, "invoiceCommissionDAO", invoiceCommissionDAO);
	}

	@Ignore
	@Test
	public void testGetCommissionDetails() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCommissionDetails_NullInvoice() {
		List<CommissionDetail> results = invoiceModel.getCommissionDetails(null);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetCommissionDetails_NoInvoiceCommissionsFound() {
		when(invoice.getId()).thenReturn(90);
		when(invoiceCommissionDAO.findByInvoiceId(90)).thenReturn(null);

		List<CommissionDetail> results = invoiceModel.getCommissionDetails(invoice);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetClientSiteServiceLevels() {
		List<CommissionAudit> fakeCommissionAudits = buildFakeCommissionAudits();

		Map<Integer, List<FeeClass>> results = invoiceModel.getClientSiteServiceLevels(fakeCommissionAudits);

		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(2, results.size());
		assertEquals(new ArrayList<FeeClass>(Arrays.asList(FeeClass.DocuGUARD, FeeClass.AuditGUARD)), results.get(1));
		assertEquals(
				new ArrayList<FeeClass>(Arrays.asList(FeeClass.AuditGUARD, FeeClass.InsureGUARD, FeeClass.DocuGUARD)),
				results.get(2));
	}

	@Test
	public void testGetClientSiteServiceLevels_NoCommissionAudits() {
		Map<Integer, List<FeeClass>> results = invoiceModel.getClientSiteServiceLevels(null);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetNumberOfSitesUsingService() {
		List<CommissionAudit> fakeCommissionAudits = buildFakeCommissionAudits();

		Map<FeeClass, Integer> results = invoiceModel.getNumberOfSitesUsingService(fakeCommissionAudits);

		assertNotNull(results);
		assertFalse(results.isEmpty());
		assertEquals(2, results.get(FeeClass.AuditGUARD).intValue());
		assertEquals(2, results.get(FeeClass.DocuGUARD).intValue());
		assertEquals(1, results.get(FeeClass.InsureGUARD).intValue());
	}

	private List<CommissionAudit> buildFakeCommissionAudits() {
		List<CommissionAudit> fakeCommissionAudits = new ArrayList<CommissionAudit>();
		fakeCommissionAudits.add(buildFakeCommissionAudit(1, FeeClass.DocuGUARD));
		fakeCommissionAudits.add(buildFakeCommissionAudit(1, FeeClass.AuditGUARD));
		fakeCommissionAudits.add(buildFakeCommissionAudit(2, FeeClass.AuditGUARD));
		fakeCommissionAudits.add(buildFakeCommissionAudit(2, FeeClass.InsureGUARD));
		fakeCommissionAudits.add(buildFakeCommissionAudit(2, FeeClass.DocuGUARD));
		return fakeCommissionAudits;
	}

	private CommissionAudit buildFakeCommissionAudit(int clientSiteId, FeeClass feeClass) {
		CommissionAudit commissionAudit = new CommissionAudit();
		commissionAudit.setClientSiteId(clientSiteId);
		commissionAudit.setFeeClass(feeClass);
		return commissionAudit;
	}

	@Test
	public void testGetNumberOfSitesUsingService_NoCommissionAudits() {
		Map<FeeClass, Integer> results = invoiceModel.getNumberOfSitesUsingService(null);

		assertNotNull(results);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testGetSortedClientSiteList() {
		List<ContractorOperator> mockContractorOperators = buildMockClientSites();
		when(contractor.getNonCorporateOperators()).thenReturn(mockContractorOperators);

		String sortedClientSites = invoiceModel.getSortedClientSiteList(contractor);

		assertEquals("SiteA, SiteB, SiteC", sortedClientSites);
	}

	@Test
	public void testGetSortedClientSiteList_EmptyList() {
		when(contractor.getNonCorporateOperators()).thenReturn(null);

		String sortedClientSites = invoiceModel.getSortedClientSiteList(contractor);

		assertEquals(Strings.EMPTY_STRING, sortedClientSites);
	}

	private List<ContractorOperator> buildMockClientSites() {
		List<ContractorOperator> clientSites = new ArrayList<ContractorOperator>(Arrays.asList(
				buildMockContractorOperator("SiteC", "No"), buildMockContractorOperator("SiteB", "Yes"),
				buildMockContractorOperator("SiteD", "Multiple"), buildMockContractorOperator("SiteA", "Yes")));

		return clientSites;
	}

	private ContractorOperator buildMockContractorOperator(String name, String doContractorsPay) {
		ContractorOperator contractorOperator = Mockito.mock(ContractorOperator.class);
		OperatorAccount operator = buildMockOperator(name, doContractorsPay);
		when(contractorOperator.getOperatorAccount()).thenReturn(operator);
		return contractorOperator;
	}

	private OperatorAccount buildMockOperator(String name, String doContractorsPay) {
		OperatorAccount operator = Mockito.mock(OperatorAccount.class);
		when(operator.getName()).thenReturn(name);
		when(operator.getDoContractorsPay()).thenReturn(doContractorsPay);
		return operator;
	}

}
