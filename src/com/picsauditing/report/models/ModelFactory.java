package com.picsauditing.report.models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Permissions;

public class ModelFactory {

	private static final Logger logger = LoggerFactory.getLogger(ModelFactory.class);

	public static AbstractModel build(ModelType type, Permissions permissions) {

		// We might want to reconsider the naming convention between ModelType
		// and classes that extend BaseModel
		// We could make them the same and use reflection

		if (type.equals(ModelType.Accounts))
			return new AccountModel(permissions);
		if (type.equals(ModelType.Contractors))
			return new AccountContractorModel(permissions);
		if (type.equals(ModelType.ContractorAudits))
			return new AccountContractorAuditModel(permissions);
		if (type.equals(ModelType.ContractorAuditOperators))
			return new AccountContractorAuditOperatorModel(permissions);
		if (type.equals(ModelType.ContractorNumbers))
			return new ContractorNumberModel(permissions);
		if (type.equals(ModelType.ContractorOperators))
			return new ContractorOperatorModel(permissions);
		if (type.equals(ModelType.Invoices))
			return new InvoiceModel(permissions);
		if (type.equals(ModelType.InvoiceCommissions))
			return new InvoiceCommissionModel(permissions);
		if (type.equals(ModelType.PaymentCommissions))
			return new PaymentCommissionModel(permissions);
		if (type.equals(ModelType.Operators))
			return new AccountOperatorModel(permissions);

		logger.warn("WARNING: ModelFactory failed to define Model for type = {}", type);

		return null;
	}
}
