<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login</title>
<meta name="help" content="Logging_In">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<style>
fieldset.form input.login {
	padding: 3px;
	font-size: 16px;
	font-weight: bold;
	font-family: 'Trebuchet MS', Helvetica, sans-serif;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('ol.fieldhelp-list :input').focus(function() {
		var parent = $(this).parent();
		parent.addClass('fieldhelp-focused');
		var offset = parent.position();
		parent.find('.fieldhelp').css({top: offset.top + 'px', left: (parent.width()+offset.left+40) +'px'});
	}).blur(function() {
		$(this).parent().removeClass('fieldhelp-focused');
	});
});
</script>
</head>
<body onload="document.forms['login'].username.focus();">

<s:include value="../actionMessages.jsp"></s:include>

<s:form id="login" cssStyle="width: 500px;">
	<fieldset class="form"><legend><span>Login to PICS Organizer</span></legend>
	<ol class="fieldhelp-list">
		<li>
			<label>Username:</label>
			<s:textfield name="username" cssClass="login" />
			<div class="fieldhelp">
				<h3>Help</h3>
				<p>Forget your login information? <a href="AccountRecovery.action">Click here to recover it</a></p>
				<p>Are you a contractor? <a href="ContractorRegistration.action">Click to Register your company</a></p>
			</div>
		</li>
		<li><label>Password:</label> <s:password name="password" cssClass="login" /></li>
	</ol>
	</fieldset>
	<fieldset class="form submit">
		<input type="submit" class="picsbutton positive" name="button" value="Login" />
	</fieldset>
</s:form>

</body>
</html>