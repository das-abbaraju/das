package com.picsauditing.report.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelFactory {

	private static final Logger logger = LoggerFactory.getLogger(ModelFactory.class);

	public static AbstractModel build(ModelType type) {

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

		logger.warn("WARNING: ModelFactory failed to define Model for type = {}", type);

		return null;
	}
}
