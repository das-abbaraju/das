<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%@ include file="utilities/contractor_pdfs_secure.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.accountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>

<%	String id = request.getParameter("id");
	cBean.setFromDB(id);
	aBean.setFromDB(id);
%>

<html>
  <head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
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
          <td valign="top" align="center"><img src="images/header_prequalification.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="center">
  <table border="0" cellspacing="0" cellpadding="1">
    <tr align="center" class="blueMain">
                <td class="blueMain">Services Provided by:<br>
				<span class="blueHeader"><%=aBean.name%></span></td>
    </tr>
    <tr align="center" class="blueMain">
                <td>&nbsp;</td>

  <tr>
    <td class="blueMain"><strong>Company
        Employees</strong>:<br>
        <span class="redMain"><%=cBean.getTradesList()%></span><br><br></td>
    </tr>
  <tr>
    <td class="blueMain"><strong>Subcontractors</strong>:<br><span class="redMain">
      <%=cBean.getSubTradesList()%></span></td>
    </tr>
  </table>

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
</body>
</html>
