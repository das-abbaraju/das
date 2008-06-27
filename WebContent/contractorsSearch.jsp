<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.OpPerms" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="com.picsauditing.mail.*"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.OperatorAccountDAO"%>
<%@page import="com.picsauditing.jpa.entities.OperatorAccount"%>
<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Not Contractor");
try {
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String searchName = request.getParameter("name");
	String changed = request.getParameter("changed");
	String filter = "&entireDB=Y";
	if (null==searchName)
		searchName = "";
	else
		filter += "&name="+searchName;
	String searchTradeID = request.getParameter("trade");
	if (null==searchTradeID)
		searchTradeID = "";
	else
		filter += "&trade="+searchTradeID;
	boolean isSearchNameOK = !("".equals(searchName)) && !sBean.DEFAULT_NAME.equals(searchName) && !(searchName.length()<sBean.MIN_NAME_SEARCH_LENGTH);
	boolean isSearchTradeIDOK = !("".equals(searchTradeID)) && !tBean.DEFAULT_SELECT_TRADE_ID.equals(searchTradeID);
	boolean doSearch = "0".equals(changed) || isSearchNameOK || isSearchTradeIDOK;
	
	AuditBuilder auditBuilder = (AuditBuilder)SpringUtils.getBean("AuditBuilder");
	if ("Add".equals(action) && pBean.oBean.canAddContractors()){
		if (pBean.oBean.addSubContractor(permissions.getAccountId(), actionID)) {
			int conID = new Integer(actionID);
			pBean.canSeeSet.add(actionID);
			doSearch = true;
			
			AccountBean aBean = new AccountBean();
			aBean.setFromDB(permissions.getAccountIdString());
			
			// Send the contractors an email that the operator added them
			EmailContractorBean emailer = (EmailContractorBean)SpringUtils.getBean("EmailContractorBean");
			emailer.setPermissions(permissions);
			OperatorAccountDAO operatorDAO = (OperatorAccountDAO)SpringUtils.getBean("OperatorAccountDAO");
			OperatorAccount operator = operatorDAO.find(permissions.getAccountId());
			emailer.addToken("opAcct", operator);
			emailer.sendMessage(EmailTemplates.contractoradded, conID);
			
			User currentUser = new User();
			currentUser.setFromDB(permissions.getUserIdString());
			
			ContractorBean.addNote(conID, permissions, "Added contractor to "+operator.getName());
			auditBuilder.buildAudits(conID);
		}
	}
	
	if ("Remove".equals(action) && pBean.oBean.canAddContractors()){
		int conID = new Integer(actionID);
		if (pBean.oBean.removeSubContractor(permissions.getAccountId(), actionID)) {
			pBean.canSeeSet.remove(actionID);
			AccountBean aBean = new AccountBean();
			aBean.setFromDB(permissions.getAccountIdString());
			ContractorBean.addNote(conID, permissions, "Removed from "+aBean.name);
			auditBuilder.buildAudits(conID);
		}
	}//if
	sBean.orderBy = "name";
	sBean.setCanSeeSet(pBean.canSeeSet);
	if (doSearch)
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	String showPage = request.getParameter("showPage");
	if (showPage == null)	showPage = "1";
%>
<html>
<head>
<title>Search for Contractors</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
  <script language="JavaScript">
	function addContractor( cid )
	{	
		var form = document.getElementById('form1');
		form['action'].value="Add";		
		form['actionID'].value=cid;		
		form['showPage'].value=<%= showPage %>;		
		
					
		form.submit();

		return false;	
	}
	function removeContractor( cid )
	{
		var form = document.getElementById('form1');
		form['action'].value="Remove";
		form['actionID'].value=cid;
		form['showPage'].value=<%= showPage %>;
		
		form.submit();
	
		return false;
	}  
  </script>
</head>
<body>
<h1>Search For New Contractors</h1>

<%	if (!doSearch){%>
<div class="redMain">* For a valid search, you must either select a trade<br>
or enter part of the name (at least 3 characters long)</div>
<%	} %>
<div id="search">
<div id="showSearch" style="display: none"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" ><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<form id='form1' name="form1" method="post">
	<table border="0" cellpadding="2" cellspacing="0">
		<tr>
			<td>
				<input name="name" type="text" class="forms" value="<%=sBean.selected_name.equals("") ? SearchBean.DEFAULT_NAME : sBean.selected_name%>" size="20" onFocus="clearText(this)">
				<% if (permissions.isOperator()) {%>
					<%=Inputs.inputSelect("flagStatus","forms", sBean.selected_flagStatus,SearchBean.FLAG_STATUS_ARRAY)%>
				<% } %>
				<%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%>
				<input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0" onClick="runSearch('form1');">
			</td>
		</tr>
		<tr>
			<td>
				<%=Inputs.inputSelect("performedBy","forms",sBean.selected_performedBy,TradesBean.PERFORMED_BY_ARRAY)%>
				<%=statesLicensedInList.getQuestionListQIDSelect("Office Location","officeIn", "forms", sBean.selected_officeIn,SearchBean.DEFAULT_OFFICE_IN)%>                       
				<input name="taxID" type="text" class="forms" value="<%=sBean.selected_taxID.equals("") ? SearchBean.DEFAULT_TAX_ID : sBean.selected_taxID%>" size="9" onFocus="clearText(this)">
				<span class=redMain>*must be 9 digits</span>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<label><%=Inputs.getCheckBoxInput("searchCorporate", "forms",sBean.searchCorporate,"Y")%>
				Check to limit search to contractors already working within my parent corporation</label>
			</td>
		</tr>
	</table>
	<input type="hidden" name="entireDB" value="Y">
	<input type="hidden" name="actionID" value="0">
	<input type="hidden" name="action" value="">
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="startsWith" value=""/>
	<input type="hidden" name="orderBy"  value="name"/>
</form>
</div> 

<%	if (doSearch) {%>
<div><%=sBean.getLinksWithDynamicForm()%></div>
<table class="report">
<thead>
  <tr> 
    <td></td>
    <td>Contractor</td>
    <td>Address</td>
    <td align="center" bgcolor="#6699CC">Contact</td>
    <td align="center" bgcolor="#336699">Phone</td>
    <td align="center" bgcolor="#336699">Performed By</td>
    <td align="center" bgcolor="#336699">Flag</td>
    <td align="center" bgcolor="#336699"></td>
  </tr>
</thead>
<%	while (sBean.isNextRecord()) {
		boolean canSee = pBean.canSeeSet.contains(sBean.aBean.id);
		String thisClass = canSee ? "" : "na";
	%>
	<tr <%=sBean.getBGColor()%> class=<%=thisClass%>>
		<td class="right"><%=sBean.count-1%></td>
		<td>
			<% if (canSee) { %>
				<a href="ContractorView.action?id=<%=sBean.aBean.id%>"><%=sBean.aBean.name%></a>
			<% } else { %>
				<%=sBean.aBean.name%>
			<% } %>
		</td>
		<td><%=sBean.aBean.city%>, <%=sBean.aBean.state%></td>
		<td class="center"><%=sBean.aBean.contact%></td>
		<td class="center"><%=sBean.aBean.phone%><br><%=sBean.aBean.phone2%></td>
		<td class="center"><%=sBean.tradePerformedBy%></td>
		<td class="center">
			<%=permissions.isCorporate() 
			? "<a href='con_selectFacilities.jsp?id="+sBean.aBean.id+"'>Facilities</a>"
			: sBean.getFlagLink()%>
		</td>
		<td class="center">
	<%	if (!permissions.isCorporate()) {
			if (permissions.hasPermission(OpPerms.AddContractors)
					&& !pBean.canSeeSet.contains(sBean.aBean.id)) {
				// This person can add contractors from their list and this contractor isn't there yet
				%>
				<input name="action" type="submit" class="buttons" value="Add" onClick="return addContractor(<%=sBean.aBean.id%>);">
				<%
			}
			if (permissions.hasPermission(OpPerms.RemoveContractors)
					&& pBean.canSeeSet.contains(sBean.aBean.id)) {
				// This person can remove this contractor and the contractor is currently on the list
				%>
	            <input name="action" type="submit" class="buttons" value="Remove" onClick="return removeContractor(<%=sBean.aBean.id%>);">
	<%		} %>
	<%	} %>
		</td>
	</tr>
<%	}//while %>
</table>
<div><%=sBean.getLinksWithDynamicForm()%></div>
<%	}//if %>
<%	} finally {
	sBean.closeSearch();
}
%>
</body>
</html>
