INSERT INTO token (tokenName,listType,velocityCode)
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

UPDATE IGNORE email_template
SET body = '<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">
<html xmlns=\"http://www.w3.org/1999/xhtml\">
<body style=\"width: 100%; margin: 0; padding: 0px;\">
<ReportName><br />
<ReportDescription><br />
<a href=<ReportLink>>View Full Report</a><br />
<ReportData><br />
#if($notFullReport)
	This is not a full report.<br />
	<a href=<ReportLink>>Log in and view the full report in PICS Organizer.</a><br />
	<br />
#end
You are subscribed to receive this report <SubscriptionFrequency>.<br/>
<a href=<LinkToSubscriptionPage>>Edit or cancel your subscription.</a><br />
</body></html>'
WHERE id = 350;
