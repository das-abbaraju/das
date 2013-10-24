package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorAuditOperatorCemexModel extends AbstractModel {
	public ContractorAuditOperatorCemexModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Contractor");

        ModelSpec account = spec.join(ContractorTable.Account);
        account.minimumImportance = FieldImportance.Average;

        ModelSpec cemexPostEval = spec.join(ContractorTable.CemexPostEval);

        cemexPostEval.join(ContractorAuditTable.SingleCAO);

        for (Integer questionID : ContractorAuditTable.CEMEX_POST_EVAL_DATA.keySet()) {
            ModelSpec data = cemexPostEval.join(ContractorAuditTable.CEMEX_POST_EVAL_DATA.get(questionID));
            if (questionID == 16605 || questionID == 14719 || questionID == 14720) {
                data.minimumImportance = FieldImportance.Average;
            }
        }

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
        permissionQueryBuilder.setAccountAlias("ContractorAccount");

		String where = permissionQueryBuilder.buildWhereClause();

		if (!where.isEmpty()) {
			where += " AND ";
		}

		where += "ContractorCemexPostEvalCao.visible = 1";

		return where;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("ContractorAccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={ContractorAccountID}");

        for (Integer questionID : ContractorAuditTable.CEMEX_POST_EVAL_DATA.keySet()) {
            String question = "ContractorCemexPostEval" + ContractorAuditTable.CEMEX_POST_EVAL_DATA.get(questionID);
            String answerName = question + "Answer";
            Field answer = fields.get(answerName.toUpperCase());
            if (questionID == 17072)
                answer.setType(FieldType.Date);
            else if (questionID == 17069 || questionID == 17070 || questionID == 17071)
                answer.setType(FieldType.String);
            else {
                answer.setDatabaseColumnName("CASE " + question + ".answer WHEN 'Yes' THEN 1 WHEN 'X' THEN 1 ELSE 0 END");
                answer.setType(FieldType.Boolean);
            }
       }

        return fields;
	}
}