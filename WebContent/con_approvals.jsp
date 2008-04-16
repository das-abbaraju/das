<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.ContractorApproval);

String action = request.getParameter("action");
if (action != null && action.equals("save")) {
	permissions.tryPermission(OpPerms.ContractorApproval, OpType.Edit);
	GeneralContractor gcBean = new GeneralContractor();
	gcBean.setConID(Integer.parseInt(request.getParameter("conID")));
	gcBean.setOpID(permissions.getAccountId());
	gcBean.setWorkStatus(request.getParameter("workStatus"));
	gcBean.save();
	%>Saved<%
	return;
}
SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);
sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID="+ permissions.getAccountId());

sql.addField("gc.dateAdded");
sql.addField("gc.workStatus");
sql.addWhere("active='Y'");

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "gc.dateAdded DESC");

report.setPageByResult(request.getParameter("showPage"));
report.setLimit(50);

//report.addFilter(new SelectFilterInteger("conID", "gc.subID=?", request.getParameter("conID")));
report.addFilter(new SelectFilter("workStatus", "gc.workStatus = '?'", request.getParameter("workStatus"), "P", ""));
report.addFilter(new SelectFilter("name", "a.name LIKE '?%'", request.getParameter("name")));

List<BasicDynaBean> searchData = report.getPage();

%>
<%@page import="com.picsauditing.PICS.redFlagReport.FlagCalculator"%>
<html>
<head>
<title>Contractor Approvals</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script src="js/Search.js" type="text/javascript"></script>
<script type="text/javascript">
function saveApproval(conID, status) {
	pars = 'action=save&conID='+conID+'&workStatus='+status;
	
	$('result_td'+conID).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater('result_td'+conID, 'con_approvals.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('result_tr'+conID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
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
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="buttons"><%@ include
			file="includes/selectReport.jsp"%> <span
			class="blueHeader">Contractor Approvals</span></td>
	</tr>
</table>

<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td class="blueMain">
		<form id="form1" name="form1" action="con_approvals.jsp" method="get">
			Name: <input type="text" name="name" value="<%= report.getFilterValue("name") %>" size="20" class="blueMain" />
			<select name="workStatus" class="blueMain">
				<option value="" <%="".equals(request.getParameter("workStatus"))?" SELECTED":"" %>>All</option>
				<option value="P"<%="P".equals(request.getParameter("workStatus"))?" SELECTED":"" %>>Pending</option>
				<option value="Y"<%="Y".equals(request.getParameter("workStatus"))?" SELECTED":"" %>>Yes</option>
				<option value="N"<%="N".equals(request.getParameter("workStatus"))?" SELECTED":"" %>>No</option>
			</select>
			<input type="submit" value="Show" onClick="runSearch( 'form1')" class="blueMain">

			<input type="hidden" name="showPage" value="1"/>
			<input type="hidden" name="startsWith" value=""/>
			<input type="hidden" name="orderBy"  value="<%=request.getParameter("orderBy") == null ? "gc.dateAdded DESC" : request.getParameter("orderBy") %>"/>
		</form>

		</td>
	</tr>
	<tr>
		<td align="right"><%=report.getPageLinksWithDynamicForm()%></td>
	</tr>
</table>

<p>Are the following companies currently working or approved to work for you?</p>

<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2><a href="javascript: changeOrderBy('form1','a.name');" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','gc.dateAdded DESC');" class="whiteTitle">Date Added</a></td>
		<td align="center"><a href="javascript: changeOrderBy('form1','workStatus');" class="whiteTitle">Approved</a></td>
		<td>&nbsp;</td>
	</tr>
	<%
		com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater(sql.getStartRow());
		for (BasicDynaBean row : searchData) {
			String rowID = row.get("id").toString();
	%>
	<tr id="result_tr<%=rowID%>" class="blueMain"
		<%= color.nextBgColor() %>>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="contractor_detail.jsp?id=<%=rowID%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("dateAdded"))%></td>
		<td>
		<% if (permissions.hasPermission(OpPerms.ContractorApproval, OpType.Edit)) { %>
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
	</tr>
	<%
		} // end foreach loop
	%>
</table>

</body>
</html>