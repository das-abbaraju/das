<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<jsp:useBean id="pBean" class="com.picsauditing.access.PermissionsBean" scope ="session"/>
<%
	String con_id = request.getParameter("id");
	boolean isToAuditor = "true".equals(request.getParameter("isaud"));
	com.picsauditing.PICS.EmailBean.sendConfirmationEmail(con_id, isToAuditor);
%>
<html>
<head>
	<title>PICS - Pacific Industrial Contractor Screening</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<META Http-Equiv="Cache-Control" Content="no-cache">
	<META Http-Equiv="Pragma" Content="no-cache">
	<META Http-Equiv="Expires" Content="0">
	<link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center">&nbsp;</td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
		</table>
		 <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		<td colspan="3">
          <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
            <tr> 
              <td height="70" colspan="2" align="center" class="blueHeader"> 
				Thank you for your confirmation.</span>
           </td>
            </tr>
 	     </table>   
	   </td>
  </tr>
    </table>   
	   </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
