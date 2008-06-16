<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%
permissions.tryPermission(OpPerms.BillingUpgrades);
try {
	String action = request.getParameter("action");
	String action_id = request.getParameter("action_id");
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy || "paymentExpires".equals(sBean.orderBy))
		sBean.orderBy = "DAYOFYEAR(paymentExpires)=0 DESC,paymentExpires,name";
	if ("auditDate".equals(sBean.orderBy))
		sBean.orderBy = "status<>'Inactive' DESC,DAYOFYEAR(auditDate)=0 DESC,DAYOFYEAR(auditDate),name";
	if ("Paid".equals(action)) {
		String amount = request.getParameter("amount");
		cBean.updateLastPayment(action_id, permissions.getUsername(), amount);
	}//if Paid
	if ("Inv".equals(action)) {
		String invoiceAmount = request.getParameter("invoiceAmount");
		cBean.setFromDB(action_id);
		cBean.lastInvoiceDate = DateBean.getTodaysDate();
		cBean.billingAmount = invoiceAmount;
		cBean.addAdminNote(action_id, "("+permissions.getUsername()+")", "Invoiced for $"+cBean.billingAmount+" membership level",cBean.lastInvoiceDate);
		if ("".equals(sBean.cBean.membershipDate)){
			sBean.cBean.membershipDate = DateBean.getTodaysDate();
			sBean.cBean.addNote(action_id, "("+permissions.getUsername()+")", "Membership Date set to "+sBean.cBean.membershipDate,DateBean.getTodaysDateTime());
		}//if
		cBean.writeToDB();
	}//if Inv

	sBean.setIsPaymentReport(true);
 	sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
%>
<html>
<head>
<title>Payments</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Payment Report</h1>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show
Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#"
onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" action="report_payment.jsp" method="post" style="display: none">
                   <table border="0" cellpadding="2" cellspacing="0">
                      <tr>
                        <td>
                          <input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                          <%=SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%>
                       	  <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0">
                          </td></tr>
                          <tr>
                      </tr>
                    </table>
            <input type="hidden" name="actionID" value="0">
			<input type="hidden" name="action" value="">
			<input type="hidden" name="showPage" value="1"/>
			<input type="hidden" name="startsWith" value="<%=sBean.selected_startsWith == null ? "" : sBean.selected_startsWith %>"/>
			<input type="hidden" name="orderBy"  value="<%=sBean.orderBy == null ? "dateCreated DESC" : sBean.orderBy %>"/>
              
<div class="alphapaging">
<%=sBean.getStartsWithLinksWithDynamicForm()%>
</div>
</form>
</div>
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>
<table class="report">
    		<thead><tr>	
                <td colspan="2" width="150"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
                <td align="center"><a href="?changed=0&showPage=1&orderBy=payingFacilities DESC" class="whiteTitle">Pay Fac</a></td>
				<td align="center"><a href="?changed=0&showPage=1&orderBy=membershipDate DESC" class="whiteTitle">Member Since</a></td>
 			    <td align="center"><a href="?changed=0&showPage=1&orderBy=paymentExpires" class="whiteTitle">Expires</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastInvoiceDate DESC" class="whiteTitle"><nobr>Last Inv</nobr></a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=newBillingAmount" class="whiteTitle"><nobr>New Inv</nobr> Level</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=lastPayment DESC" class="whiteTitle">Last Pmt</a></td>
 			    <td colspan=2 align="center"><a href="?changed=0&showPage=1&orderBy=newBillingAmount" class="whiteTitle"><nobr>New Pmt</nobr> Level</a></td>
  			</tr></thead> 
<%	int totalNewPayments = 0;
	while (sBean.isNextRecord()){
		totalNewPayments += Integer.parseInt(sBean.cBean.newBillingAmount);
		sBean.cBean.setFacilitiesFromDB();
%>			  <tr <%=sBean.getBGColor()%>>
		  	  <form name="form2" id="form2" method="post" action="report_payment.jsp?changed=0&showPage=<%=sBean.showPage%>">
                <td class="right"><%=sBean.count-1%></td>
			    <td><a href="accounts_edit_contractor.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>">
			    <%=sBean.getActiveStar()%><%=sBean.aBean.name%></a></td>
			    <td><%=sBean.cBean.payingFacilities%></td>
				<td><%=sBean.cBean.membershipDate%></td>
			    <td><%=sBean.cBean.paymentExpires%></td>
			    <td>$<%=sBean.cBean.billingAmount%></td>
			    <td><%=sBean.cBean.lastInvoiceDate%></td>
			    <td><input type="text" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=invoiceAmount></td>
			    <td><input name="action" type="submit" class="buttons" value="Inv"></td>
			    <td>$<%=sBean.cBean.lastPaymentAmount%></td>
			    <td><%=sBean.cBean.lastPayment%></td>
			    <td class="middle"><input type="text" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=amount></td>
			    <td><input name="action" type="submit" class="buttons" value="Paid">
				<input name="action_id" type="hidden" value="<%=sBean.aBean.id%>">
                <input name="orderBy" type="hidden" value="<%=sBean.orderBy%>"></td>
	          </form>
		  	  </tr>
<%	}//while %>
      <tr bgcolor="#003366" class="whiteTitle">
         <td colspan="11">Total</td>
         <td align="left" colspan=3>$<%=totalNewPayments%></td>
        </tr>
</table>
<div>
<%=sBean.getLinksWithDynamicForm()%>
</div>

<%	sBean.closeSearch(); %>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>