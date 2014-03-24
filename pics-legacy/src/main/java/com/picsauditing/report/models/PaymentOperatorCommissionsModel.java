package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaymentOperatorCommissionsModel extends AbstractModel {

	public PaymentOperatorCommissionsModel(Permissions permissions) {
		super(permissions, new PaymentOperatorCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec paymentOperatorCommission = new ModelSpec(null, "PaymentOperatorCommission");

        ModelSpec invoiceOperatorCommission = paymentOperatorCommission.join(PaymentOperatorCommissionTable.Commission);
        invoiceOperatorCommission.alias = "InvoiceOperatorCommission";

        ModelSpec invoice = invoiceOperatorCommission.join(InvoiceOperatorCommissionTable.Invoice);
        invoice.alias = "Invoice";

        ModelSpec account = invoice.join(InvoiceTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Required;

        ModelSpec payment = paymentOperatorCommission.join(PaymentOperatorCommissionTable.Payment);
        payment.alias = "Payment";

        ModelSpec opAccount = invoiceOperatorCommission.join(InvoiceOperatorCommissionTable.Account);
        opAccount.alias = "OperatorCommission";

        ModelSpec operator = opAccount.join(AccountTable.Operator);

        operator.join(OperatorTable.Reporting);

		return paymentOperatorCommission;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		setUrlForField(fields, "InvoiceID", "InvoiceDetail.action?invoice.id={InvoiceID}");
		setUrlForField(fields, "AccountName", "ContractorView.action?id={AccountID}");
        setUrlForField(fields, "OperatorName", "FacilitiesEdit.action?id={OperatorID}");

		return fields;
	}

	private void setUrlForField(Map<String, Field> availableFields, String fieldKey, String url) {
		Field field = availableFields.get(fieldKey.toUpperCase());
		if (field == null) {
			return;
		}

		field.setUrl(url);
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		String whereClause = super.getWhereClause(filters);

		return whereClause;
	}
}