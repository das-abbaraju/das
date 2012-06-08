package com.picsauditing.report.models;

public class ModelFactory {
	static public BaseModel getBase(ModelType type) {

		// We might want to reconsider the naming convention between ModelType and classes that extend BaseModel
		// We could make them the same and use reflection

		if (type.equals(ModelType.Accounts))
			return new AccountModel();
		if (type.equals(ModelType.Contractors))
			return new AccountContractorModel();
		if (type.equals(ModelType.ContractorAudits))
			return new AccountContractorAuditModel();
		if (type.equals(ModelType.ContractorAuditOperators))
			return new AccountContractorAuditOperatorModel();
		if (type.equals(ModelType.Country))
			return new CountryModel();
		if (type.equals(ModelType.Operators))
			return new AccountOperatorModel();

		return null;
	}
}
