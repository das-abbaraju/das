<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.accountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.contractorBean" scope ="page"/>
<%	String id = request.getParameter("id");
	aBean.setFromDB(id);
	cBean.setFromDB(id);
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
		               <tr align="center" class="blueMain"><td colspan="5" align="center">
     <%             			if (isAdmin) {%>						
	 <%@ include file="utilities/adminContractorNav.jsp"%>
<%					}//if 
				if (isAuditor && auditorCanSeeSet.contains(req_uid)) {%>
				<%@ include file="utilities/auditorContractorNav.jsp"%>					
			<% } //if%>
</td></tr>
<tr>
          <td colspan="5">&nbsp;</td>
        <tr>
          <td>&nbsp;</td>
		  <td colspan="3" align="center" class="blueMain">Pre-qualification information for:<br>
		    <span class="blueHeader"><%=aBean.name%></span><br>
            Date submitted: <span class="redMain"><%=cBean.prequalDate%></span><br><br>
          <table border="1" cellpadding="1" cellspacing="0" bordercolor="#FFFFFF">
            <tr class="blueMain">
              <td class="blueMain"><strong>Form</strong></td>
              <td class="blueMain"><strong>Description</strong></td>
            </tr>
            <%// don't show form A to auditors BJ 10-28-04
     		if (!(utype.equals("Auditor"))) {
			%>
			<tr> 
              <td class="blueMain"><a href="/servlet/viewPDF?id=<%=id%>&form=prequalA" target="_blank">Form A</a></td>
              <td class="redMain">PDF Form - General Info, Officers, Organization,
                Work History, EMR Data </td>
            </tr>
            <%} //if 
			%>
			<tr>
              <td class="blueMain"><a href="/servlet/viewPDF?id=<%=id%>&form=prequalS" target="_blank">Form
                S</a></td>
              <td class="redMain">PDF Form - Health, Safety, Environment, Training</td>
            </tr>
            <tr>
              <td class="blueMain"><a href="/pqf_viewOSHA.jsp?id=<%=id%>&catID=29" target="_blank">Form
                O</a></td>
              <td class="redMain">OSHA Injury and Illness Data </td>
            </tr>
            <tr>
              <td class="blueMain"><a href="/prequal_viewTrades.jsp?id=<%=id%>" target="_blank">Services </a></td>
              <td class="redMain">The services that the company provides </td>
            </tr>
          </table>
            <br>
          </td>
            <td>&nbsp;</td>
        </tr>
      </table>
        <br>
        <br>
        <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<map name="Map">
  <area shape="rect" coords="73,4,142,70" href="logout.jsp">
</map>
</body>
</html>
