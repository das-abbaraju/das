package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.AccountUserTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.InvoiceCommissionTable;
import com.picsauditing.report.tables.InvoiceTable;
import com.picsauditing.report.tables.PaymentCommissionTable;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;

public class PaymentCommissionsModel extends AbstractModel {

	public PaymentCommissionsModel(Permissions permissions) {
		super(permissions, new PaymentCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec paymentCommission = new ModelSpec(null, "PaymentCommission");

        ModelSpec invoiceCommission = paymentCommission.join(PaymentCommissionTable.Commission);
        invoiceCommission.alias = "InvoiceCommission";

        ModelSpec invoice = invoiceCommission.join(InvoiceCommissionTable.Invoice);
        invoice.alias = "Invoice";

        ModelSpec account = invoice.join(InvoiceTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Required;

        ModelSpec payment = paymentCommission.join(PaymentCommissionTable.Payment);
        payment.alias = "Payment";

        ModelSpec accountUser = invoiceCommission.join(InvoiceCommissionTable.AccountUser);
        accountUser.alias = "CommissionUser";
        accountUser.category = FieldCategory.Commission;

        ModelSpec opAccount = accountUser.join(AccountUserTable.Account);
        opAccount.alias = "Operator";

        ModelSpec user = accountUser.join(AccountUserTable.User);
        user.alias = "CommissionUserUser";
        user.category = FieldCategory.Commission;

		return paymentCommission;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		setUrlForField(fields, "InvoiceInvoiceID", "InvoiceDetail.action?invoice.id={InvoiceInvoiceID}");
		setUrlForField(fields, "AccountName", "ContractorView.action?id={AccountID}");
        setUrlForField(fields, "OperatorName", "FacilitiesEdit.action?id={OperatorID}");
		formatActivationPoints(fields);

		return fields;
	}

	private void formatActivationPoints(Map<String, Field> fields) {
        // TODO Adding SQL should be done on the ReportField, not here
		Field activationPointsField = fields.get("PaymentCommissionActivationPoints".toUpperCase());
        if (activationPointsField == null) {
            return;
        }
		String activationPointsDatabaseColumnName = activationPointsField.getDatabaseColumnName();
		String formattedColumnName = "ROUND(" + activationPointsDatabaseColumnName + ", 2)";
		activationPointsField.setDatabaseColumnName(formattedColumnName);
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
		whereClause = restrictSalesRepsToTheirOwnRows(whereClause);

		return whereClause;
	}

	private String restrictSalesRepsToTheirOwnRows(String whereClause) {
		Set<Integer> groupIds = permissions.getAllInheritedGroupIds();
		if (CollectionUtils.isNotEmpty(groupIds) && !groupIds.contains(User.GROUP_MANAGER)
				&& groupIds.contains(User.GROUP_SALES_REPS)) {
			whereClause += Strings.EMPTY_STRING + "AccountUser.userID = " + permissions.getUserId();
		}
		return whereClause;
	}
}