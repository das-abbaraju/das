UPDATE email_template
SET body = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
</head>
<body style="width: 100%; margin: 0; padding: 0px;">
<ReportName><br />
<ReportDescription><br />
<a href="<ReportLink>">View Full Report</a><br />
<ReportData><br />
#if($notFullReport)
	This is not a full report.<br />
	<a href="<ReportLink>">Log in and view the full report in PICS Organizer.</a><br />
	<br />
#end
You are subscribed to receive this report <SubscriptionFrequency>.<br/>
<a href="<LinkToSubscriptionPage>">Edit or cancel your subscription.</a><br />
</body></html>'
WHERE id = 350;