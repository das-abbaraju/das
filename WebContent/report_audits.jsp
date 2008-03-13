<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<%	try{
	tBean.setFromDB();
	String assignAuditorID = request.getParameter("assignAuditorID");
	String action = request.getParameter("action");
	
	boolean doAssignments = "Assign Checked Audits".equals(action) && !assignAuditorID.equals(sBean.DEFAULT_AUDITOR_ID) && pBean.isAdmin();
	if(doAssignments){
		java.util.Enumeration e = request.getParameterNames();
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			if(temp.startsWith("assignPQF_")) {
				String cID = temp.substring(10);
				sBean.cBean.setFromDB(cID);
				sBean.cBean.pqfAuditor_id = assignAuditorID;
				sBean.cBean.writeToDB();
			}else if(temp.startsWith("assignDesktop_")){
				String cID = temp.substring(14);
				sBean.cBean.setFromDB(cID);
				sBean.cBean.desktopAuditor_id = assignAuditorID;
				sBean.cBean.desktopAssignedDate = DateBean.getTodaysDate();
				sBean.cBean.writeToDB();
			}else if(temp.startsWith("assignDa_")){
				String cID = temp.substring(9);
				sBean.cBean.setFromDB(cID);
				sBean.cBean.daAuditor_id = assignAuditorID;
				sBean.cBean.daAssignedDate = DateBean.getTodaysDate();
				sBean.cBean.writeToDB();
			}//else if
		}//while
	}//if
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	//else {  //put the secondary sort in the changeSort calls...Otherwise, it turns into "order by field1, name, name, name....."
		//filter += "&orderBy="+sBean.orderBy;
		//sBean.orderBy += ",name";
	//}
	
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);

	String filter = "";
	if (!"0".equals(sBean.selected_pqfAuditorID))
		filter += "&pqfAuditorID="+sBean.selected_pqfAuditorID;
	if (!"0".equals(sBean.selected_desktopAuditorID))
		filter += "&desktopAuditorID="+sBean.selected_desktopAuditorID;
	if (!"0".equals(sBean.selected_daAuditorID))
		filter += "&daAuditorID="+sBean.selected_daAuditorID;
	if (!"0".equals(sBean.selected_officeAuditorID))
		filter += "&officeAuditorID="+sBean.selected_officeAuditorID;
%>
<html>
<head>
<title>PICS - Audit Report</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
  <script language="JavaScript" SRC="js/Search.js"></script>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr>
          <td colspan="5" align="center" class="blueMain">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="70" colspan="2" align="center"><%@ include file="includes/selectReport.jsp"%>
                  <%@ include file="includes/nav/editAuditNav.jsp"%><br>
                  <span class="blueHeader">Audit Report</span><br>
                  <form id="form1" name="form1" method="post" action="report_audits.jsp">
<%	if (pBean.isAdmin())
		out.println(sBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID));
	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center">
                      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                      <%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%>
					  <%=sBean.getSearchIndustrySelect("industry","forms",sBean.selected_industry)%>
                      <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
                    </tr>
                    <tr align="left">
                      <td class="blueMain">PQF:<%=AUDITORS.getAuditorsSelect("pqfAuditorID","forms", sBean.selected_pqfAuditorID)%>
                        Desktop:<%=AUDITORS.getAuditorsSelect("desktopAuditorID","forms", sBean.selected_desktopAuditorID)%>
                        D&A:<%=AUDITORS.getAuditorsSelect("daAuditorID","forms", sBean.selected_daAuditorID)%>
                        Office:<%=AUDITORS.getAuditorsSelect("officeAuditorID","forms", sBean.selected_officeAuditorID)%>
					  </td>
                    </tr>
                  </table>

                  	<input type="hidden" name="showPage" value="1"/>
		          	<input type="hidden" name="startsWith" value=""/>
		          	<input type="hidden" name="orderBy" value="<%= sBean.orderBy %>"/>
                  </form>
	            </td>
            </tr>
            <tr> 
              <td height="30">
