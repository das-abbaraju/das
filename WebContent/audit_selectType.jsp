<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<%	String auditType = request.getParameter("auditType");
	String from = request.getParameter("from");
	if (null == from)
		from = "";
	if (null != auditType) {
		session.setAttribute("auditType", auditType);
		if (null != from && !"".equals(from)) {
			response.sendRedirect(from);
			return;
		}//if
	}//if
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

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.form1.auditType.focus();">
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
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3" align="center">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">                  
                <td class="blueMain"><%@ include file="includes/nav/editPQFNav.jsp"%></td>
              </tr>
              <tr>             
  		        <td align="center" class="redMain"> 
  		          <br>Please select an Audit Type<br>
				  <form name="form1" method="post" action="audit_selectType.jsp">
                    <%=Inputs.inputSelectFirstSubmit("auditType","forms",auditType,Constants.AUDIT_TYPE_ARRAY,Constants.DEFAULT_AUDIT)%>
                  <input type=hidden name=from value="<%=from%>">
				  </form>
                </td>
              </tr>
            </table>
          <br>
          </td>
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
