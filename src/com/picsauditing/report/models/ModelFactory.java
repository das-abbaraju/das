package com.picsauditing.report.models;

public class ModelFactory {
	static public com.picsauditing.report.models.ModelBase getBase(ModelType type) {

		// We might want to reconsider the naming convention between ModelType and classes that extend ModelBase
		// We could make them the same and use reflection

		if (type.equals(ModelType.Accounts))
			return new QueryAccount();
		if (type.equals(ModelType.Contractors))
			return new QueryAccountContractor();
		if (type.equals(ModelType.ContractorAudits))
			return new QueryAccountContractorAudit();
		if (type.equals(ModelType.Operators))
			return new QueryAccountOperator();

		return null;
	}
}
