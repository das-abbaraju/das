<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Cron</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>

<h1>Contractor Cron</h1>
<s:form id="form" method="GET">
	<fieldset class="form">
	<h2 class="formLegend">Custom Options</h2>
	<ol>
		<li><label>Contractor ID:</label> <s:textfield name="conID" />
			<s:if test="conID > 0"><a href="ContractorView.action?id=<s:property value="conID"/>">Account Summary</a></s:if>
		</li>
		<li><label>Steps:</label> <s:select name="steps"
			list="stepValues" multiple="true" value="%{'All'}" size="15" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
	<button class="picsbutton positive" name="button" value="Run"
		type="submit" onclick="$(this).attr('disabled', true); $('#form').submit();">Run</button>
	</fieldset>
</s:form>

<s:include value="../actionMessages.jsp" />

<h2>Running Process(es):</h2>
<ul>
	<s:iterator value="manager">
		<li><s:date name="startTime" nice="true" />: <s:iterator
			value="queue" id="qConID">
			<s:property value="#qConID" />
		</s:iterator></li>
	</s:iterator>
</ul>

</body>
</html>
