package com.picsauditing.report.models;


public class ModelFactory {
	static public com.picsauditing.report.models.ModelBase getBase(ModelType type) {

		if (type.equals(ModelType.Accounts))
			return new QueryAccount();
		if (type.equals(ModelType.Contractors))
			return new QueryAccountContractor();
		if (type.equals(ModelType.ContractorAudits))
			return new QueryAccountContractorAudit();

		return null;
	}
}
