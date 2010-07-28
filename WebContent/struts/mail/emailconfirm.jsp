<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Confirmation Page</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css" />
</head>
<body>
<h1>Email Confirmation</h1>
<div class="info">
You have sent <s:property value="ids.size()"/> emails to the queue.<br/>
</div>
<div class="instructions">
If you want to view the list of your emails in the email queue, please click on 
<a href="EmailQueueList.action?filter.status=Pending">Email Queue</a>.<br /><br /> 

If you want to send more emails, please click on 
<a href="EmailWizard.action">Email Wizard</a>.
</div>
</body>
</html>