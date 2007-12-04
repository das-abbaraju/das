<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page isErrorPage="true" %>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="../PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="EEEEEE">
<p>&nbsp;</p>
<table width="500" border="0" align="center" cellpadding="15" cellspacing="0" bordercolor="#CCCCCC" bgcolor="#FFFFFF">
  <tr> 

    <td><span class="blueMain"><p>There was an exception thrown:<br> 
        <b><%= exception.getMessage() %></b></p>
<%
	java.io.StringWriter sw = new java.io.StringWriter();
	exception.printStackTrace(new java.io.PrintWriter(sw));
	String stacktrace = sw.toString();
	out.println("stacktrace:<br>" + com.picsauditing.PICS.Utilities.escapeHTML(stacktrace));
%>		
</span></td>
  </tr>
</table>
<p align="center">&nbsp;</p>
<p class="blueMain">&nbsp;</p>
</body>
</html>
