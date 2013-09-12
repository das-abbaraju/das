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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

public class RegistrationRequestEmailHelperTest extends PicsTranslationTest {
	private RegistrationRequestEmailHelper emailHelper;

	@Mock
	private ContractorAccount contractor;
	@Mock
	private ContractorOperator relationship;
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
	@Mock
	private User user;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		emailHelper = new RegistrationRequestEmailHelper();

		PicsTestUtil testUtil = new PicsTestUtil();
		testUtil.autowireEMInjectedDAOs(emailHelper, entityManager);

		Whitebox.setInternalState(emailHelper, "builder", builder);
		Whitebox.setInternalState(emailHelper, "sender", sender);
	}

	@Test
	public void testBuildInitialEmail() throws Exception {
		when(entityManager.find(eq(EmailTemplate.class), anyInt())).thenReturn(template);

		emailHelper.buildInitialEmail(contractor, user, relationship);

		verify(builder).build();
		verify(entityManager).find(eq(EmailTemplate.class), anyInt());
	}

	@Test
	public void testBuildInitialEmail_NullRequest() throws Exception {
		emailHelper.buildInitialEmail(null, null, null);

		verify(builder, never()).build();
		verify(entityManager, never()).find(eq(EmailTemplate.class), anyInt());
	}

	@Test
	public void testGetContractorLetterFromHierarchy() throws Exception {
		List<OperatorForm> forms = new ArrayList<OperatorForm>();
		forms.add(form);

		when(form.getFormName()).thenReturn("Letter*");
		when(operator.getOperatorForms()).thenReturn(forms);
		when(relationship.getOperatorAccount()).thenReturn(operator);

		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(contractor, relationship);
		assertNotNull(contractorLetter);
		assertEquals(form, contractorLetter);
	}

	@Test
	public void testGetContractorLetterFromHierarchy_NullRequestedBy() throws Exception {
		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(contractor, relationship);
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
		when(relationship.getOperatorAccount()).thenReturn(operator);

		OperatorForm contractorLetter = emailHelper.getContractorLetterFromHierarchy(contractor, relationship);
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
		when(relationship.getOperatorAccount()).thenReturn(operator);

		emailHelper.sendInitialEmail(contractor, user, relationship, "ftpdir");

		verify(builder).build();
		verify(entityManager).find(eq(EmailTemplate.class), anyInt());
		verify(sender).send(any(EmailQueue.class));
	}
}
