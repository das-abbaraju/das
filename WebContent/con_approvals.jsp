<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.ContractorApproval);

pageBean.setTitle("Contractor Approvals");
pageBean.includeScriptaculous(true);

String action = request.getParameter("action");
if (action != null && action.equals("save")) {
	GeneralContractor gcBean = new GeneralContractor();
	gcBean.setConID(Integer.parseInt(request.getParameter("conID")));
	gcBean.setOpID(permissions.getAccountId());
	gcBean.setApprovedStatus(request.getParameter("status"));
	if (gcBean.getApprovedStatus().length() > 0)
		gcBean.setApprovedByID(permissions.getUserId());
	gcBean.save();
	%> on <%=DateBean.getTodaysDate() %> by <%=permissions.getName() %> <%
	return;
}
SearchAccounts search = new SearchAccounts();

String orderBy = request.getParameter("orderBy");
if (orderBy != null) {
	search.sql.addOrderBy(orderBy);
}
search.sql.addOrderBy("gc.dateAdded DESC");

search.setType(SearchAccounts.Type.Contractor);
search.sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID="+ permissions.getAccountId());
search.sql.addJoin("LEFT JOIN users u ON gc.approvedByID = u.id");

search.sql.addField("gc.dateAdded");
search.sql.addField("gc.approvedStatus");
search.sql.addField("gc.approvedByID");
search.sql.addField("u.name as user_displayname");
search.sql.addField("gc.approvedDate");

search.sql.addWhere("active='Y'");
String status = request.getParameter("status");
if (status==null || status.equals(""))
	search.sql.addWhere("gc.approvedStatus = null");
else if (status.equals("Yes") || status.equals("No"))
	search.sql.addWhere("gc.approvedStatus = '"+status+"'");

search.setPageByResult(request);
search.setLimit(50);

List<BasicDynaBean> searchData = search.doSearch();
%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
function saveApproval(conID, status) {
	pars = 'action=save&conID='+conID+'&status='+status;
	
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
		<td>
		<form action="con_approvals.jsp" method="get">
			<select name="status" class="blueMain">
				<option value="">Pending Approval</option>
				<option value="Yes"<%="Yes".equals(status)?" SELECTED":"" %>>Approved</option>
				<option value="No"<%="No".equals(status)?" SELECTED":"" %>>Not Approved</option>
				<option value="All"<%="All".equals(status)?" SELECTED":"" %>>All</option>
			</select>
			<input type="submit" value="Show" class="blueMain">
		</form>
		</td>
	</tr>
	<tr>
		<td align="right"><%=search.getPageLinks()%></td>
	</tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td colspan=2><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="?"
			class="whiteTitle">Date Added</a></td>
		<td align="center"><a href="?orderBy=approvedStatus"
			class="whiteTitle">Approved</a></td>
		<td align="center"><a href="?orderBy=approvedDate DESC"
			class="whiteTitle">Date / By</a></td>
	</tr>
	<%
		int counter = 0;
		for (BasicDynaBean row : searchData) {
			counter++;
			String rowID = row.get("id").toString();
	%>
	<tr id="result_tr<%=rowID%>" class="blueMain"
		<% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %>>
		<td align="right"><%=counter%></td>
		<td><a href="contractor_detail.jsp?id=<%=rowID%>"><%=row.get("name")%></a></td>
		<td><%=DateBean.toShowFormat(row.get("dateAdded"))%></td>
		<td>
			<form class="smallform">
				<label><input type="radio" name="approveStatus" onClick="saveApproval(<%=rowID%>, 'Yes')"
					value="Yes"<%="Yes".equals(row.get("approvedStatus"))?" SELECTED":""%>>Yes</label>
				<label><input type="radio" name="approveStatus" onClick="saveApproval(<%=rowID%>, 'No')"
					value="No"<%="No".equals(row.get("approvedStatus"))?" SELECTED":""%>>No</label>
			</form>
		</td>
		<td id="result_td<%=rowID%>">sd
		<% if (row.get("approvedStatus") != null) { %>
			<%=DateBean.toShowFormat(row.get("approvedDate"))%>
			<%=row.get("user_displayname")%>
		<% } %>
		</td>
	</tr>
	<%
		} // end foreach loop
	%>
</table>

<%@ include file="includes/footer.jsp" %>
