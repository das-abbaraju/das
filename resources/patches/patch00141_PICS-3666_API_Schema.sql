-- PICS-3666 
-- Adding support for an API key to be used with the REST API to link the request to a (special) user account.

ALTER TABLE `users` 
    add column `apiKey` varchar(36) DEFAULT NULL,
    add key `apiKey` (`apiKey`);    

