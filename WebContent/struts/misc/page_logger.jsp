<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Page Logging</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<s:include value="../jquery.jsp"/>
</head>
<body>

<h1>Page Logging</h1>

<fieldset class="form"><legend><span>Logging Status</span></legend>
<ol>
	<li>
	<div id="loggingstatus">
	Logging is:&nbsp;
		<s:if test="loggingEnabled">
			<strong>Enabled</strong>&nbsp;
			<a class="picsbutton negative" href="PageLogger.action?button=disable">Disable Logging</a>
		</s:if>
		<s:else>
			<strong>Disabled</strong>&nbsp;
			<a class="picsbutton positive" href="PageLogger.action?button=enable">Enable Logging</a>
		</s:else>
	</div>
	</li>
</ol>
</fieldset>
<div class="clear"></div>

</body>
</html>