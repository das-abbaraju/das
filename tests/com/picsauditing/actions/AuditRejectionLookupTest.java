package com.picsauditing.actions;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.Action;
import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AuditRejectionCodeDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditRejectionCode;
import com.picsauditing.jpa.entities.AuditSubStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;

/*
 * populateJsonArray is tested with execute
 *
 */

public class AuditRejectionLookupTest extends PicsActionTest {
	
	private AuditRejectionLookup auditRejectionLookup;

	@Mock
	private AuditRejectionCodeDAO auditRejectionCodeDao;
	@Mock
	private ContractorAuditOperatorDAO contractorAuditOperatorDao;
	@Mock
	private ContractorAuditOperator cao;
	@Mock
	private List<ContractorAuditOperatorPermission> caops;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		auditRejectionLookup = new AuditRejectionLookup();
		super.setUp(auditRejectionLookup);
		PicsTestUtil.autowireDAOsFromDeclaredMocks(auditRejectionLookup, this);
	}
	
	@Test
	public void testExecute() throws Exception {
		when(cao.getCaoPermissions()).thenReturn(caops);
		when(contractorAuditOperatorDao.find(anyInt())).thenReturn(cao);
		
		List<AuditRejectionCode> codes = buildMockListRejectionCodes();
		when(auditRejectionCodeDao.findByCaoPermissions(anyListOf(ContractorAuditOperatorPermission.class))).thenReturn(codes);
		
		String strutsResponse = auditRejectionLookup.execute();
		String json = auditRejectionLookup.getJsonArray().toString();
		
		assertEquals(Action.SUCCESS, strutsResponse);
		assertEquals("[{\"id\":\"NoWaiverOfSubrogation\",\"value\":\"Policy must include; BP, its directors, officers, employees and agents as " +
				"additional insured and a Waiver of subrogation is required for ALL policies (except workers comp).\"}]", json);
	}
	
	@Test
	public void testPopulateJsonArray_EmptyList() throws Exception {
		JSONArray result = Whitebox.invokeMethod(auditRejectionLookup, "populateJsonArray", new ArrayList<AuditRejectionCode>());
		assertEquals("[]", result.toJSONString());	
	}
	
	@Test
	public void testPopulateJsonArray_NullList() throws Exception {
		JSONArray result = Whitebox.invokeMethod(auditRejectionLookup, "populateJsonArray", (Object[]) null);
		assertEquals("[]", result.toJSONString());
	}
	
	private List<AuditRejectionCode> buildMockListRejectionCodes() {
		List<AuditRejectionCode> mocks = new ArrayList<AuditRejectionCode>();
		mocks.add(buildMockAuditRejectionCode(123, AuditSubStatus.NoWaiverOfSubrogation, "Policy must include; BP, its directors, officers, employees and agents as additional insured " +
				"and a Waiver of subrogation is required for ALL policies (except workers comp)."));
		
		return mocks;
	}
	
	private AuditRejectionCode buildMockAuditRejectionCode(int operatorId, AuditSubStatus subStatus, String rejectionReason) {
		OperatorAccount operator = Mockito.mock(OperatorAccount.class);
		AuditRejectionCode auditRejectionCode = Mockito.mock(AuditRejectionCode.class);
		
		when(operator.getId()).thenReturn(operatorId);
		when(auditRejectionCode.getOperator()).thenReturn(operator);
		when(auditRejectionCode.getAuditSubStatus()).thenReturn(subStatus);
		when(auditRejectionCode.getRejectionReason()).thenReturn(rejectionReason);		
		
		return auditRejectionCode;
	}

}
