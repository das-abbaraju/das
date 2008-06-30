<%@page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean"
	scope="page" />
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean"
	scope="page" />
<%
	try {
		tBean.setFromDB();
		sBean.orderBy = "name";
		sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
		sBean.writeToExcelFile(config, pBean);
%>
<html>
<head>
<title>Contractor Contact Information</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
</head>
<body>
<h1>Contractor Contact Information</h1>
<div><%=sBean.getExcelLink(permissions.getAccountIdString())%></div>

<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show
Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#"
	onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" action="report_contactInfo.jsp" method="post" style="display: none">
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value="<%=sBean.selected_startsWith == null ? "" : sBean.selected_startsWith %>"/>
	<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "dateCreated DESC" : sBean.orderBy %>"/>
<%
	if (permissions.isAdmin())
			out.println(SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain",
					sBean.selected_generalContractorID));
		if (permissions.isCorporate())
			out.println(pBean.oBean.getFacilitySelect("generalContractorID", "forms",
					sBean.selected_generalContractorID));
%>
<table border="0" cellpadding="2" cellspacing="0">
	<tr>
		<td><input name="name" type="text" class="forms"
			value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
		<td><%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%></td>
		<td><input name="imageField" type="image"
			src="images/button_search.gif" width="70" height="23" border="0">
	</tr>
	<tr>
		<td colspan=3><%=SearchBean.getSearchIndustrySelect("industry", "forms", sBean.selected_industry)%>
		</td>
	</tr>
</table>
<div class="alphapaging">
<%=sBean.getStartsWithLinksWithDynamicForm()%>
</div>
</form>
</div>

<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>

<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor</td>
		<td>Address</td>
		<td class="center">Contact</td>
		<td class="center">Phone</td>
	</tr>
	</thead>
	<%
		while (sBean.isNextRecord()) {
				String thisClass = ""; // TODO Add in the Contractor FlagColor
	%>
	<tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
		<td class="right"><%=sBean.count - 1%></td>
		<td><a href="ContractorView.action?id=<%=sBean.aBean.id%>"
			title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a></td>
		<td><%=sBean.aBean.address%><br><%=sBean.aBean.city%>, <%=sBean.aBean.state%>
		<%=sBean.aBean.zip%></td>
		<td class="center"><%=sBean.aBean.contact%></td>
		<td class="center"><%=sBean.aBean.phone%><br><%=sBean.aBean.phone2%></td>
	</tr>
	<%
		}
	%>
</table>
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>

<%
	sBean.closeSearch();
	} finally {
		sBean.closeSearch();
	}
%>

</body>
</html>
