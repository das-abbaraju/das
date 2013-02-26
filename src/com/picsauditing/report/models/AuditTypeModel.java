package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AuditTypeTable;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.UserTable;

public class AuditTypeModel extends AbstractModel {

	public AuditTypeModel(Permissions permissions) {
		super(permissions, new AuditTypeTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec auditType = new ModelSpec(null, "AuditType");
		auditType.join(AuditTypeTable.Operator);

		ModelSpec createdBy = auditType.join(AuditTypeTable.CreatedBy);
		createdBy.join(UserTable.Account).minimumImportance = FieldImportance.Required;

		return auditType;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field auditTypeName = fields.get("AuditTypeName".toUpperCase());
		auditTypeName.setUrl("ManageAuditType.action?id={AuditTypeID}");

		Field createdByName = fields.get("AuditTypeCreatedByName".toUpperCase());
		createdByName.setUrl("UsersManage.action?account={AuditTypeCreatedByAccountID}&user={AuditTypeCreatedByID}");

		Field operatorName = fields.get("AuditTypeOperatorName".toUpperCase());
		operatorName.setUrl("FacilitiesEdit.action?operator={AuditTypeOperatorID}");

		return fields;
	}
}