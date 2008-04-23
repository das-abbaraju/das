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
  <script language="JavaScript" SRC="js/Search.js"></script>
<title>Corporate Contractors Report</title>
<script src="js/Search.js" type="text/javascript"></script>
</head>
<body>
<div align="center">
	<form id="form1" name="form1" method="post" action="report_operatorContractor.jsp">
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
				<td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
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
            
	<%=sBean.getLinksWithDynamicForm()%>
</div>
<table border="0" cellpadding="1" cellspacing="1">
<tr>
	<td colspan="2" bgcolor="#993300" align="left" class="whiteTitle">Contractor</td>
	<%
	int count = 0;
	for (java.util.ListIterator<String> li = opAL.listIterator();li.hasNext();){
		String opID = (String)li.next();
		String opName = (String)li.next();
		if (permissions.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
			%>
			<td bgcolor="#003366" class="whiteTitleSmall"><%= opName %></td>
			<%
		}
	}
	%>
	<td bgcolor="#003366" class="whiteTitleSmall">Total</td>
</tr>
<%	int duplication = 0;
	int total = 0;
	
	while (sBean.isNextRecord()) {
		total = 0;
		sBean.cBean.setFacilitiesFromDB();
		FlagDO flagDO = new FlagDO();
		Map<String, FlagDO> flagMap = flagDO.getFlagByContractor(sBean.cBean.id);
		String thisClass = sBean.getTextColor();
		if (!"cantSee".equals(thisClass))
			thisClass = ContractorBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>            <tr <%=sBean.getBGColor()%> class="<%=thisClass%>"> 
                <td align="right"><%=sBean.count-1%></td>
                <td>
				  <%=sBean.getActiveStar()%>
				  <a href="ContractorView.action?id=<%=sBean.aBean.id%>" class="<%=thisClass%>">
			        <%=sBean.aBean.name%></a>
				</td>
<%//		cBean.setFromDB(sBean.aBean.id);
		for (java.util.ListIterator<String> li = opAL.listIterator();li.hasNext();) {
			String opID = (String)li.next();
			String opName = (String)li.next();
			if (permissions.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
				if (sBean.cBean.generalContractors.contains(opID)) {
					marker = "<img src='images/icon_"+flagMap.get(opID).getFlag().toLowerCase()+"Flag.gif' width=12 height=15 border=0>";
//					marker = "X";
					total++;
		 		} else
					marker = " ";
%>
			  <td align="center"><%=marker%></td>
<%			}//if
		} // for
		if (total-1 < 0)
			total = 1;
%>
			  <td align="center"><%=total%></td>
            </tr>
<%	duplication += total-1;
	} // while %>
</table>
<br><center><%=sBean.getLinksWithDynamicForm()%></center><br>

<%	}finally{
		sBean.closeSearch();
	}
%>
</body>
</html>