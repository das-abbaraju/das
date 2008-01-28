<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="uBean" class="com.picsauditing.domain.UsersDO" scope ="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>

<%	
	String edit_id = request.getParameter("id");

	uBean.setId(Integer.parseInt(edit_id));
	if (request.getParameter("submit") == null)
		uBean.setFromDB();
	else {
		uBean.setFromRequest(request);
		if (uBean.isOK()){
			uBean.writeToDB();
			AUDITORS.resetAuditorsAL();
			response.sendRedirect("accounts_manage.jsp?type=Auditor");
			return;
		}//if
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
<form name="form1" method="post" action="accounts_edit_auditor.jsp?id=<%=edit_id%>">

<input name="type" type="hidden" value="Auditor">
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
          <td valign="top"><div align="center" class="forms"> <a href="accounts_manage.jsp"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></a> 
              </div></td>
            <td valign="top"><script language="JavaScript">
  var j,d="",l="",m="",p="",q="",z="",list= new Array()
  list[list.length]='images/squareLogin_1.gif';
  list[list.length]='images/squareLogin_2.gif';
  list[list.length]='images/squareLogin_3.gif';
  list[list.length]='images/squareLogin_4.gif';
  list[list.length]='images/squareLogin_5.gif';
  j=parseInt(Math.random()*list.length);
  j=(isNaN(j))?0:j;
  document.write("<img useMap='#Map' border='0' hspace='1' src='"+list[j]+"'>");</script>
      <map name="Map">
        <area shape="rect" coords="73,4,142,70" href="logout.jsp">
      </map></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3"><br>
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="125" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"> 
                    <br>
                  </td>
                  <td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
<table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain"> 
                        <td colspan="2" class="blueHeader">Edit Auditor</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain"> <%	if (request.getParameter("submit") != null)
							out.println(uBean.getErrorMessages());
						%> </td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Name</td>
                        <td> <input name="name" type="text" class="forms" size="20" value="<%=uBean.getName()%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Email</td>
                        <td><input name="email" type="text" class="forms" size="30" value="<%=uBean.getEmail()%>"></td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Username</td>
                        <td><input name="username" type="text" class="forms" size="15" value="<%=uBean.getUsername()%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Password</td>
                        <td><input name="password" type="text" class="forms" size="15" value="<%=uBean.getPassword()%>"></td>
                      </tr>
					   <tr> 
                        <td class="blueMain" align="right">Active?</td>
                        <td class="blueMain" align="left">
							<%=com.picsauditing.PICS.Inputs.getYesNoRadio("Active","forms", uBean.getIsActive())%>
                      </tr>
                      <tr> 
						<td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
                  </td>
                  <td width="126" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"></td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table></form>
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