<%	if (pBean.isOperator()) { %>
                  <%=sBean.getStartsWithLinks()%>
<%	}//if %>
                </td>
                <td align="center"><%=sBean.getLinksWithDynamicForm()%></td>
              </tr>
            </table>
            <form name="form2" method="post" action="report_audits.jsp?<%=filter%>">
              Assign Checked Audits to: <%=AUDITORS.getAuditorsSelect("assignAuditorID","forms","")%>
              <input name="action" type="submit" class="buttons" value="Assign Checked Audits" onClick="return confirm('Are you sure you want to assign these audits?');"><br>
            <table border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle"> 
                <td colspan=2></td>
                <td align="center" bgcolor="#6699CC" colspan=2>PQF</td>
                <td align="center" bgcolor="#6699CC" colspan=4>Desktop Audit</td>
                <td align="center" bgcolor="#6699CC" colspan=4>D&A Audit</td>
                <td align="center" bgcolor="#336699" colspan=4>Office Audit</td>
<%	if (pBean.isAdmin()){%>
                <td align="center" bgcolor="#993300" colspan=3>Assign</td>
<%	}//if%>
  			  </tr>
              <tr bgcolor="#003366" class="whiteTitle"> 
                <td colspan=2><a href="javascript: changeOrderBy('form1','name');" class="whiteTitle">Contractor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','pqfSubmittedDate DESC, name');" class="whiteTitleSmall">Submit</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','pqfAuditor_id, name');" class="whiteTitleSmall">Auditor</a></td>

                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','desktopAssignedDate DESC, name');" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','desktopAuditor_id, name');" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','desktopSubmittedDate DESC, name');" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','desktopClosedDate DESC, name');" class="whiteTitleSmall">Close</a></td>

                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','daAssignedDate DESC, name');" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','daAuditor_id, name');" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','daSubmittedDate DESC, name');" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','daClosedDate DESC, name');" class="whiteTitleSmall">Close</a></td>

                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','assignedDate DESC, name');" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','auditor_id, name');" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','auditCompletedDate DESC, name');" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#6699CC"><a href="javascript: changeOrderBy('form1','auditClosedDate DESC, name');" class="whiteTitleSmall">Close</a></td>
<%	if (pBean.isAdmin()){%>
                <td align="center" bgcolor="#6699CC" class="whiteTitleSmall">PQF</td>
                <td align="center" bgcolor="#6699CC" class="whiteTitleSmall">Dsktp</td>
                <td align="center" bgcolor="#6699CC" class="whiteTitleSmall">D&A</td>
<%	}//if%>
  			  </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
			  <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
				<td><%=sBean.count-1%></td>
				<td><a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a></td>
				<td align="center"><%=sBean.cBean.pqfSubmittedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.pqfAuditor_id)%></td>
				<td align="center"><%=sBean.cBean.desktopAssignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.desktopAuditor_id)%></td>
				<td align="center"><%=sBean.cBean.desktopSubmittedDate%></td>
				<td align="center"><%=sBean.cBean.desktopClosedDate%></td>

				<td align="center"><%=sBean.cBean.daAssignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.daAuditor_id)%></td>
				<td align="center"><%=sBean.cBean.daSubmittedDate%></td>
				<td align="center"><%=sBean.cBean.daClosedDate%></td>

				<td align="center"><%=sBean.cBean.assignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.auditor_id)%></td>
				<td align="center"><%=sBean.cBean.auditCompletedDate%></td>
				<td align="center"><%=sBean.cBean.auditClosedDate%></td>
<%		if (pBean.isAdmin()){%>
				<td align="center">
				  <input type="checkbox" class="forms" name="assignPQF_<%=sBean.aBean.id%>">
				</td>
				<td align="center">
				  <input type="checkbox" class="forms" name="assignDesktop_<%=sBean.aBean.id%>">
				</td>
				<td align="center">
				  <input type="checkbox" class="forms" name="assignDa_<%=sBean.aBean.id%>">
				</td>
<%		}//if%>
		  	  </tr>
<%	}//while %>
		    </table><br>
		    </form>
			<center><%=sBean.getLinksWithDynamicForm()%></center>
<%	sBean.closeSearch(); %>
		  </td>
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
<%	}finally{
		sBean.closeSearch();
	}//finally
%>