<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/forms.css?" />
</head>
<body>
<div id="main">
<div id="bodyholder" style="margin-top: 50px; height: 250px;">
<div id="content">
<h1>Page Not Found</h1>
<div id="error">We could not find the page you were trying access.
Please check the URL and try again. If you believe this was caused by a
bug on our website, then please let <a href="/Contact.action">us
know</a>.</div>

<p>
<%
	if (permissions.isLoggedIn()) {
%> <a href="Home.action"> <%
 	} else {
 %> <a href="Login.action"> <%
 	}
 %>Click here</a> to return to PICS.</p>
</div>
</div>
</div>
</body>
</html>