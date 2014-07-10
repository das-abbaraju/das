package com.picsauditing.employeeguard.validators.document;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.util.Strings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

public class ProfileDocumentValidationUtilTest {

	@Mock
	private ThreadLocal<ActionContext> threadLocalActionContext;

	@Mock
	private ActionContext actionContext;

	@Mock
	private ActionInvocation actionInvocation;

	@Mock
	private ActionSupport actionSupport;

	@Mock
	private ValueStack valueStack;

	private static final String DUMMY_RESOURCE_BUNDLE_STRING= "DUMMY RESOURCE BUNDLE STRING";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		initResourceBundleMocking();
	}

	private void initResourceBundleMocking(){
		Whitebox.setInternalState(ActionContext.class, threadLocalActionContext);
		when(threadLocalActionContext.get()).thenReturn(actionContext);
		when(actionContext.getActionInvocation()).thenReturn(actionInvocation);
		when(actionInvocation.getAction()).thenReturn(actionSupport);
		when(actionContext.getValueStack()).thenReturn(valueStack);
		when(actionSupport.getText(any(String.class), any(String.class), anyList(),any(ValueStack.class))).thenReturn("DUMMY RESOURCE BUNDLE STRING");
	}

	@After
	public void tearDown() {
		Whitebox.setInternalState(ActionContext.class, "actionContext", new ThreadLocal<ActionContext>());
	}

	@Test
	public void testValidateExpirationDate_NoExpirationDateProvided() {
		DocumentForm fakeDocumentForm = build(0, 0, 0, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_DoesNotExpireTakesPrecedenceOverDate() {
		DocumentForm fakeDocumentForm = build(2020, 1, 1, true);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(Strings.EMPTY_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_InvalidExpirationDate() {
		DocumentForm fakeDocumentForm = build(-1, -1, -1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_TooFarInFuture() {
		DocumentForm fakeDocumentForm = build(2200, 1, 1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_TooFarInPast() {
		DocumentForm fakeDocumentForm = build(1900, 1, 1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_ValidExpirationDate() {
		DocumentForm fakeDocumentForm = build(2020, 1, 1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(Strings.EMPTY_STRING, result);
	}

	@Test
	public void testValidateExpirationDate_ValidDoesNotExpire() {
		DocumentForm fakeDocumentForm = build(0, 0, 0, true);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals(Strings.EMPTY_STRING, result);
	}

	private DocumentForm build(final int year, final int month, final int day, final boolean noExpiration) {
		DocumentForm documentForm = new DocumentForm();

		documentForm.setExpireYear(year);
		documentForm.setExpireMonth(month);
		documentForm.setExpireDay(day);
		documentForm.setNoExpiration(noExpiration);

		return documentForm;
	}

}
