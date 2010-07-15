<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?" />
</head>
<body>
<div id="main">
	<div id="bodyholder" style="margin-top: 50px; height: 250px;">
		<div id="content">
			<h1>Error!</h1>
			<div id="alert">
				The page your were trying to find is unavailable or you have mistyped the page name.<br/>
				Click 	<% if(permissions.isLoggedIn()){%>
							<a href="Home.action">
						<%} else { %>
							<a href="Login.action">
						<%} %>
							here</a> to return to PICS. 
			</div>
		</div>
	</div>
</div>
</body>
</html>