<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%	String id = request.getParameter("id");
	aBean.setFromDB(id);
	if (request.getParameter("newPassword") != null) {
		String newPassword = request.getParameter("newPassword");
		if (aBean.newPasswordOK(newPassword)) {
			String ses_id = (String)session.getAttribute("temp_userid");
			if ((null == ses_id) || !ses_id.equals(id)) {
				response.sendRedirect("logout.jsp");
				return;
			}//if
			session.removeAttribute("temp_userid");
			session.setAttribute("userid",ses_id);

			aBean.changePassword(newPassword);
			if ("Contractor".equals(aBean.type)) {
				response.sendRedirect("contractor_detail.jsp?id=" + id);
				return;
			}//if
			if ("Operator".equals(aBean.type)) {
				response.sendRedirect("contractor_list.jsp");
				return;
			}//if
			if ("Auditor".equals(aBean.type)) {
				response.sendRedirect("contractor_list_auditor.jsp");
				return;
			}//if

		}//if
	} else {
		String ses_id = (String)session.getAttribute("userid");
		if ((null == ses_id) || !ses_id.equals(id)) {
			response.sendRedirect("logout.jsp");
			return;
		}//if
		session.removeAttribute("userid");
		session.setAttribute("temp_userid",ses_id);
	}//else

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
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" rowspan="2" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_editAccount.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3">
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="125" bgcolor="#DDDDDD" class="blueMain"> <br>
                  </td>
                  <td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
					<form name="form1" method="post" action="change_password.jsp?id=<%=id%>">
				    <table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain"> 
                        <td colspan="2" class="blueHeader">Change Password</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain">
<%
		if ("".equals(aBean.getErrorMessages()))
			out.println("For security reasons, we require that you periodically change your password.  Please select a new one.");
		else
			out.println(aBean.getErrorMessages());
%> 					    </td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Username:</td>
                        <td class="blueMain"><b> <%=aBean.username%></b></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Old Password:</td>
                        <td class="blueMain"><%=aBean.password%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">New Password</td>
                        <td><input name="newPassword" type="text" class="forms" size="15"></td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
					</form>
                    <br>
                  </td>
                  <td width="126" bgcolor="#DDDDDD" class="blueMain"> </td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
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
