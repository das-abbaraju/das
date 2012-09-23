package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;


public class InvoiceModelTest {
	@Test
	public void testAvailableFields() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		InvoiceModel model = new InvoiceModel(permissions);

		Map<String, Field> availableFields = model.getAvailableFields();

		assertEquals("OK if close to expected because we added a few fields", 53, availableFields.size());
	}

}
