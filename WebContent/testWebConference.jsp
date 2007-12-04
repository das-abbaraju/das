<%@ page language="java" errorPage="" %>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<body>
<form method="POST" name="intro" action="http://secure.webconference.com/mywebconference_com/mywebconf/mywebconf.php">
<input type="hidden" name="loginAttempt" value="true">
<input type="hidden" name="rid" value="R239651519718247"><br />
U/N:<input type="text" class="graybox" name="username" style="width:150px;" /><br />
P/W:<input type="password" class="graybox" name="password" style="width:150px;" /><br />
<input type="submit" value='Sign In'>
</form>

-------------------------<br>
<a href="http://www2.cslb.ca.gov/CSLB_LIBRARY/license+request.asp?LicNum=750580&EditForm=Y">License link</a>
<form method=post action="http://www2.cslb.ca.gov/CSLB_LIBRARY/license+request.asp" name=frmInput>
	<b>Contractor's License Number: </b>
	<input type="text" name="LicNum" size="8" maxlength="8" value="750580">
	<input type="hidden" name="EditForm" value="Yes">
	<br>
	<br>
	<input type="image" name="CheckLicense" SRC="../images/checklicense.gif" ALT="Check License" WIDTH="128" HEIGHT="20">
</form>			


<form method="POST" name="intro" action="testWebConference.jsp">
<input type="text" name="text1"><br />
<textarea name="text2"></textarea>
<input type="submit" value='Text'>
</form>
<%
	String text1 = request.getParameter("text1");
	String text2 = request.getParameter("text2");

	if (null != text2)
	for (int i = 0; i < text2.length(); i++) { 
		char ch = text2.charAt(i); 
		if (ch == 146)
			out.println("hey<br>");
		for (int j=1;j < 150; j++) {
//			ch = (char)j;
			if (j == ch)
				out.println("char: " + ch + " int: " + j + "<br>");
		}//for
	}
	out.println("here: " + com.picsauditing.PICS.Utilities.escapeNewLines(com.picsauditing.PICS.Utilities.escapeQuotes(text2)));
%>
</body>
</html>
