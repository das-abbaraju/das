package com.picsauditing.auditbuilder;

import com.picsauditing.EntityFactory;
import com.picsauditing.auditbuilder.entities.ContractorDocument;
import com.picsauditing.auditbuilder.entities.DocumentCatData;
import com.picsauditing.auditbuilder.entities.AuditType;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContractorDocumentCategoriesTest {
    @Mock
    ContractorDocument audit;

	private List<DocumentCatData> categories;

	private AuditType testAuditType;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		testAuditType = EntityFactory.makeAuditType(100);
		categories = new ArrayList<>();
		createCategory();
	}

	private DocumentCatData createCategory() {
		return createCategory(0);
	}

	private DocumentCatData createCategory(int categoryID) {
		DocumentCatData acd = EntityFactory.makeAuditCatData();
		acd.getCategory().setId(categoryID);
		acd.getCategory().setAuditType(testAuditType);
        Calendar date = Calendar.getInstance();
        date.set(2001, 0, 1);
        acd.getCategory().setEffectiveDate(date.getTime());
        date.set(4000, 0, 1);
        acd.getCategory().setExpirationDate(date.getTime());
		categories.add(acd);
        acd.setAudit(audit);
		return acd;
	}
}
