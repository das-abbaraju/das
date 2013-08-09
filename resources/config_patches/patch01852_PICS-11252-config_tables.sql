INSERT IGNORE INTO token (tokenName,listType,velocityCode)
VALUES ('ReportName','ALL','$report.name'),
('ReportDescription','ALL','$report.description'),
('ReportLink','ALL','$reportLink'),
('ReportData','ALL','$reportData'),
('LinkToSubscriptionPage','ALL','$linkToSubscriptionPage'),
('SubscriptionFrequency','ALL','$subscriptionFrequency');

SELECT t.tokenID INTO @mainPhone  FROM token t WHERE t.tokenName = 'ReportName';
SELECT t.tokenID INTO @salesPhone FROM token t WHERE t.tokenName = 'ReportDescription';
SELECT t.tokenID INTO @picsEmail  FROM token t WHERE t.tokenName = 'ReportLink';
SELECT t.tokenID INTO @picsName   FROM token t WHERE t.tokenName = 'ReportData';
SELECT t.tokenID INTO @address    FROM token t WHERE t.tokenName = 'LinkToSubscriptionPage';
SELECT t.tokenID INTO @address2    FROM token t WHERE t.tokenName = 'SubscriptionFrequency';

-- insert translations for new tokens
INSERT IGNORE INTO app_translation (msgKey, locale, msgValue) VALUES
(CONCAT('Token.', @mainPhone , '.velocityCode'), 'en', '$report.name'),
(CONCAT('Token.', @salesPhone, '.velocityCode'), 'en', '$report.description'),
(CONCAT('Token.', @picsEmail , '.velocityCode'), 'en', '$reportLink'),
(CONCAT('Token.', @picsName  , '.velocityCode'), 'en', '$reportData'),
(CONCAT('Token.', @address   , '.velocityCode'), 'en', '$linkToSubscriptionPage'),
(CONCAT('Token.', @address2  , '.velocityCode'), 'en', '$subscriptionFrequency');
