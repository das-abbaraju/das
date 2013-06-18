package com.picsauditing.actions.contractors;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.TranslatableString;

public class ConInsureGuardTest extends PicsActionTest {

	private ConInsureGuard conInsureGuard;

	@Mock
	private AuditData auditData;
	@Mock
	private AuditQuestion auditQuestion;
	@Mock
	private AuditCategory auditCategory;
	@Mock
	private TranslatableString tString;
	@Mock
	private Map<Certificate, List<String>> certificates;
	@Mock
	private Certificate certificate;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		conInsureGuard = new ConInsureGuard();
	}

	@Test
	public void testAssociateClientSitesWithCertificate_AddsNewClientSites() throws Exception {
		Whitebox.invokeMethod(conInsureGuard, "initializeMaps");
		when(tString.toString()).thenReturn("abc");
		when(auditCategory.getName()).thenReturn(tString);
		when(auditQuestion.getCategory()).thenReturn(auditCategory);
		when(auditData.getQuestion()).thenReturn(auditQuestion);

		List<AuditData> audits = new ArrayList<AuditData>();
		audits.add(auditData);
		audits.add(auditData);

		when(certificate.getDescription()).thenReturn("A Description");

		when(certificates.get(certificate)).thenReturn(null);

		Whitebox.invokeMethod(conInsureGuard, "associateClientSitesWithCertificate", audits, certificates, certificate);

		verify(certificates).put(eq(certificate), anyListOf(String.class));
	}

}
