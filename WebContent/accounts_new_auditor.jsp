<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" %>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>
<%	if (request.getParameter("submit") != null) {
		aBean.setFromRequestNewAuditor(request);
		if (aBean.isOK() && aBean.writeNewToDB()) {
			AUDITORS.resetAuditorsAL();
			response.sendRedirect("accounts_manage.jsp?type=Auditor");
			return;
		} // if
	} // if
%>

<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <form name="form1" method="post" action="accounts_new_auditor.jsp">
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
          <td valign="top" align="center"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3"><br>
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="125" align="center" bgcolor="#DDDDDD" class="blueMain"><br></td>
                  <td align="center" bgcolor="#FFFFFF" class="blueMain"><table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr class="blueMain"> 
                        <td colspan="2" align="center" class="blueHeader">New 
                          Auditor</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain">
<%	if (request.getParameter("submit") != null)
		out.println(aBean.getErrorMessages());
%>
                        </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Name</td>
                        <td> <input name="name" type="text" class="forms" size="20" value="<%=aBean.name%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Email</td>
                        <td><input name="email" type="text" class="forms" size="30" value="<%=aBean.email%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Username</td>
                        <td><input name="username" type="text" class="forms" size="15" value="<%=aBean.username%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Password</td>
                        <td><input name="password" type="text" class="forms" size="15" value="<%=aBean.password%>"></td>
                      </tr>
					  <tr> 
                        <td class="blueMain" align="right">Visible?</td>
                        <td class="blueMain" align="left"> <input name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>
                          Yes 
                          <input name="active" type="radio" value="N" <%=aBean.getNotActiveChecked()%>>
                          No </td>
                      </tr>
                      <tr> 
                        <td></td>
                        <td class="blueMain">&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain">&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
                    <br>
                  </td>
                  <td width="126" bgcolor="#DDDDDD" class="blueMain"> </td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
	  </form>
      <br>
      <br>
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