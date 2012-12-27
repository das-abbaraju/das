package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.AccountUserTable;
import com.picsauditing.report.tables.InvoiceCommissionTable;
import com.picsauditing.report.tables.InvoiceTable;
import com.picsauditing.report.tables.PaymentCommissionTable;

public class PaymentCommissionModel extends AbstractModel {

	public PaymentCommissionModel(Permissions permissions) {
		super(permissions, new PaymentCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec paymentCommission = new ModelSpec(null, "PaymentCommission");

		ModelSpec invoiceCommission = joinToInvoiceCommission(paymentCommission);
		ModelSpec invoice = joinToInvoice(invoiceCommission);
		ModelSpec account = joinToAccount(invoice);

		joinToUserForContact(account);
		joinToPayment(paymentCommission);

		ModelSpec accountUser = joinToAccountUser(invoiceCommission);
		joinToUser(accountUser);

		return paymentCommission;
	}

	private ModelSpec joinToInvoiceCommission(ModelSpec paymentCommission) {
		ModelSpec invoiceCommission = paymentCommission.join(PaymentCommissionTable.Commission);
		invoiceCommission.alias = "InvoiceCommission";
		return invoiceCommission;
	}

	private ModelSpec joinToPayment(ModelSpec paymentCommission) {
		ModelSpec payment = paymentCommission.join(PaymentCommissionTable.Payment);
		payment.alias = "PaymentCommission";
		return payment;
	}

	private ModelSpec joinToInvoice(ModelSpec invoiceCommission) {
		ModelSpec invoice = invoiceCommission.join(InvoiceCommissionTable.Invoice);
		invoice.alias = "Invoice";
		return invoice;
	}

	private ModelSpec joinToAccountUser(ModelSpec invoiceCommission) {
		ModelSpec accountUser = invoiceCommission.join(InvoiceCommissionTable.AccountUser);
		accountUser.alias = "AccountUser";
		return accountUser;
	}

	private ModelSpec joinToAccount(ModelSpec invoice) {
		ModelSpec account = invoice.join(InvoiceTable.Account);
		account.alias = "Account";
		return account;
	}

	private ModelSpec joinToUser(ModelSpec accountUser) {
		ModelSpec user = accountUser.join(AccountUserTable.User);
		user.alias = "User";
		return user;
	}

	private ModelSpec joinToUserForContact(ModelSpec account) {
		ModelSpec contactUser = account.join(AccountTable.Contact);
		return contactUser;
	}
	
	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		setUrlForField(fields, "InvoiceInvoiceID", "InvoiceDetail.action?invoice.id={InvoiceInvoiceID}");
		setUrlForField(fields, "AccountName", "ContractorView.action?id={AccountID}");
		return fields;
	}
	
	private Field setUrlForField(Map<String, Field> availableFields, String fieldKey, String url) {
		Field field = availableFields.get(fieldKey.toUpperCase());
		field.setUrl(url);
		return field;
	}

}