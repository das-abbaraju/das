<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
try{
	OperatorBean oBean = new OperatorBean();
	java.util.ArrayList<String> opAL = oBean.getOperatorsAL();
	String marker = " ";
	int ctr = 0;
	sBean.orderBy = "Name";
	if (permissions.isAdmin())
		sBean.doSearch(request, SearchBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
	else
		sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);

%>
<%@page import="com.picsauditing.PICS.redFlagReport.FlagDO"%>
<%@page import="java.util.Map"%>
<html>
<head>
<title>Corporate Contractors Report</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Contractors by Facility</h1>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>

<form id="form1" name="form1" method="post" style="display: none" action="report_operatorContractor.jsp">
		<table border="0" cellpadding="2" cellspacing="0">
			<tr align="center">
				<td>
                  <input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
<%		if (permissions.isAdmin())
			out.println(SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID));
		if (permissions.isCorporate())
			out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
				</td>
				<td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" >
			</tr>
		</table>
        <%=sBean.getStartsWithLinks()%><br>
		<% if (permissions.isOperator()) { %>
			<input type="hidden" name="searchCorporate" value="<%= request.getParameter("searchCorporate")%>"/>
		<% } %>
		<input type="hidden" name="showPage" value="1"/>
		<input type="hidden" name="startsWith" value=""/>
		<input type="hidden" name="orderBy"  value="name"/>
		<input type="hidden" name="changed"  value="1"/>
</form>
</div>

<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>
<table class="report">
<thead>
<tr>
	<td colspan="2">Contractor</td>
	<%
	int count = 0;
	for (java.util.ListIterator<String> li = opAL.listIterator();li.hasNext();){
		String opID = (String)li.next();
		String opName = (String)li.next();
		if (permissions.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
			%>
			<td style="font-size: 10px;"><%= opName %></td>
			<%
		}
	}
	%>
	<td>Total</td>
</tr>
</thead>
<%	int duplication = 0;
	int total = 0;
	
	while (sBean.isNextRecord()) {
		total = 0;
		sBean.cBean.setFacilitiesFromDB();
		FlagDO flagDO = new FlagDO();
		Map<String, FlagDO> flagMap = flagDO.getFlagByContractor(sBean.cBean.id);
		String thisClass = sBean.getTextColor();
		if (!"cantSee".equals(thisClass))
			thisClass = ""; // TODO Add in the Contractor FlagColor
%>            <tr <%=sBean.getBGColor()%> class="<%=thisClass%>"> 
                <td class="right"><%=sBean.count-1%></td>
                <td>
				  <%=sBean.getActiveStar()%>
				  <a href="ContractorView.action?id=<%=sBean.aBean.id%>" class="<%=thisClass%>">
			        <%=sBean.aBean.name%></a>
				</td>
<%
		for (java.util.ListIterator<String> li = opAL.listIterator();li.hasNext();) {
			String opID = (String)li.next();
			String opName = (String)li.next();
			if (permissions.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
				if (sBean.cBean.generalContractors.contains(opID)) {
					marker = "<img src='images/icon_"+flagMap.get(opID).getFlag().toLowerCase()+"Flag.gif' width=12 height=15 border=0>";
					total++;
		 		} else
					marker = " ";
%>
			  <td class="center"><%=marker%></td>
<%			}
		}
		if (total-1 < 0)
			total = 1;
%>
			  <td class="right"><%=total%></td>
            </tr>
<%	duplication += total-1;
	} // while %>
</table>
<center>
<%=sBean.getLinksWithDynamicForm()%>
</center>

<%	}finally{
		sBean.closeSearch();
	}
%>
</body>
</html>