INSERT INTO app_properties (property, value)
	VALUES ('Toggle.PermissionGroups', 'true')
	ON DUPLICATE KEY UPDATE app_properties.value = 'true';