<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="com.picsauditing.search.*"%>
<%@page import="java.util.*"%>
<%
SelectAccount sql = new SelectAccount();

sql.setType(SelectAccount.Type.Contractor);
sql.addJoin("JOIN contractor_audit ca ON a.id = ca.conID");

sql.addField("ca.*");

sql.startsWith(request.getParameter("startsWith"));

Report report = new Report();
report.setSql(sql);
report.setOrderBy(request.getParameter("orderBy"), "c.pqfSubmittedDate DESC");

report.setPageByResult(request);
report.setLimit(50);

List<BasicDynaBean> searchData = report.getPage();

%>
<html>
<head>
<title>Schedule Drug &amp; Alcohol Audits</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript">
function selectAuditor(conID) {
	var form = $('auditor_form'+conID);
	var auditor = form['daAuditor_id'];
	pars = 'action=saveAuditor&conID='+conID+'&auditorID='+$F(auditor);
	
	var divName = 'auditor_td'+conID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_daAudit.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+conID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
function notRequired(conID) {
	pars = 'action=notRequired&conID='+conID;
	
	var divName = 'auditor_td'+conID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_daAudit.jsp', {method: 'post', parameters: pars});
	new Effect.Highlight($('auditor_tr'+conID), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
<style>
.auditselect {
	margin: 0px;
	padding: 0px;
}
</style>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
      <%@ include file="includes/selectReport.jsp"%>
      <span class="blueHeader">Schedule Drug &amp; Alcohol Audits</span><br>
      <a href="report_daAudit.jsp" class="blueMain">Refresh</a>
    </td>
  </tr>
</table>
<table border="0" cellpadding="1" cellspacing="1" align="center">
	<tr bgcolor="#003366" class="whiteTitle"> 
			    <td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=pqfSubmittedDate DESC" class="whiteTitle">PQF</a></td>
 			    <td align="center"><a href="?orderBy=daSubmittedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=daClosedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=daAuditor_id DESC,name" class="whiteTitle">Auditor</a></td>
 			    <td align="center"><a href="?orderBy=daAssignedDate DESC" class="whiteTitle">Assigned</a></td>
 			    <td align="center"><a href="?orderBy=requiredAnswer, name" class="whiteTitle" title="Does your company have employees who are covered under DOT OQ requirements?">Rqd</a></td>
 			    <td align="center">Not Required</td>
	</tr>
<%
com.picsauditing.util.ColorAlternater color = new com.picsauditing.util.ColorAlternater();
for(BasicDynaBean row: searchData) {
%>
	<tr id="auditor_tr<%=row.get("id")%>" class="blueMain" <%=color.nextBgColor()%>>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
			    <td><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("daSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("daClosedDate"))%></td>
			    <td>
			    	<form class="auditselect" id="auditor_form<%=row.get("id")%>">
			    		<%=AUDITORS.getAuditorsSelect("daAuditor_id","forms",row.get("daAuditor_id").toString(),"selectAuditor("+row.get("id")+")")%>
			    	</form>
			    </td>
			    <td id="auditor_td<%=row.get("id")%>"><%=DateBean.toShowFormat(row.get("daAssignedDate"))%></td>
			    <td><%=row.get("requiredAnswer")%></td>
			    <td>
			    	<form class="auditselect" id="">
			    		<input type="button" class="blueMain" value="Not Required" onclick="notRequired(<%=row.get("id")%>)" name="required_button<%=row.get("id")%>" />
			    	</form>
			    </td>
	</tr>
<%
}
%>
</table>
<p align="center"><%=report.getPageLinks()%></p>
</body>
</html>