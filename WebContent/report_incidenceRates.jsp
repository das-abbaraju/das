<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%	try{
	boolean showAll = false;
	String id = (String)session.getAttribute("userid");

	sBean.orderBy = "name";
	sBean.setIsOSHASearch();
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	if (!pBean.isAdmin())
		sBean.setCanSeeSet(pBean.canSeeSet);
	//int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear();
	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
%>
<html>
<head>
<title>Incidence Rates</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Incidence Rates Report</h1>
<form id="form1" name="form1" method="post" action="report_incidenceRates.jsp">
<%	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
			<table border="0" cellpadding="2" cellspacing="0">
			  <tr class="blueMain">
				<td align="right">Incidence Rate Cutoff:</td>
				<td><input name="searchIncidenceRate" type="text" size="5" value=<%=sBean.searchIncidenceRate%>></td>
				<td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"></td>
			  </tr>
		    <input type="hidden" name="actionID" value="0">
		  	<input type="hidden" name="action" value="">
   		  	<input type="hidden" name="showPage" value="1">
		    </table>
		    <strong>Check next to the years to search</strong>
            <br><br><div>
			<%=sBean.getLinksWithDynamicForm()%>
			</div>
            <table class="report">
              <thead><tr>
                <td colspan="2">Contractor</td>
                <td>Location</td>
                <td>Type</td>
                <td><%=thisYear-1%><input name="searchYear1" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear1)%> checked></td>
                <td><%=thisYear-2%><input name="searchYear2" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear2)%> checked></td>
                <td><%=thisYear-3%><input name="searchYear3" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear3)%> checked></td>
  			  </tr></thead>
<%	while (sBean.isNextRecord()){
		String thisClass = ""; // TODO Add in the Contractor FlagColor
%>
			  <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
			    <td>
				  <a href="pqf_viewOSHA.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>" target="_blank"><%=sBean.aBean.name%></a></td>
			    <td><%=sBean.osBean.getLocationDescription()%></td>
			    <td><%=sBean.osBean.SHAType%></td>
			    <td><%=sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR1)%></td>
			    <td><%=sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR2)%></td>
			    <td><%=sBean.osBean.calcRate(OSHABean.RECORDABLE_TOTAL,OSHABean.YEAR3)%></td>
		  	  </tr>
<%	}//while %>
		    </table><br>
		    </form>
		    <center>
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div></center>
<%	sBean.closeSearch(); %>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>