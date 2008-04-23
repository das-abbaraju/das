<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>

<%	try{
	String action = request.getParameter("action");
	sBean.setExpiresInDays("365");
	sBean.orderBy = "auditDate";
	sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
%>
<html>
<head>
<title>Expired Audits</title>
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body>
          <table border="0" cellpadding="0" cellspacing="0">
            <tr> 
              <td height="70" colspan="2" align="center" class="buttons"> 
                <%@ include file="includes/selectReport.jsp"%>
	            <span class="blueHeader">Expired Audits Report </span><br>
				Shows contractors whose audits have expired<br>
				 or whose audits will expire 
				in the next 12 months.
				<form name="form1" method="post" action="report_expiredAudits.jsp">
<%	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
<table border="0" cellpadding="2" cellspacing="0">
  <tr align="center" >
    <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
    <td><%=sBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%></td>
    <td><%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%></td>
    <td><input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)"></td>
    <td><strong>
      <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
    </strong></td>
  </tr>
</table>
                  </form>
              </td>
            </tr>

            <tr> 
              <td height="30"><%=sBean.getStartsWithLinks()%>
			  </td>
              <td align="right"><%=sBean.getLinks()%></td>
            </tr>
          </table>
          <table width="657" border="0" cellpadding="1" cellspacing="1">
            <tr bgcolor="#003366" class="whiteTitle"> 
                  <td colspan="2">Contractor</td>
                  <td>City</td>
                  <td align="center" bgcolor="#6699CC">Audit</td>
  			</tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
			  <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
				<td>
				<a href="ContractorView.action?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a></td>
				<td><%=sBean.aBean.city%>, <%=sBean.aBean.state%></td>
				<td align="center"><%=sBean.cBean.auditDate%></td>
		  	  </tr>
<%	}//while %>
		  </table><br>
		  <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>