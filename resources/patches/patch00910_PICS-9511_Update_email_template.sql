update app_translation set msgValue = replace(msgValue, '$contractor.expiringPoliciesForInsuranceExpirationEmail', '${policies}') where msgKey = "EmailTemplate.10.translatedBody";
