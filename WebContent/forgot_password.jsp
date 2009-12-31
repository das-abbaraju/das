<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Forgot Password</title>
<meta name="help" content="Password_Recovery">
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
</head>
<body>
<form name="form1" method="post" action="Login.action" style="width: 500px">

<fieldset class="form" style="margin-top: 20px;"><legend><span>Forgot Password</span></legend>
<ol>
	<li>Enter the email address that you submitted when you created your PICS company profile and we will email you
	your username and password. If you have any problems, <a href="Contact.action" title="Contact PICS">contact us</a>
	directly.</li>
	<li><label>Email address:</label> <input name="email" type="text" size="40"></li>
	<li>
	<div class="buttons" style="padding-left: 200px; padding-bottom: 50px;">
	<button class="picsbutton positive" value="forgot" name="button" type="submit">Send Password</button>
	</div>
	</li>
	<li><a href="Login.action">Return to Login Page</a></li>
</ol>
</fieldset>
<fieldset class="form submit"></fieldset>
</form>

</body>
</html>