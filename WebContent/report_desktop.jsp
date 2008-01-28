<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<jsp:useBean id="pageBean" class="com.picsauditing.PICS.WebPage" scope ="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>
<%
	if (null == pBean) pBean = new PermissionsBean();
	pBean.checkAccess(PermissionsBean.FULL, response);
	//pBean.getPermissions().tryPermission(OpPerms.AssignAudits);
%>
<%
pageBean.setTitle("Schedule Desktop Audits");
pageBean.includeScriptaculous(true);

String action = request.getParameter("action");
if (action != null && action.equals("saveAuditor")) {
	String auditorID = request.getParameter("auditorID");
	String conID = request.getParameter("conID");
	ContractorBean cBean = new ContractorBean();
	cBean.setFromDB(conID);
	cBean.desktopAuditor_id = auditorID;
	cBean.desktopAssignedDate = DateBean.getTodaysDate();
	cBean.writeToDB();
	%><%=cBean.desktopAssignedDate%><%
	return;
}
SearchAccounts search = new SearchAccounts();

String orderBy = request.getParameter("orderBy");
if (orderBy != null) {
	search.sql.addOrderBy(orderBy);
}
search.sql.addOrderBy("c.desktopSubmittedDate DESC");

search.setType(SearchAccounts.Type.Contractor);
//search.sql.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc JOIN operators gc_o ON gc.genID = gc_o.id AND gc_o.canSeeDesktop = 'Yes')");

search.sql.addField("c.lastPayment");
search.sql.addField("c.pqfSubmittedDate");
search.sql.addField("c.desktopAuditor_id");
search.sql.addField("c.desktopAssignedDate");
search.sql.addField("c.desktopSubmittedDate");
search.sql.addField("c.desktopClosedDate");
search.sql.addField("c.desktopPercent");
search.sql.addField("c.desktopVerifiedPercent");

search.sql.addWhere("active='Y'");
search.sql.addWhere("isExempt='No'");
search.sql.addWhere("c.desktopSubmittedDate='0000-00-00' OR c.desktopSubmittedDate < DATE_ADD(CURDATE(),INTERVAL -34 MONTH)");
search.sql.addWhere("!(auditCompletedDate<>'0000-00-00' AND auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR))");

// Get from the PQF if they uploaded their manual and what the revision data of it was
//Please upload your manual as a single pdf or word document.
int manualQID = Integer.parseInt(com.picsauditing.PICS.pqf.Constants.MANUAL_PQF_QID);
search.addPQFQuestion(manualQID, true, "manualUploaded");

//What is the documented last date of revision of your safety manual?
int revisionQID = Integer.parseInt(com.picsauditing.PICS.pqf.Constants.MANUAL_REVISION_QID);
search.addPQFQuestion(revisionQID, false, "revisionDate");

String showPage = request.getParameter("showPage");
if (showPage != null) {
	search.setCurrentPage(Integer.valueOf(showPage));
}
search.startsWith(request.getParameter("startsWith"));

//SimpleResultSet searchData = search.doSearch();
List<BasicDynaBean> searchData = search.doSearch();

%>
<%@ include file="includes/header.jsp" %>
<script type="text/javascript">
function selectAuditor(conID) {
	var form = $('auditor_form'+conID);
	var auditor = form['auditorID'];
	pars = 'action=saveAuditor&conID='+conID+'&auditorID='+$F(auditor);
	
	var divName = 'auditor_td'+conID;
	$(divName).innerHTML = '<img src="images/ajax_process.gif" />';
	var myAjax = new Ajax.Updater(divName, 'report_desktop.jsp', {method: 'post', parameters: pars});
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
                  <span class="blueHeader">Schedule Desktop Audits</span>
                </td>
              </tr>
			</table>
            <table border="0" cellpadding="5" cellspacing="0" align="center">
              <tr> 
                <td height="30" align="left"><%=search.getStartsWithLinks()%></td>
                <td align="right"><%=search.getPageLinks()%></td>
              </tr>
            </table>
			<table border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle"> 
			    <td colspan=2><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=lastPayment DESC" class="whiteTitle">Paid</a></td>
 			    <td align="center"><a href="?orderBy=pqfSubmittedDate DESC" class="whiteTitle">PQF</a></td>
 			    <td align="center"><a href="?orderBy=desktopSubmittedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=desktopClosedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=revisionDate DESC" class="whiteTitle">Revision</a></td>
 			    <td align="center"><a href="?orderBy=desktopAuditor_id" class="whiteTitle">Auditor</a></td>
 			    <td align="center"><a href="?orderBy=desktopAssignedDate DESC" class="whiteTitle">Assigned</a></td>
  			  </tr>
<%
int counter = 0;
for(BasicDynaBean row: searchData) {
	counter++;
%>
			  <tr id="auditor_tr<%=row.get("id")%>" class="blueMain" <% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %> >
                <td align="right"><%=counter%></td>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>			    
			    <td><%=DateBean.toShowFormat(row.get("lastPayment"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("desktopSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("desktopClosedDate"))%></td>			   

		  	  </tr>
<%
} // end foreach loop
%>
		    </table>

<%@ include file="includes/footer.jsp" %>
