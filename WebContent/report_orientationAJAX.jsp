<%@page import="com.picsauditing.search.Report"%>
<%@page import="com.picsauditing.search.SelectSQL"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%><html>
<%
Report report = new Report();
SelectSQL sql = new SelectSQL("accounts a");
sql.addJoin("JOIN contractor_info c ON a.id = c.id");
sql.addJoin("JOIN contractor_audit ca ON ca.conID = c.id");

sql.addWhere("ca.auditTypeID = 86");

sql.addField("a.name");
sql.addField("c.id conID");
sql.addField("ca.auditFor");
sql.addField("ca.id auditID");
sql.addField("DATE_FORMAT(ca.completedDate, '%m/%d/%Y') finished");

int searchName = 0;
String name = request.getParameter("name");
if (name != null && !name.contains("- Company Name -")) {
	sql.addWhere("a.name LIKE '%"+name+"%'");
	searchName = 1;
}

String employee = request.getParameter("employee");
if (employee != null && !employee.contains("- Employee Name -")) {
	sql.addWhere("ca.auditFor LIKE '%"+employee+"%'");
	searchName = 2;
}

if (searchName == 1)
	sql.addOrderBy("a.name");
else if (searchName == 2)
	sql.addOrderBy("ca.auditFor");
else
	sql.addOrderBy("ca.completedDate DESC");
report.setSql(sql);
report.setLimit(100);
int page_num = 1;
try {
	page_num = Integer.parseInt(request.getParameter("showPage"));
	if (page_num < 0)
		page_num = 1;
} catch (Exception e) {

}
report.setCurrentPage(page_num);

List<BasicDynaBean> data = report.getPage();

%>


<%= report.getPageLinksWithDynamicForm() %>

<table class="report">
<thead>
	<tr>
		<td>Contractor</td>
		<td>Employee</td>
		<td>Date</td>
	</tr>
</thead>
<% for (BasicDynaBean item : data) { %>
	<tr>
		<td><a href="ContractorView.action?id=<%= item.get("conID") %>"><%= item.get("name") %></a></td>
		<td><a href="Audit.action?auditID=<%= item.get("auditID") %>"><%= item.get("auditFor") %></a></td>
		<td><%= item.get("finished") %></td>
	</tr>
<% } %>
</table>

<%= report.getPageLinksWithDynamicForm() %>
