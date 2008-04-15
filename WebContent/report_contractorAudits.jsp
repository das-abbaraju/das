<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);
sql.startsWith(request.getParameter("startsWith"));
sql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID");
sql.addJoin("JOIN audit_type at ON ca.auditTypeID = at.auditTypeID");

sql.addField("ca.auditID");
sql.addField("at.auditName");
sql.addField("ca.createdDate");
sql.addField("ca.auditStatus");

sql.addJoin("JOIN users au ON ca.auditorID = au.id");
sql.addField("au.name as auditorName");

Report report = new Report();
report.setSql(sql);
report.setPageByResult(request.getParameter("showPage"));
report.setOrderBy(request.getParameter("orderBy"), "ca.createdDate DESC");

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Contractor Audits</title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
      <%@ include file="includes/selectReport.jsp"%>
      <span class="blueHeader">Contractor Audits</span><br>
    </td>
  </tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle"> 
		    <td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
		    <td align="center"><a href="?orderBy=at.auditName DESC" class="whiteTitle">Type</a></td>
		    <td align="center"><a href="?orderBy=ca.createdDate DESC" class="whiteTitle">Created</a></td>
		    <td align="center"><a href="?orderBy=ca.auditStatus DESC" class="whiteTitle">Status</a></td>
		    <td align="center"><a href="?orderBy=au.name" class="whiteTitle">Auditor</a></td>
	</tr>
	<%
	com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
	for(BasicDynaBean row: searchData) {
		%>
		<tr id="auditor_tr<%=row.get("id")%>" class="blueMain" <%=color.nextBgColor()%>>
		    <td><a href="pqf_view.jsp?auditID=<%=row.get("auditID")%>"><%=row.get("name")%></a></td>
		    <td><%=row.get("auditName")%></td>
		    <td><%=DateBean.toShowFormat(row.get("createdDate"))%></td>
		    <td><%=row.get("auditStatus")%></td>
		    <td><%=row.get("auditorName")%></td>
		</tr>
		<%
	}
	%>
</table>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>
