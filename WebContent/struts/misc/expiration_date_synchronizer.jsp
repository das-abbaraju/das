<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Expiration Date Synchronizer</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
</head>
<body>

<fieldset class="form"><legend><span>Expiration Date Synchronizer</span></legend>
	<s:form enctype="multipart/form-data" method="POST">
	<ol>
		<li>
			<button class="picsbutton positive" name="button" value="Synchronize Expiration Dates"
				type="submit">Synchronize Expiration Dates</button>
		</li>
		<li>
			<div style="margin-right: 10px"><s:include value="../actionMessages.jsp" /></div>
		</li>
	</ol>
	</s:form>
</fieldset>

</body>
</html>