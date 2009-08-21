<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.ContractorApproval);

SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);
sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID="+ permissions.getAccountId());

sql.addField("gc.creationDate");
sql.addField("gc.workStatus");
sql.addWhere("active='Y'");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "gc.creationDate DESC");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

//report.addFilter(new SelectFilterInteger("conID", "gc.subID=?", request.getParameter("conID")));
report.addFilter(new SelectFilter("workStatus", "gc.workStatus = '?'", request.getParameter("workStatus"), "", ""));
report.addFilter(new SelectFilter("name", "a.name LIKE '?%'", request.getParameter("name")));

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Contractor Approvals</title>
<%@include file="struts/reports/reportHeader.jsp" %>
<script type="text/javascript">
function saveApproval(conID, status) {
	pars = 'conID='+conID+'&workStatus='+status;
	
	$('result_td'+conID).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('result_td'+conID, 'con_approval_ajax.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('result_tr'+conID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEE'});
}
</script>
<style>
form.smallform {
	margin: 0px;
	padding: 0px;
}
</style>
</head>
<body>
<h1>Contractor Approval</h1>

<div id="search">
<form id="form1" action="con_approvals.jsp">
	Name: <input type="text" name="name" value="<%= report.getFilterValue("name") %>" size="20" class="blueMain" />
	<select name="workStatus" class="blueMain">
		<option value="" <%="".equals(report.getFilterValue("workStatus"))?" SELECTED":"" %>>All</option>
		<option value="P"<%="P".equals(report.getFilterValue("workStatus"))?" SELECTED":"" %>>Pending</option>
		<option value="Y"<%="Y".equals(report.getFilterValue("workStatus"))?" SELECTED":"" %>>Yes</option>
		<option value="N"<%="N".equals(report.getFilterValue("workStatus"))?" SELECTED":"" %>>No</option>
	</select>
	<input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onClick="runSearch( 'form1')" >
	<input type="hidden" name="showPage" value="1"/>
	<input type="hidden" name="filter.startsWith"/>
	<input type="hidden" name="filter.ajax" value="false"/>	
	<input type="hidden" name="orderBy"  value="<%=request.getParameter("orderBy") == null ? "gc.creationDate DESC" : request.getParameter("orderBy") %>"/>
</form>
<div id="caldiv2" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>
</div>
<div>
<%=report.getPageLinksWithDynamicForm()%>
<p>Are the following companies currently working or approved to work for you?</p>
</div>

<table class="report">
	<thead>
	<tr>
		<td colspan=2><a href="javascript: changeOrderBy('form1','a.name');" class="whiteTitle">Contractor</a></td>
		<td class="center"><a href="javascript: changeOrderBy('form1','gc.creationDate DESC');" class="whiteTitle">Date Added</a></td>
		<td class="center"><a href="javascript: changeOrderBy('form1','workStatus');" class="whiteTitle">Approved</a></td>
		<td>&nbsp;</td>
		<td>Notes</td>
	</tr>
	</thead>
	<%
		com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(sql.getStartRow());
		for (BasicDynaBean row : searchData) {
			String rowID = row.get("id").toString();
	%>
	<tr id="result_tr<%=rowID%>"
		<%= color.nextBgColor() %>>
		<td class="right"><%=color.getCounter()%></td>
		<td><a href="ContractorView.action?id=<%=rowID%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("creationDate"))%></td>
		<td>
		<%
			if (permissions.hasPermission(OpPerms.ContractorApproval, OpType.Edit)) {
		%>
			<form class="smallform">
				<label><input type="radio" name="workStatus" onClick="saveApproval(<%=rowID%>, 'Y')"
					value="Y"<%="Y".equals(row.get("workStatus"))?" checked ":""%>>Yes</label>
				<label><input type="radio" name="workStatus" onClick="saveApproval(<%=rowID%>, 'N')"
					value="N"<%="N".equals(row.get("workStatus"))?" checked ":""%>>No</label>
				<label><input type="radio" name="workStatus" onClick="saveApproval(<%=rowID%>, 'P')"
					value="P"<%="P".equals(row.get("workStatus"))?" checked ":""%>>Pending</label>
			</form>
		<% } %>
		</td>
		<td id="result_td<%=rowID%>" style="font-style: italic;">
		</td>
		<td><a href="ContractorNotes.action?id=<%=rowID%>">Add Notes</a></td>
	</tr>
	<%
		} // end foreach loop
	%>
</table>
<div><%=report.getPageLinksWithDynamicForm()%></div>
</body>
</html>