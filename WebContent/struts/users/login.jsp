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
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('#username').focus();
});
</script>
</head>
<body>

<s:include value="login_form.jsp"/>

</body>
</html>