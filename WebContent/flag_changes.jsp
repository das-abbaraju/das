<%@ page language="java"%>
<jsp:useBean id="permissions"
	class="com.picsauditing.access.Permissions" scope="session" />
<%@page import="com.picsauditing.search.SelectSQL"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.beanutils.BasicDynaBean"%>
<%@page import="com.picsauditing.search.Database"%>
<html>
<head>
<title>Flag Changes</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<%
Database db = new Database();
SelectSQL sql = new SelectSQL("generalcontractors gc");
sql.addField("gc.id");
sql.addField("c.id AS conID");
sql.addField("c.name AS conName");
sql.addField("ci.lastRecalculation");
sql.addField("o.id AS opID");
sql.addField("o.name AS opName");
sql.addField("gc.flag");
sql.addField("gc.baselineFlag");
sql.addJoin("JOIN accounts c ON c.id = gc.subID AND c.status = 'Active'");
sql.addJoin("JOIN contractor_info ci ON c.id = ci.id");
sql.addJoin("JOIN accounts o ON o.id = gc.genID AND o.status = 'Active' AND o.type = 'Operator'");
sql.addWhere("gc.flag = gc.baselineFlag");
sql.addOrderBy("conName, opName");
sql.setLimit(100);
List<BasicDynaBean> list = db.select(sql.toString(), false);
%>
<script type="text/javascript">

function approve(id) {
	$("#row" + id).hide("slow");
}
</script>
</head>
<body>
<h1>Contractor Flag Changes</h1>

<table class="report">
<thead>
	<tr>
		<th>Contractor</th>
		<th>Operator</th>
		<th>Baseline</th>
		<th>Change</th>
		<th>Last Calc</th>
		<th></th>
	</tr>
</thead>
<tbody>
<%

for(BasicDynaBean row : list) {
	%><tr id="row<%= row.get("id") %>">
		<td><a href="ContractorView.action?id=<%= row.get("conID") %>" target="_BLANK"><%= row.get("conName") %></a></td>
		<td><a href="OperatorConfiguration.action?id=<%= row.get("opID") %>" target="_BLANK"><%= row.get("opName") %></a></td>
		<td><%= row.get("baselineFlag") %></td>
		<td><a href="ContractorFlag.action?id=<%= row.get("conID") %>&opID=<%= row.get("opID") %>" target="_BLANK"><%= row.get("flag") %></a></td>
		<td><%= row.get("lastRecalculation") %></td>
		<td><a href="#" onclick="approve(<%= row.get("id") %>); return false;" class=".button.green">Approve</a></td>
	</tr><%
}

%>
</tbody>
</table>

</body>
</html>