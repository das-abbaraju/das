<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>

<%
try {
	sBean.orderBy = "Name";
	sBean.setIsFatalitiesReport();
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	if (!pBean.isAdmin())
		sBean.setCanSeeSet(pBean.canSeeSet);
%>
<html>
<head>
<title>Fatalities</title>
</head>
<body>
          <table width="657" border="0" cellpadding="0" cellspacing="0">
            <tr> 
              <td height="70" colspan="2" align="center"><%@ include file="includes/selectReport.jsp"%>
	            <span class="blueHeader">Fatalities Report</span>
	            </td>
            </tr>
          </table>
<%	if (pBean.isCorporate()){%>
		  <form name="form1" method="post" action="report_fatalities.jsp">
          <table border="0" cellpadding="2" cellspacing="0">
            <tr align="center" >
              <td><%=pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID)%></td>
              <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
            </tr>
          </table>
		  </form>
<%	}//if
	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
%>
		  <%=sBean.getLinks()%>
          <table width="657" border="0" cellpadding="1" cellspacing="1">
            <tr bgcolor="#003366" class="whiteTitle"> 
              <td colspan="2">Contractor</td>
              <td><%=thisYear-1%></td>
              <td><%=thisYear-2%></td>
              <td><%=thisYear-3%></td>
  			</tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
			  <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
				<td>
				  <a href="pqf_viewOSHA.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>" target="_blank"><%=sBean.aBean.name%></a></td>
				<td><%=sBean.osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR1)%></td>
				<td><%=sBean.osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR2)%></td>
				<td><%=sBean.osBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR3)%></td>
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