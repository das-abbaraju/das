insert IGNORE into app_properties (property, value)
VALUES ('Toggle.RequestNewContractorAccount', 'hasPermission("RequestNewContractor") || releaseToUserAudienceLevel(1)');