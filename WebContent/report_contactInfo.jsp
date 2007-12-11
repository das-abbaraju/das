<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="session"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope="page"/>
<%	try{
	tBean.setFromDB();
	sBean.orderBy = "name";
	sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	sBean.writeToExcelFile(config, pBean);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
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
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
		<td colspan="3" align="center">
          <table width="657" border="0" cellpadding="0" cellspacing="0">
            <tr> 
              <td height="70" colspan="2" align="center"><%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">Contractor Contact Information Report</span><br>
                    <%=sBean.getExcelLink(pBean.userID)%><br>
                  <form name="form1" method="post" action="report_contactInfo.jsp">
<%	if (isAdmin)
		out.println(SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID));
	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center">
                      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
                      <td><%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%></td>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
                    </tr>
                    <tr align="left">
                      <td colspan=3><%=SearchBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%>
<!--                        <%=sBean.getStatusSelect("status","blueMain", sBean.selected_status)%>
                        <%=sBean.getAuditStatusSelect("auditStatus", "blueMain", sBean.selected_auditStatus)%>
-->					  </td>
                    </tr>
                  </table>
                  </form>
	            </td>
            </tr>
            <tr> 
              <td height="30">
<%	if (pBean.isOperator()) { %>
                  <%=sBean.getStartsWithLinks()%>
<%	} //if %>
              </td>
              <td align="center"><%=sBean.getLinks()%></td>
            </tr>
          </table>
          <table width="657" border="0" cellpadding="1" cellspacing="1">
            <tr bgcolor="#003366" class="whiteTitle"> 
              <td colspan="2">Contractor</td>
              <td>Address</td>
              <td align="center" bgcolor="#6699CC">Contact</td>
              <td align="center" bgcolor="#336699">Phone</td>
  			</tr>
<%	while (sBean.isNextRecord()) {
		String thisClass = ContractorBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
			  <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
				<td align="right"><%=sBean.count-1%></td>
				<td>
				  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a></td>
				<td><%=sBean.aBean.address%><br><%=sBean.aBean.city%>, <%=sBean.aBean.state%> <%=sBean.aBean.zip%></td>
				<td align="center"><%=sBean.aBean.contact%></td>
            	<td align="center"><%=sBean.aBean.phone%><br><%=sBean.aBean.phone2%></td>
		  	  </tr>
<%	}//while %>
		  </table><br>
		  <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
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
		sBean.closeSearch();
	}//finally
%>