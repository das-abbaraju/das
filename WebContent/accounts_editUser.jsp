<%//@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.*"%>
<%@ include file="utilities/op_edit_secure.jsp" %>

<%	String editID = request.getParameter("uID");
	String action = request.getParameter("action");
	String opID = request.getParameter("id");
	User user = new User();
	UserAccess userAccess = new UserAccess();
	UserAccess opAccess = new UserAccess();
	opAccess.db = "opAccess";
	opAccess.setFromDB(opID);
	boolean isSubmitted = !(null == request.getParameter("submit"));
	boolean isNew = "New".equals(action);
	boolean isEdit = "Edit".equals(action);
	if (isEdit){
		user.setFromDB(editID);
		userAccess.setFromDB(editID);
	}//if
	if (isSubmitted){
		user.setFromRequest(request);
		userAccess.setFromRequest(request);
		if (isNew){
			if (user.isOK()){
				user.writeNewToDB(opID, request);
				userAccess.writeNewToDB(opID,user.userDO.id);
				response.sendRedirect("accounts_userList.jsp?id="+opID+"&msg="+
						user.userDO.name+"'s account successfully created.<br>Login "+
						"info and user manual have been sent to "+user.userDO.email);
				return;
			}//if
		}else if (isEdit){
			if (user.isOK()){
				user.writeToDB();
				userAccess.writeToDB(opID);
				response.sendRedirect("accounts_userList.jsp?id="+opID);
				return;
			}//if
		}//else if
	}//if
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
            <td colspan="3">
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="126" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"></td>
                  <td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
                    <form name="form1" method="post" action="accounts_editUser.jsp">
                    <input type=hidden name="action" value="<%=action%>">
                    <input type=hidden name="uID" value="<%=editID%>">
                    <input type=hidden name="id" value="<%=opID%>">
                    <table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain">
                        <td colspan="2" class="blueHeader">Edit User</td>
                      </tr>
                      <tr>
                        <td colspan="2" class="redMain">
                          <strong><%=user.getErrorMessages()%></strong>
                        </td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Name:</td>
                        <td> <input name="name" type="text" class="forms" size="30" value="<%=user.userDO.name%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Email:</td>
                        <td><input name="email" type="text" class="forms" size="30" value="<%=user.userDO.email%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Username:</td>
                        <td><input name="username" type="text" class="forms" size="30" value="<%=user.userDO.username%>"></td>
                      </tr>
<%	if (isEdit){ %>
                      <tr>
                        <td class="blueMain" align="right">Password:</td>
                        <td class=blueMain>******</td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">New Password:</td>
                        <td><input name="newPassword" type="text" class="forms" size="30"></td>
                      </tr>
<%	}else{%>
                      <tr>
                        <td class="blueMain" align="right">Password</td>
                        <td><input name="newPassword" type="text" class="forms" size="15"></td>
                      </tr>
<%	}//else%>
                      <tr>
                        <td class="blueMain" align="right">Active</td>
                        <td class="blueMain" align="left"><%=Utilities.getYesNoRadio("isActive",user.userDO.isActive)%></td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="Save"></td>
                      </tr>
                      <tr>
						<td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
						<td colspan=2 align=center>
						  <table bgcolor="#EEEEEE" cellspacing="1" cellpadding="1">
						    <tr class="whiteTitle">
                              <td align="center" bgcolor="#336699" colspan=2>Permission</td>
                              <td align="center" bgcolor="#993300">Grant</td>
						    </tr>
<%	int count = 1;
	for(OpPerms perm: OpPerms.values()){
		if (opAccess.hasAccess(perm)){%>
                            <tr <%=Utilities.getBGColor(count)%>>
                              <td class="blueMain" align="right"><%=count++%></td>
                              <td class="blueMain" align="left"><%=perm.getDescription()%></td>
                              <td align="center">
                                <input name="perm_<%=perm%>" type="checkbox" class="forms" value="checked" <%=userAccess.getChecked(perm)%>>
                              </td>
                            </tr>
<%		}//if
	}//for %>
                          </table>
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="Save"></td>
                      </tr>
                    </table>
                    </form>
                  </td>
                  <td width="126" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"></td>
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
</body>
</html>
