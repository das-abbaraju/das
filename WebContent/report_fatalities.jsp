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
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Fatalities Report</h1>
<%	if (pBean.isCorporate()){%>
		  <form name="form1" method="post" action="report_fatalities.jsp">
          <table border="0" cellpadding="2" cellspacing="0">
            <tr align="center" >
              <td><%=pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID)%></td>
              <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
            </tr>
          </table>
          <input type="hidden" name="actionID" value="0">
		  <input type="hidden" name="action" value="">
   		  <input type="hidden" name="showPage" value="1"/>
		<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "dateCreated DESC" : sBean.orderBy %>"/>
		  </form>
<%	}//if
	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
%>
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>
          <table class="report">
            <thead><tr> 
              <td colspan="2">Contractor</td>
              <td><%=thisYear-1%></td>
              <td><%=thisYear-2%></td>
              <td><%=thisYear-3%></td>
  			</tr></thead>
<%	while (sBean.isNextRecord()){
		String thisClass = ""; // TODO Add in the Contractor FlagColor
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
		  <center><%=sBean.getLinksWithDynamicForm()%>
</center>
<%	sBean.closeSearch(); %>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>