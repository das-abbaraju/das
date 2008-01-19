<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope ="application"/>
<%//@ page language="java"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope ="application"/>

<%	try{
	new Billing().updateAllPayingFacilities(FACILITIES, application);
	
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String message = "";
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "dateCreated DESC";
	if ("Send".equals(action)) {
		sBean.aBean.setFromDB(actionID);
		EmailBean eBean = new com.picsauditing.PICS.EmailBean();
		EmailBean.init(config);
		eBean.sendWelcomeEmail(sBean.aBean, adminName);
		message += "A welcome email was sent to <b>" + sBean.aBean.name + "</b>";
	}//if
	if ("Called".equals(action)) {
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.welcomeCallDate = DateBean.getTodaysDate();
		cBean.addNote(actionID, "("+adminName+")", "Welcome call today:", DateBean.getTodaysDateTime());
		cBean.writeToDB();
		message += "Welcome call recorded";
	}//if
	if ("Assign".equals(action)) {
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.welcomeAuditor_id = request.getParameter("assignedWelcomeAuditorID");
		cBean.addNote(actionID, "("+adminName+")", AUDITORS.getNameFromID(cBean.welcomeAuditor_id)+" assigned to make welcome call", DateBean.getTodaysDateTime());
		cBean.writeToDB();
		message += AUDITORS.getNameFromID(cBean.welcomeAuditor_id)+" has been assigned a welcome call";
	}//if
	if ("Yes".equals(action)) {
		sBean.aBean.setFromDB(actionID);
		sBean.aBean.active = "Y";
		message += "<b>"+sBean.aBean.name+"</b> has been made visible";
		sBean.aBean.writeToDB();
	}//if
	if ("Paid".equals(action)){
		String amount = request.getParameter("amount");
		ContractorBean cBean = new ContractorBean();
		cBean.updateLastPayment(actionID, adminName, amount);
	}//if Paid
	if ("Inv".equals(action)){
		String invoiceAmount = request.getParameter("invoiceAmount");
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.lastInvoiceDate = DateBean.getTodaysDate();
		cBean.billingAmount = invoiceAmount;
		cBean.addAdminNote(actionID, "("+adminName+")", "Invoiced for $"+cBean.billingAmount+" membership level",cBean.lastInvoiceDate);
		if ("".equals(cBean.membershipDate)){
			cBean.membershipDate = DateBean.getTodaysDate();
			cBean.addNote(actionID, "("+adminName+")", "Membership Date set today to "+cBean.membershipDate,DateBean.getTodaysDateTime());
		}//if
		cBean.writeToDB();
	}//if Inv
	String BUTTON_VALUE = "Remove";
	if (BUTTON_VALUE.equals(action)){
		sBean.aBean.setFromDB(actionID);
		sBean.cBean.setFromDB(actionID);
		//sets to temporary account date until actual first login. This accoutn date will not be displayed BJ 1-15-05
		sBean.cBean.accountDate = ContractorBean.REMOVE_FROM_REPORT;
		message += "<b>"+sBean.aBean.name+"</b> has been removed";
		sBean.cBean.writeToDB();
	}//if
	sBean.setIsActivationReport();
	sBean.doSearch(request, SearchBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
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
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="MM_preloadImages('images/button_search_o.gif')">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" class="buttons"> 
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
          <td colspan="3" align="center">
            <%@ include file="includes/selectReport.jsp"%>
            <span class="blueHeader">Activation Report </span><br>
            <form name="form1" method="post" action="report_activation.jsp">
              <table border="0" cellpadding="2" cellspacing="0">
                <tr align="center">
                  <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                    <%=SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%>
                    <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
                  </td>
                </tr>
              </table>
            </form>
            <span class="blueMain"><%=sBean.getStartsWithLinks()%></span>
            <span class="redMain"><br><%=message%></span>
	        <table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
	          <tr>
		        <td><span class="blueHeader">Contractors that haven't ever logged in</span></td>
		        <td align="right"><span class="redMain"><%=sBean.getLinks()%></span></td>
	          </tr>
	        </table>
	        <table width="657" border="0" cellpadding="1" cellspacing="1">
			  <tr bgcolor="#003366" class="whiteTitle">
				<td colspan="2" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
				<td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=dateCreated DESC" class="whiteTitle">Created</a></td>
				<td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=membershipDate DESC" class="whiteTitle">Member</a></td>
				<td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=requestedByID DESC" class="whiteTitle">Requester</a></td>
 			    <td align="center"><a href="?changed=0&showPage=1&orderBy=payingFacilities" class="whiteTitle">Paying Fac.</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastInvoiceDate DESC" class="whiteTitle"><nobr>Last Invoice</nobr></a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=newBillingAmount DESC" class="whiteTitle"><nobr>New Invoice</nobr></a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastPayment DESC" class="whiteTitle">Last Payment</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=billingCycle" class="whiteTitle">New Payment</a></td>
				<td colspan=4 align="center" bgcolor="#6699CC">Welcome Call</td>
				<td colspan=2 align="center" bgcolor="#6699CC">Welcome Email</td>
				<td align="center" bgcolor="#336699">Visible?</td>
				<td width="25" align="center" bgcolor="#336699">Y/N</td>
				<td>&nbsp;</td>
			  </tr>
<%	while (sBean.isNextRecord()){%>
			  <tr <%=sBean.getBGColor()%> class="blueMain"> 
			  <form name="form2" method="post" action="report_activation.jsp?changed=0&showPage=<%=sBean.showPage%>">
				<td align="right"><%=sBean.count-1%></td>
		        <td class="<%=sBean.cBean.getTextColor()%>"><%=sBean.getActiveStar()%>
				  <a target="_blank" href="accounts_edit_contractor.jsp?id=<%=sBean.aBean.id%>" class="<%=sBean.getTextColor()%>">
				  <%=sBean.aBean.name%></a>
				</td>
				<td align="center"><%=sBean.aBean.dateCreated%></td>
				<td align="center"><%=sBean.cBean.membershipDate%></td>
				<td align="center"><%=FACILITIES.getNameFromID(sBean.cBean.requestedByID)%></td>
			    <td align="center"><%=sBean.cBean.payingFacilities%></td>
			    <td align="center"><%=sBean.cBean.billingAmount%></td>
			    <td align="center"><%=sBean.cBean.lastInvoiceDate%></td>
			    <td><input type="text" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=invoiceAmount></td>
			    <td><input name="action" type="submit" class="buttons" value="Inv"></td>
			    <td align="center">$<%=sBean.cBean.lastPaymentAmount%></td>
			    <td><%=sBean.cBean.lastPayment%></td>
			    <td valign="middle"><input type="text" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=amount></td>
			    <td><input name="action" type="submit" class="buttons" value="Paid"></td>
				<td align="center"><%=AUDITORS.getAuditorsSelect("assignedWelcomeAuditorID","forms",sBean.cBean.welcomeAuditor_id)%></td>
				<td align="center"><input name="action" type="submit" class="buttons" value="Assign"></td>
				<td align="center"><%=sBean.cBean.welcomeCallDate%></td>
				<td align="center"><input name="action" type="submit" class="buttons" value="Called"></td>
				<td align="center"><%=sBean.cBean.welcomeEmailDate%></td>
				<td align="center"><input name="action" type="submit" class="buttons" value="Send"></td>
				<td align="center"><input name="action" type="submit" class="buttons" value="Yes"> </td>
				<td align="center"><%=sBean.aBean.active%></td>             
				<td><input name="action" type="submit" class="buttons" value="<%=BUTTON_VALUE%>"></td>
			    <input name="actionID" type="hidden" value="<%=sBean.aBean.id%>">
                <input name="orderBy" type="hidden" value="<%=sBean.orderBy%>">
			  </form>
			  </tr>
<%	}//while %>
			</table>
		    <br><center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
	  <br><center><%@ include file="utilities/contractor_key.jsp"%><br><br></center><br><br><br>
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