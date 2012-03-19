package com.picsauditing.actions.audits;

import static org.junit.Assert.fail;

import org.junit.Test;

public class ContractorAuditDownloadTest {

	@Test
	public void testNullPointerFix() {
		ContractorAuditDownload contractorAuditDownload = new ContractorAuditDownload();
		try {
			// We removed Osha from this class
			// TODO create a more meaningful unit test
			// contractorAuditDownload.fillExcelOsha(contractorAuditDownload.new SheetStatus(), null, null);
		} catch (Exception e) {
			fail("An unexpected exception occurred during test execution");
		}
	}
	
}
