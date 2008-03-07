<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.access.*"%>
<%@ include file="includes/main.jsp" %>
<%
permissions.tryPermission(OpPerms.EditUsers, OpType.View);

String opID = permissions.getAccountIdString();
if (permissions.isAdmin() && request.getParameter("id") != null) {
	opID = request.getParameter("id");
}

String msg = request.getParameter("msg");
if (null == msg) msg = "";
String newUserID = request.getParameter("newid");
if (newUserID != null) {
	//Add the most recently added user to a hidden div for testing purposes
	msg = msg + "<div style='display: none' id='newUser'>"+newUserID+"</div>";
}

AccountBean aBean = new AccountBean();
aBean.setFromDB(opID);

SelectUser sql = new SelectUser();
sql.addField("lastLogin");
sql.addWhere("isGroup = 'No' ");
sql.addWhere("accountID = "+opID);

sql.addOrderBy("u.name");

Report search = new Report();
search.setSql(sql);
search.setPageByResult(request);

List<BasicDynaBean> userList = search.getPage();

%>
<%@page import="com.picsauditing.search.SelectUser"%>
<%@page import="com.picsauditing.search.Report"%>
<html>
  <head>
  <title>PICS - Manage Users</title>
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
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
            <table width="657" cellpadding="2" cellspacing="0">
              <tr>
                <td align="center" valign="top" class="blueMain">
                  <%@ include file="includes/nav/opSecondNav.jsp"%>
                  <table width="0" border="0" cellspacing="1" cellpadding="1">
                    <tr align="center" class="blueMain"> 
                      <td colspan="2" class="blueHeader">Edit Additional Users for <br> <%=aBean.name%></td>
                    </tr>
                    <tr align="center" class="blueMain"> 
                      <td colspan="2" class="redMain"><strong><%=msg%></strong></td>
                    </tr>
                    <tr class="blueMain"> 
                      <td colspan="2" align="center">
                      <a href="accounts_editUser.jsp?id=<%=opID%>&action=New">Add New User</a> |
                      <a href="users_manage.jsp">Use the new tool (beta)</a>
                      </td>
                    </tr>
					<tr>
					  <td colspan="2">
					    <table align="center" cellspacing="1" cellpadding="1">
                          <tr bgcolor="#003366" class="whiteTitle">
							<td colspan=2 align=center>Name</td>
							<td align=center>Last Login</td>
							<td align=center>Active</td>
						  </tr>
					<%
					com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
					for(BasicDynaBean row: userList) {
						%>
						  <tr <%=color.nextBgColor()%> class="blueMain">
							<td align="right"><%=color.getCounter()%>.</td>
                            <td><a href="accounts_editUser.jsp?action=Edit&id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
                            <td align="center"><%=row.get("lastLogin")%></td>
                            <td align="center"><%=row.get("isActive")%></td>
					      </tr>
						<%
					}
					%>
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
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
