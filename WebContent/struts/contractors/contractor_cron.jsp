<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Contractor Cron</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>

<h1>Contractor Cron</h1>
<s:form method="GET">
	<fieldset class="form">
	<legend><span>Custom Options</span></legend>
	<ol>
		<li><label>Contractor ID:</label> <s:textfield name="conID" /></li>
		<li><label>Steps:</label> <s:select name="steps" list="stepValues" multiple="5" /></li>
		<li><button class="picsbutton positive" name="button" value="Run" type="submit">Run</button></li>
	</ol>
	</fieldset>
</s:form>

<s:include value="../actionMessages.jsp" />
</body>
</html>
