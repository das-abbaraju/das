<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.accountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.tradesBean" scope ="page"/>

<%	String edit_id = request.getParameter("id");
	cBean.setFromDB(edit_id);
	if (!"admin".equals(utype) && !cBean.canEditPrequal()) {
		response.sendRedirect("/login.jsp");
		return;
	}//if

	if (request.getParameter("submit") != null) {
		cBean.setTrades(request.getParameterValues("trades"));
		cBean.setSubTrades(request.getParameterValues("subTrades"));
		cBean.writeToDB();
		response.sendRedirect("prequal_edit.jsp?id=" + edit_id);
		return;
	}//if
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
<form name="form1" method="post" action="prequal_trades.jsp?id=<%=edit_id%>">
  <table border="0" cellspacing="0" cellpadding="1">
    <tr align="center" class="blueMain">
      <td colspan="2" class="blueHeader">Services You Provide </td>
    </tr>
    <tr align="center">
      <td colspan="2" class="redMain"><%	if (request.getParameter("submit") != null)
	out.println(cBean.getErrorMessages());
%>
      </td>
    </tr>
    <tr align="center" class="blueMain">
      <td colspan="2">Please select the categories in which you are qualified
        to perform work:</td>
    </tr>
  <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td class="blueMain" align="right" valign="top">Work performed by<br>
                                <strong>company employees</strong>:</td>
    <td valign="top" class="redMain"><%=tBean.getTradesMultipleSelect("trades", "blueMain", cBean.trades)%><br>
      Hold down 'CTRL' key to select multiple<br>
      <br>
    </td>
  </tr>
  <tr>
    <td class="blueMain" align="right" valign="top">Work performed by<br>
                                <strong>subcontractors</strong>:</td>
    <td valign="top" class="redMain"><%=tBean.getTradesMultipleSelect("subTrades", "blueMain", cBean.subTrades)%><br>
      Hold down 'CTRL' key to select multiple<br>
      <br>
    </td>
  </tr>
  <tr>
    <td class="blueMain" align="right">&nbsp;</td>
    <td><input name="submit" type="submit" class="forms" value="submit"></td>
  </tr>
  </table>

			</form>
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
