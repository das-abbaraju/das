<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%@ taglib uri="sitemesh-decorator" prefix="decorator"%>
<decorator:usePage id="thisPage" />
<html>
<head>
<title>PICS - <decorator:title default="PICS" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link href="css/marketing.css" rel="stylesheet" type="text/css" />
<decorator:head />
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0"
	topmargin="0" marginwidth="0" marginheight="0"
	onload="<decorator:getProperty property="body.onload" />">
<table width="100%" height="100%" border="0" cellpadding="0"
	cellspacing="0">
	<tr>
		<td valign="top">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="72"
					bgcolor="<decorator:getProperty property="meta.color" default="#993300" />">&nbsp;</td>
			</tr>
		</table>
		</td>
		<td width="657" valign="top">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<form action="login.jsp" method="post">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td width="146" height="218" align="center" valign="top"><a
							href="index.jsp"><img src="images/logo.gif" alt="Home"
							width="146" height="145" border="0"></a><br>
						<%
							if (permissions.isLoggedIn())
							{
						%> <a class="blueMain"
							href="Home.action">Back to PICS Online</a><br />
						<a class="blueMain" href="logout.jsp">Logout</a> <%
 	} else if (!thisPage.getRequest().getRequestURI().contains("login"))
 	{
 %>
						<table border="0" cellspacing="0" cellpadding="1">
							<tr>
								<td height="5"></td>
								<td></td>
							</tr>
							<tr>
								<td align="right" valign="middle">
								<p><img src="images/login_user.gif" alt="User Name"
									width="50" height="9">&nbsp;</p>
								</td>
								<td valign="middle"><input name="username" type="text"
									class="loginForms" size="9"></td>
							</tr>
							<tr>
								<td align="right" valign="middle"><img
									src="images/login_pass.gif" alt="Password" width="50"
									height="9">&nbsp;</td>
								<td valign="middle"><input name="password" type="password"
									class="loginForms" size="9"></td>
							</tr>
							<tr>
								<td class="forgotpassword" valign="middle">
									<a href="forgot_password.jsp"">Forgot<br>Password</a></td>
								<td><input name="Submit" type="image"
									src="images/button_login.jpg" width="65" height="28" border="0">
								</td>

							</tr>
							<tr>
								<td colspan="2" class="blueMain"></td>
							</tr>
						</table>
						<%
							}
						%>
						</td>
						<td valign="top">
						<table width="511" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td height="72">
								<table width="511" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td width="364"><object
											classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
											codebase="https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
											width="364" height="72">
											<param name="movie"
												value="flash/NAV_<decorator:getProperty property="meta.flashName" default="HOME" />.swf">
											<param name="quality" value="high">
											<embed
												src="flash/NAV_<decorator:getProperty property="meta.flashName" default="LOGIN" />.swf"
												quality="high"
												pluginspage="https://www.macromedia.com/go/getflashplayer"
												type="application/x-shockwave-flash" width="364" height="72"></embed>
										</object></td>
										<td><img src="images/squares_home.gif" width="147"
											height="72"></td>
									</tr>
								</table>
								</td>
							</tr>
							<tr>
								<%
									if (thisPage.getRequest().getRequestURI().contains("index.jsp"))
									{
								%>
								<td height="146"><object
									classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
									codebase="https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0"
									width="511" height="146">
									<param name="movie" value="flash/home_feature.swf">
									<param name="quality" value="high">
									<embed src="flash/home_feature.swf" quality="high"
										pluginspage="https://www.macromedia.com/go/getflashplayer"
										type="application/x-shockwave-flash" width="511" height="146"></embed>
								</object></td>
								<%
									} else
									{
								%>
								<td height="146"><img
									src="images/photo_<decorator:getProperty property="meta.iconName" default="login" />.jpg"
									width="510" height="146"></td>
								<%
									}
								%>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</form>
				</td>
			</tr>
			<tr>
				<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top"><decorator:body /> <br />
						<br />
						<br />
						</td>
					</tr>
				</table>
				</td>
			</tr>

		</table>
		</td>
		<td valign="top">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td height="72"
					bgcolor="<decorator:getProperty property="meta.color" default="#993300" />">&nbsp;</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr bgcolor="#003366">
		<td height="72">&nbsp;</td>
		<td height="72" align="center" valign="middle" class="footer">&copy;
		Copyright 2008 PICS</td>
		<td height="72" valign="top">&nbsp;</td>
	</tr>
</table>
<script type="text/javascript" src="js/ieupdate.js"></script>
<%@ include file="../includes/statcounter.jsp"%>
</body>
</html>
