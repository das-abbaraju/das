--liquibase formatted sql

--changeset gmeurer:4
--preConditions onFail MARK_RAN

-- add app property for braintree payment url

INSERT INTO app_properties(property, `value`, description)
VALUES ('brainTree.payment_url', 'https://secure.braintreepaymentgateway.com/api/transact.php', 'The url to our braintree payment gateway')
ON DUPLICATE KEY UPDATE description = VALUES(description);