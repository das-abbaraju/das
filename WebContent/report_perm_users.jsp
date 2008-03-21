<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.NCMS);

SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);

sql.addJoin("JOIN ncms_desktop d ON (c.taxID = d.fedTaxID AND c.taxID != '') OR a.name=d.ContractorsName");
sql.addWhere("a.id IN ( "
				+ "SELECT a.id FROM accounts a JOIN ncms_desktop d ON a.name = d.ContractorsName WHERE d.remove = 'No' "
				+ "UNION "
				+ "SELECT c.id FROM contractor_info c JOIN ncms_desktop d ON c.taxID = d.fedTaxID WHERE d.remove = 'No' "
				+ ") ");
sql.addField("c.taxID");
sql.addField("d.fedTaxID");
sql.addField("d.ContractorsName");
sql.addField("d.lastReview");

sql.startsWith(request.getParameter("startsWith"));

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "a.name");

report.setPageByResult(request);
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>NCMS Data</title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0"
	align="center">
	<tr>
		<td height="70" colspan="2" align="center" class="buttons"><%@ include
			file="includes/selectReport.jsp"%> <span
			class="blueHeader">NCMS Data</span></td>
	</tr>
</table>
<table border="0" cellpadding="5" cellspacing="0" align="center">
	<tr>
		<td height="30" align="left"><%=report.getStartsWithLinks()%></td>
		<td align="right"><%=report.getPageLinks()%></td>
	</tr>
</table>
<table width="657" border="0" cellpadding="1" cellspacing="1"
	align="center">
	<tr bgcolor="#003366" class="whiteTitle">
		<td width="150" colspan="2">Contractor</td>
		<td width="150">NCMS Name</td>
		<td align="center">PICS Tax ID</td>
		<td align="center">NCMS Tax ID</td>
		<td align="center">NCMS Last Review</td>
	</tr>
	<%
		com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
		for (BasicDynaBean row : searchData) {
			String temp = (String) row.get("ContractorsName");
			if (temp == null)
				temp = "";
			String contractorName = java.net.URLEncoder.encode(temp
					.toString(), "UTF-8");
	%>
	<tr class="blueMain" <%=color.nextBgColor()%>>
		<td align="right"><%=color.getCounter()%></td>
		<td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"
			title="view <%=row.get("name")%> details"><%=row.get("name")%></a></td>
		<td><a
			href="report_ncmsIndividual.jsp?conID=<%=row.get("id")%>&name=<%=contractorName%>"><%=row.get("ContractorsName")%></a></td>
		<td><%=row.get("taxID")%></td>
		<td><%=row.get("fedTaxID")%></td>
		<td><%=row.get("lastReview")%></td>
	</tr>
	<%
		}
	%>
</table>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>