<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%@ include file="utilities/contractor_list_auditor_secure.jsp" %>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>

<%	try{
	tBean.setFromDB();
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.uBean.id);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
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
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_approvedContractors.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
		  <td colspan="5" align="center">
            <table border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="70" colspan="2" align="center"> 
	            <form name="form1" method="post" action="contractor_list_auditor.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center"> 
                      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
                      <td><%=com.picsauditing.PICS.Inputs.inputSelectFirst("auditType","forms",sBean.selected_auditType,com.picsauditing.PICS.pqf.Constants.AUDIT_TYPE_ARRAY,com.picsauditing.PICS.pqf.Constants.DEFAULT_AUDIT)%></td>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"></td>
                      <td>&nbsp;&nbsp;<a href="verify_insurance.jsp?id=<%=pBean.userID%>" class="blueMain">Verify Insurance</a></td>
                    </tr>
                  </table>
                </form>
                </td>
              </tr>
			  <tr>
                <td></td><td align="center" class="blueMain">You have <strong><%=sBean.getNumResults()%></strong> contractors to audit | &nbsp;<%=sBean.getLinks()%>
			    </td>
              </tr>
			  <tr>
			    <td colspan="2" align="center"><a href="audit_calendar.jsp?format=popup&id=<%=pBean.userID%>" target="_blank" class="blueMain">Audit Calendar</a></td>
			  </tr>
		      <tr>
			    <td colspan="2">&nbsp;</td>
			  </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle">
			    <td colspan=2 bgcolor="#003366"></td>
			    <td align="center" bgcolor="#336699">PQF</td>
                <td align="center" bgcolor="#6699CC" colspan=4>Desktop Audit</td>
                <td align="center" bgcolor="#336699" colspan=4>Office Audit</td>
                <td align="center" bgcolor="#336699" colspan=4>D&A Audit</td>
  			  </tr>
              <tr>
			    <td colspan=2 bgcolor="#003366"><a href="?orderBy=name" class="whiteTitle">Contractor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=pqfSubmittedDate DESC" class="whiteTitleSmall">Submit</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopAssignedDate DESC" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopAuditor_id DESC" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopSubmittedDate DESC" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopClosedDate DESC" class="whiteTitleSmall">Close</a></td>

                <td align="center" bgcolor="#336699"><a href="?changed=0&showPage=1&orderBy=assignedDate DESC" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#336699"><a href="?changed=0&showPage=1&orderBy=auditor_id DESC" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#336699"><a href="?changed=0&showPage=1&orderBy=auditCompletedDate DESC" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#336699"><a href="?changed=0&showPage=1&orderBy=auditClosedDate DESC" class="whiteTitleSmall">Close</a></td>

                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daAssignedDate DESC" class="whiteTitleSmall">Assign</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daAuditor_id DESC" class="whiteTitleSmall">Auditor</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daSubmittedDate DESC" class="whiteTitleSmall">Perform</a></td>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daClosedDate DESC" class="whiteTitleSmall">Close</a></td>
              </tr>
<%	while (sBean.isNextRecord()) {
		String auditStatus = "";
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(sBean.selected_auditType))
			auditStatus = sBean.cBean.getDesktopStatus();
		else if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(sBean.selected_auditType))
			auditStatus = sBean.cBean.auditStatus;
%>
			  <tr <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
<%		if (auditStatus.equals(sBean.cBean.AUDIT_STATUS_CLOSED)) { %>
				<td><%=sBean.count-1%></td>
				<td><span class="cantSee"><%=sBean.aBean.name%></span></td>
<%		} else { %>
				<td><%=sBean.count-1%></td>
				<td><a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>"><%=sBean.aBean.name%></a></td>
<%		} //else %>
				<td align="center"><%=sBean.cBean.pqfSubmittedDate%></td>
				<td align="center"><%=sBean.cBean.desktopAssignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.desktopAuditor_id)%></td>
				<td align="center"><%=sBean.cBean.desktopSubmittedDate%></td>
				<td align="center"><%=sBean.cBean.desktopClosedDate%></td>

				<td align="center"><%=sBean.cBean.assignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.auditor_id)%></td>
				<td align="center"><%=sBean.cBean.auditCompletedDate%></td>
				<td align="center"><%=sBean.cBean.auditClosedDate%></td>

				<td align="center"><%=sBean.cBean.daAssignedDate%></td>
				<td align="center"><%=AUDITORS.getNameFromID(sBean.cBean.daAuditor_id)%></td>
				<td align="center"><%=sBean.cBean.daSubmittedDate%></td>
				<td align="center"><%=sBean.cBean.daClosedDate%></td>
		  	  </tr>
<%	} // while %>
		    </table><br>
		  <center><span class="redMain"><%=sBean.getLinks()%></span></center>
<%  sBean.closeSearch(); %>
		  </td>
        </tr>
      </table>
        <br><center><%@ include file="utilities/contractor_key.jsp"%><br><br>
        <span class="blueMain"> You must have <a href="http://www.adobe.com/products/acrobat/readstep2.html" target="_blank">Adobe
         Reader 6.0</a> or later to view the documents above.</span> 
      </center>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<map name="Map">
  <area shape="rect" coords="73,4,142,70" href="logout.jsp">
</map>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>