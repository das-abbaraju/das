<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.UserBean" scope ="page"/>
<%
try{
	String accountID = request.getParameter("id");
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String errorMsg = "";
	if ("Update".equals(action)) {
		uBean.setFromDB(actionID);
		uBean.setFromRequest(request,actionID);
		if (uBean.isOK()){
			uBean.writeToDB(actionID);
			response.sendRedirect("accounts_editUsers.jsp?id="+accountID);
			return;
		}//if
	} //if
	if ("Add".equals(action)) {
		uBean.setFromRequest(request,"new");
		if (aBean.usernameExists(uBean.username))
			errorMsg = "The username <b>"+uBean.username+"</b> already exists.  Please choose another username.";
		else if (uBean.isOK()){
			uBean.addUser(accountID);
			response.sendRedirect("accounts_editUsers.jsp?id="+accountID);
			return;
		}//if
	} //if
	if ("Delete".equals(action))
		uBean.deleteUser(actionID);

	aBean.setFromDB(accountID);
	uBean.setList(aBean.id);
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
                <td align="center" valign="top" class="blueMain">
                  <table width="0" border="0" cellspacing="0" cellpadding="1">
                    <tr align="center" class="blueMain"> 
                      <td colspan="2" class="blueHeader">Edit Additional Users for <br> <%=aBean.name%></td>
                    </tr>
                    <tr> 
                      <td colspan="2" class="redMain"><%=errorMsg+uBean.getErrorMessages()%></td>
                    </tr>
                    <tr class="blueMain"> 
                      <td colspan="2">&nbsp; </td>
                    </tr>
					<tr>
					  <td colspan="2">
					    <table align="center" cellspacing="1">
						  <tr class="whiteTitle" bgColor="336699"">
							<td>Name</td>
							<td>Username</td>
							<td>Password</td>
							<td>Email</td>
							<td>See OSHA</td>
							<td>See Full PQF</td>
							<td>Edit Flag Criteria</td>
							<td>Edit Forced Flags</td>
							<td>Edit Notes</td>
							<td>Last Login</td>
							<td colspan=2>&nbsp;</td>
						  </tr>
<%	while (uBean.isNextRecord()) {%>
                          <form name="form_<%=uBean.id%>" id="form_<%=uBean.id%>" method="post" action="">
						  <tr class="blueMain" <%=Utilities.getBGColor(uBean.count)%>>
                            <td><input name="name_<%=uBean.id%>" id="name_<%=uBean.id%>" type="text" class="forms" size="12" value="<%=uBean.name%>"></td>
                            <td><input name="username_<%=uBean.id%>" id="username_<%=uBean.id%>" type="text" class="forms" size="12" value="<%=uBean.username%>"></td>
                            <td><input name="password_<%=uBean.id%>" id="password_<%=uBean.id%>" type="text" class="forms" size="8" value="<%=uBean.password%>"></td>
                            <td><input name="email_<%=uBean.id%>" id="email_<%=uBean.id%>" type="text" class="forms" size="12" value="<%=uBean.email%>"></td>
                            <td align="center"><input name="seeOsha_<%=uBean.id%>" id="seeOsha_<%=uBean.id%>" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.seeOsha)%>></td>
                            <td align="center"><input name="seeFullPQF_<%=uBean.id%>" id="seeFullPQF_<%=uBean.id%>" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.seeFullPQF)%>></td>
                            <td align="center"><input name="editFlagCriteria_<%=uBean.id%>" id="editFlagCriteria_<%=uBean.id%>" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editFlagCriteria)%>></td>
                            <td align="center"><input name="editForcedFlags_<%=uBean.id%>" id="editForcedFlags_<%=uBean.id%>" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editForcedFlags)%>></td>
                            <td align="center"><input name="editNotes_<%=uBean.id%>" id="editNotes_<%=uBean.id%>" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editNotes)%>></td>
                            <td align="center"	><%=uBean.lastLogin%></td>
                            <td><input name="action" type="submit" class="buttons" value="Update"></td>
                            <td><input name="action" type="submit" class="buttons" value="Delete" onClick="return confirm('Are you sure you want to delete <%=uBean.name%>?');"></td>
					      </tr>
					      <input name="actionID" type="hidden" value="<%=uBean.id%>">
						  </form>
<%	}//for
	if ("Add".equals(action))
		uBean.setFromRequest(request,"new");
	else
		uBean.resetFields();
%>
						  <tr><td colspan="5" align="center" class="blueMain">------------------------------------------------------------------------</td></tr>
						  <form name="form_new" id="form_new" method="post" action="">
						  <tr>
						    <td><input name="name_new" id="name_new" type="text" class="forms" size="12" value="<%=uBean.name%>"></td>
						    <td><input name="username_new" id="username_new" type="text" class="forms" size="12" value="<%=uBean.username%>"></td>
						    <td><input name="password_new" id="password_new" type="text" class="forms" size="8" value="<%=uBean.password%>"></td>
						    <td><input name="email_new" id="email_new" type="text" class="forms" size="12" value="<%=uBean.email%>"></td>
						    <td align="center"><input name="seeOsha_new" id="seeOsha_new" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.seeOsha)%>></td>
						    <td align="center"><input name="seeFullPQF_new" id="seeFullPQF_new" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.seeFullPQF)%>></td>
						    <td align="center"><input name="editFlagCriteria_new" id="editFlagCriteria_new" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editFlagCriteria)%>></td>
						    <td align="center"><input name="editForcedFlags_new" id="editForcedFlags_new" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editForcedFlags)%>></td>
						    <td align="center"><input name="editNotes_new" id="editNotes_new" type="checkbox" class="forms" value="Yes" <%=com.picsauditing.PICS.Utilities.checkedBox(uBean.editNotes)%>></td>
						    <td colspan=3><input name="action" type="submit" class="buttons" value="Add"></td>
					      </tr>
					      </form>
					      <tr>
					        <td colspan="4" align="center" class="blueMain">
							  <a href="accounts_edit_operator.jsp?id=<%=accountID%>">Return to Edit Operator</a>
							</td>
						  </tr>
					    </table>
					  </td>
					</tr>
                    <tr> 
				      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                    </tr>
                      <tr> 
                      <td>&nbsp;</td>
                      <td></td>
                    </tr>
                  </table>
                </td>
              </tr>
            </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>   
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
<%	}finally{
		uBean.closeList();
	}//finally
%>