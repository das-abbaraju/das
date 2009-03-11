<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>System Logging</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>

<h1>System Logging</h1>

<s:include value="../actionMessages.jsp" />

<div>[<a href="?button=clear">Clear All</a>]</div>

<fieldset class="form"><legend><span>Currently Logging</span></legend>
<ol>
	<s:iterator value="rules">
		<li><a class="remove" href="?button=delete&rule.name=<s:property value="name" />"><s:property value="name" /></a></li>
	</s:iterator>
</ol>
</fieldset>

<fieldset class="form"><legend><span>Logging Options</span></legend>
<ol>
	<!-- List All System Logging Options here -->

	<li><a class="edit" href="?button=add&rule.name=AuditCategoryAction.execute">AuditCategoryAction.execute</a></li>
	<li><a class="edit" href="?button=add&rule.name=AuditCriteriaAnswerBuilder.build">AuditCriteriaAnswerBuilder.build</a></li>
	<li><a class="edit" href="?button=add&rule.name=CaoStatus">CaoStatus</a></li>
	<li><a class="edit" href="?button=add&rule.name=CC_Hash_Errors">CC_Hash_Errors</a></li>
	<li><a class="edit" href="?button=add&rule.name=ContractorCron">ContractorCron</a></li>
	<li><a class="edit" href="?button=add&rule.name=ContractorFlagAction">ContractorFlagAction</a></li>
	<li><a class="edit" href="?button=add&rule.name=Flag.calculate">Flag.calculate</a></li>
	<li><a class="edit" href="?button=add&rule.name=LoggerConfig.execute">LoggerConfig.execute</a></li>
	<li><a class="edit" href="?button=add&rule.name=ReportUserPermissionMatrix">ReportUserPermissionMatrix</a></li>
	<li><a class="edit" href="?button=add&rule.name=User.Permissions">User.Permissions</a></li>
	<li><a class="edit" href="?button=add&rule.name=BuildAudits">BuildAudits</a></li>
	<li><a class="edit" href="?button=add&rule.name=BuildAudits|AuditOperators">BuildAudits|AuditOperators</a></li>
	<li><a class="edit" href="?button=add&rule.name=BuildAudits|AuditCategories">BuildAudits|AuditCategories</a></li>

	<!-- End of System Logging Options -->
</ol>
</fieldset>
<div class="clear"></div>

</body>
</html>