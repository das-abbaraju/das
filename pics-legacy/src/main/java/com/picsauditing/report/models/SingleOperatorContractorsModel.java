package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.SingleOperatorContractorTable;

import java.util.Map;

import static com.picsauditing.report.tables.SingleOperatorContractorTable.*;

public class SingleOperatorContractorsModel extends AbstractModel {

	public SingleOperatorContractorsModel(Permissions permissions) {
		super(permissions, new SingleOperatorContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec singleOperatorContractorsView = new ModelSpec(null, SingleOperatorContractorsView);
		ModelSpec account = singleOperatorContractorsView.join(SingleOperatorContractorTable.Account);
		account.alias = "Account";  // Re-use the Account labels

		return singleOperatorContractorsView;
	}

	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		addUrlsToFields(fields);

		return fields;
	}

	private void addUrlsToFields(Map<String, Field> fields) {
		String accountNameField = "AccountName";
		String accountIdField = "AccountID";
		Field accountName = fields.get(accountNameField.toUpperCase());
		accountName.setUrl("ContractorView.action?id={" + accountIdField +"}");

		String logoNameField = SingleOperatorContractorsView + logoAccountNameField;
		String logoIdField = SingleOperatorContractorsView + logoAccountIdField;
		Field logoAccountName = fields.get(logoNameField.toUpperCase());
		logoAccountName.setUrl("FacilitiesEdit.action?operator={" + logoIdField + "}");

	}

}
