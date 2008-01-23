<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>
<%
String action = request.getParameter("action");
if (action != null && action.equals("saveAuditor")) {
	String auditorID = request.getParameter("auditorID");
	String conID = request.getParameter("conID");
	ContractorBean cBean = new ContractorBean();
	cBean.setFromDB(conID);
	cBean.daAuditor_id = auditorID;
	cBean.daAssignedDate = DateBean.getTodaysDate();
	cBean.writeToDB();
	%><%=cBean.daAssignedDate%><%
	return;
}
SearchAccounts search = new SearchAccounts();

String orderBy = request.getParameter("orderBy");
if (orderBy != null) {
	search.sql.addOrderBy(orderBy);
}
search.sql.addOrderBy("a.name");

search.setType(SearchAccounts.Type.Contractor);
search.sql.addJoin("JOIN generalcontractors gc ON a.id = gc.subID");
search.sql.addJoin("JOIN operators gc_o ON gc.genID = gc_o.id AND gc_o.canSeeDA = 'Yes'");

search.sql.addField("c.lastPayment");
search.sql.addField("c.pqfSubmittedDate");
search.sql.addField("c.daAuditor_id");
search.sql.addField("c.daAssignedDate");
search.sql.addField("c.daSubmittedDate");
search.sql.addField("c.daClosedDate");
search.sql.addField("c.daPercent");
search.sql.addField("c.daVerifiedPercent");

search.addPQFQuestion(318); //q318.answer
//search.setLimit(10);
String showPage = request.getParameter("showPage");
if (showPage != null) {
	search.setCurrentPage(Integer.valueOf(showPage));
}
String startsWith = request.getParameter("startsWith");
if (startsWith != null) {
	search.sql.addWhere("a.name LIKE '"+Utilities.escapeQuotes(startsWith)+"%'");
}
SimpleResultSet searchData = search.doSearch();
%>
<html>
<head>
  <title>PICS - Schedule Drug and Alcohol Audits</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
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
  </script>
  <style>
  .auditselect {
  	margin: 0px;
  	padding: 0px;
  }
  </style>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	  <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3" align="center" class="blueMain">
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr>
                <td height="70" colspan="2" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">Schedule Drug and Alcohol Audits</span>
                </td>
              </tr>
			</table>
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr> 
                <td height="30" align="left"><%=search.getStartsWithLinks()%></td>
                <td align="right"><%=search.getPageLinks()%></td>
              </tr>
            </table>
			<table width="657" border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle"> 
			    <td colspan=2><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
 			    <td align="center"><a href="?orderBy=lastPayment" class="whiteTitle">Paid</a></td>
 			    <td align="center"><a href="?orderBy=pqfSubmittedDate DESC" class="whiteTitle">PQF</a></td>
 			    <td align="center"><a href="?orderBy=daSubmittedDate DESC" class="whiteTitle">Submitted</a></td>
 			    <td align="center"><a href="?orderBy=daClosedDate DESC" class="whiteTitle">Closed</a></td>
 			    <td align="center"><a href="?orderBy=daAuditor_id DESC,name" class="whiteTitle">Auditor</a></td>
 			    <td align="center"><a href="?orderBy=daAssignedDate DESC" class="whiteTitle">Assigned</a></td>
  			  </tr>
<%
int counter = 0;
for(SimpleResultRow row: searchData) {
	counter++;
%>
			  <tr id="auditor_tr<%=row.get("id")%>" class="blueMain" <% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %> >
                <td align="right"><%=counter%></td>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>"><%=row.get("name")%></a></td>
			    <td><%=DateBean.toShowFormat(row.get("lastPayment"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("pqfSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("daSubmittedDate"))%></td>
			    <td><%=DateBean.toShowFormat(row.get("daClosedDate"))%></td>
			    <td>
			    	<form class="auditselect" id="auditor_form<%=row.get("id")%>">
			    		<%=AUDITORS.getAuditorsSelect("daAuditor_id","forms",row.get("daAuditor_id"),"selectAuditor("+row.get("id")+")")%>
			    	</form>
			    </td>
			    <td id="auditor_td<%=row.get("id")%>"><%=DateBean.toShowFormat(row.get("daAssignedDate"))%></td>
		  	  </tr>
<%
} // end foreach loop
%>
		    </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
