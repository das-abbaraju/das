<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page isErrorPage="true" %>
<html>
<head>
<title>Untitled Document</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
<%
	String message = "";
	String cause = "Undetermined";
	
	if(exception != null){
		if (exception.getMessage() != null)
			message = exception.getMessage();
		else
			message = exception.toString();
		if (exception.getCause() != null)
			cause = exception.getCause().getMessage();
	}//if
%>
</head>

<body bgcolor="EEEEEE">
<p>&nbsp;</p>
<table width="500" border="0" align="center" cellpadding="15" cellspacing="0" bordercolor="#CCCCCC" bgcolor="#FFFFFF">
  <tr> 
    <td><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a>
      </td>
    <td class="blueMain">There was an exception thrown:<br> 
        <b><%=message %> </b><br>
       <b>Caused by: <%=cause %><br><br>
       <span class="redMain">You may continue working by clicking on your browser's <b>Back</b> button. We apologize for this inconvenience.</span>
       <br><br>Please report this problem to our <a href="mailto:gsilverman@picsauditing.com">administrator</a>.
       </td>
  </tr>
</table>
<br><br><br>
</body>
</html>
