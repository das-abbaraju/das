<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%
permissions.tryPermission(OpPerms.AssignAudits);
boolean canEdit = permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);

pageBean.setTitle("Schedule Drug &amp; Alcohol Audits");
pageBean.includeScriptaculous(true);

String action = request.getParameter("action");
if (action != null) {
	String outputText = "<span=\"color: red\">no permission</span>";
	if (canEdit) {
		String conID = request.getParameter("conID");
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(conID);
		if (action.equals("saveAuditor")) {
			String auditorID = request.getParameter("auditorID");
			cBean.daAuditor_id = auditorID;
			cBean.daAssignedDate = DateBean.getTodaysDate();
			if (auditorID.equals("0")) cBean.daAssignedDate = "";
			outputText = "<b>" + cBean.daAssignedDate + "</b>";
		}
		if (action.equals("notRequired")) {
			cBean.daRequired = "No";
			outputText = "<i>not required</i>";
		}
		cBean.writeToDB();
	}
	%><%=outputText%><%
	return;
}

SearchAccounts search = new SearchAccounts();

String orderBy = request.getParameter("orderBy");
if (orderBy != null) {
	search.sql.addOrderBy(orderBy);
}
search.sql.addOrderBy("c.pqfSubmittedDate DESC");

search.setType(SearchAccounts.Type.Contractor);
search.sql.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc JOIN operators gc_o ON gc.genID = gc_o.id AND gc_o.canSeeDA = 'Yes')");

search.sql.addField("c.pqfSubmittedDate");
search.sql.addField("c.daAuditor_id");
search.sql.addField("c.daAssignedDate");
search.sql.addField("c.daSubmittedDate");
search.sql.addField("c.daClosedDate");

search.addPQFQuestion(894, false, "requiredAnswer"); //q318.answer
search.sql.addWhere("q894.answer = 'Yes' OR c.daRequired IS NULL OR c.daRequired = 'Yes'");
search.sql.addWhere("c.daSubmittedDate = '0000-00-00'");
String showPage = request.getParameter("showPage");
if (showPage != null) {
	search.setCurrentPage(Integer.valueOf(showPage));
}
search.startsWith(request.getParameter("startsWith"));
List<BasicDynaBean> searchData = search.doSearch();

%>
<%@ include file="includes/header.jsp" %>
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
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
      <%@ include file="includes/selectReport.jsp"%>
      <span class="blueHeader">Schedule Drug and Alcohol Audits</span><br>
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
int counter = 0;
for(BasicDynaBean row: searchData) {
	counter++;
%>
	<tr id="auditor_tr<%=row.get("id")%>" class="blueMain" <% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %> >
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
<p align="center"><%=search.getPageLinks()%></p>
<%@ include file="includes/footer.jsp" %>
