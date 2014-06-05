package com.picsauditing.employeeguard.validators.document;

import com.picsauditing.employeeguard.forms.contractor.DocumentForm;
import com.picsauditing.util.Strings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfileDocumentValidationUtilTest {

	@Test
	public void testValidateExpirationDate_NoExpirationDateProvided() {
		DocumentForm fakeDocumentForm = build(0, 0, 0, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals("Expiration date is required", result);
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

		assertEquals("Invalid Year-Month-Day", result);
	}

	@Test
	public void testValidateExpirationDate_TooFarInFuture() {
		DocumentForm fakeDocumentForm = build(2200, 1, 1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals("Expiration Date is too far in the future", result);
	}

	@Test
	public void testValidateExpirationDate_TooFarInPast() {
		DocumentForm fakeDocumentForm = build(1900, 1, 1, false);

		String result = ProfileDocumentValidationUtil.validateExpirationDate(fakeDocumentForm);

		assertEquals("Expiration Date is too far in the past", result);
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
