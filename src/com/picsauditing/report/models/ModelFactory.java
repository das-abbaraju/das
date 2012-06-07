package com.picsauditing.report.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelFactory {
	private static final Logger logger = LoggerFactory.getLogger(ModelFactory.class);
	
	static public com.picsauditing.report.models.ModelBase getBase(ModelType type) {
       
		// We might want to reconsider the naming convention between ModelType and classes that extend ModelBase
		// We could make them the same and use reflection

		if (type.equals(ModelType.Accounts))
			return new QueryAccount();
		if (type.equals(ModelType.Contractors))
			return new QueryAccountContractor();
		if (type.equals(ModelType.ContractorAudits))
			return new QueryAccountContractorAudit();
		if (type.equals(ModelType.ContractorAuditOperators))
			return new QueryAccountContractorAuditOperator();
		if (type.equals(ModelType.Country))
			return new QueryCountry();
		if (type.equals(ModelType.Operators))
			return new QueryAccountOperator();

		logger.warn("WARNING: ModelFactory failed to define Model for type = {}", type);
		return null;
	}
}
