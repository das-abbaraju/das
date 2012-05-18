<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="EmailWizard.ConfirmationPage" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css?v=${version}" />
</head>
<body>
<h1><s:text name="EmailWizard.EmailConfirmation" /></h1>
<div class="info">
	<s:text name="EmailWizard.EmailsSentToQueue">
		<s:param value="%{ids.size}" />
	</s:text>
</div>
<div class="instructions">
	<s:text name="EmailWizard.QueueWizardInstructions" />
</div>
</body>
</html>