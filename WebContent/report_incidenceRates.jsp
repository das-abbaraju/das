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
</head>
<body>
<h1>Incidence Rates Report</h1>
<form name="form1" method="post" action="report_incidenceRates.jsp">
<%	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
			<table border="0" cellpadding="2" cellspacing="0">
			  <tr class="blueMain">
				<td align="right">Incidence Rate Cutoff:</td>
				<td><input name="searchIncidenceRate" type="text" size="5" value=<%=sBean.searchIncidenceRate%>></td>
				<td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"></td>
			  </tr>
		    </table>
            <strong>Check next to the years to search</strong>
            <br><br><%=sBean.getLinks()%>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle">
                <td colspan="2">Contractor</td>
                <td>Location</td>
                <td>Type</td>
                <td><%=thisYear-1%><input name="searchYear1" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear1)%> checked></td>
                <td><%=thisYear-2%><input name="searchYear2" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear2)%> checked></td>
                <td><%=thisYear-3%><input name="searchYear3" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear3)%> checked></td>
  			  </tr>
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
		    <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
      <center><%@ include file="utilities/contractor_key.jsp"%></center>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>