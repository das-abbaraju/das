package com.picsauditing.PICS;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.search.Database;

public class RegistrationRequestEmailHelperTest {
	private RegistrationRequestEmailHelper emailHelper;

	@Mock
	private ContractorRegistrationRequest request;
	@Mock
	private Database database;
	@Mock
	private EmailBuilder builder;
	@Mock
	private EmailQueue email;
	@Mock
	private EmailSender sender;
	@Mock
	private EmailTemplate template;
	@Mock
	private EntityManager entityManager;
	@Mock
	private OperatorAccount operator;
	@Mock
	private OperatorForm form;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		emailHelper = new RegistrationRequestEmailHelper();

		PicsTestUtil testUtil = new PicsTestUtil();
		testUtil.autowireEMInjectedDAOs(emailHelper, entityManager);

		Whitebox.setInternalState(emailHelper, "builder", builder);
		Whitebox.setInternalState(emailHelper, "sender", sender);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testBuildInitialEmail() throws Exception {
		when(entityManager.find(eq(EmailTemplate.class), anyInt())).thenReturn(template);

		emailHelper.buildInitialEmail(request);

		verify(builder).build();
		verify(entityManager).find(eq(EmailTemplate.class), anyInt());
	}

	@Test
	public void testBuildInitialEmail_NullRequest() throws Exception {
		emailHelper.buildInitialEmail(null);

		verify(builder, never()).build();
		verify(entityManager, never()).find(eq(EmailTemplate.class), anyInt());
	}

	@Test
	public void testGetContractorLetterFromHierarchy() throws Exception {
		List<OperatorForm> forms = new ArrayList<OperatorForm>();
		forms.add(form);

		when(form.getFormName()).thenReturn("Letter*");
		when(operator.getOperatorForms()).thenReturn(forms);
		when(request.getRequestedBy()).thenReturn(operator);

		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(request);
		assertNotNull(contractorLetter);
		assertEquals(form, contractorLetter);
	}

	@Test
	public void testGetContractorLetterFromHierarchy_NullRequestedBy() throws Exception {
		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(request);
		assertNull(contractorLetter);
	}

	@Test
	public void testGetContractorLetterFromHierarchy_LetterInCorporate() throws Exception {
		OperatorAccount corporate = mock(OperatorAccount.class);
		OperatorForm otherForm = mock(OperatorForm.class);

		List<OperatorForm> forms = new ArrayList<OperatorForm>();
		forms.add(otherForm);
		forms.add(form);

		when(corporate.getId()).thenReturn(1);
		when(corporate.getOperatorForms()).thenReturn(forms);
		when(form.getFormName()).thenReturn("Letter*");
		when(operator.getId()).thenReturn(2);
		when(operator.getParent()).thenReturn(corporate);
		when(operator.getOperatorForms()).thenReturn(new ArrayList<OperatorForm>());
		when(request.getRequestedBy()).thenReturn(operator);

		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(request);
		assertNotNull(contractorLetter);
		assertEquals(form, contractorLetter);
	}

	@Test
	public void testSendInitialEmail() throws Exception {
		List<OperatorForm> forms = new ArrayList<OperatorForm>();
		forms.add(form);

		when(builder.build()).thenReturn(email);
		when(form.getFormName()).thenReturn("Letter*");
		when(operator.getOperatorForms()).thenReturn(forms);
		when(request.getRequestedBy()).thenReturn(operator);

		emailHelper.sendInitialEmail(request, "ftpdir");

		verify(builder).build();
		verify(entityManager).find(eq(EmailTemplate.class), anyInt());
		verify(sender).send(any(EmailQueue.class));
	}
}
