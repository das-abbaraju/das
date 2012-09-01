package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;


public class InvoiceCommissionModelTest {
	@Test
	public void testAvailableFields() throws Exception {
		InvoiceCommissionModel model = new InvoiceCommissionModel();
		Permissions permissions = EntityFactory.makePermission();

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);
		
		assertFalse("accountFax is Low importance", availableFields.containsKey("accountFax".toUpperCase()));
		assertTrue("invoiceCommissionRecipientUserName is required", availableFields.containsKey("invoiceCommissionRecipientUserName".toUpperCase()));
		
		assertEquals("OK if close to expected because we added a few fields", 20, availableFields.size());
	}


}
