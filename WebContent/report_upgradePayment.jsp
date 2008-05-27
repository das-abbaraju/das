<%@page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<%
	permissions.tryPermission(OpPerms.BillingUpgrades);
	String action = request.getParameter("action");
	String action_id = request.getParameter("action_id");
	sBean.orderBy = request.getParameter("orderBy");
	if (null == sBean.orderBy || "paymentExpires".equals(sBean.orderBy))
		sBean.orderBy = "DAYOFYEAR(paymentExpires)=0 DESC,paymentExpires,name";
	if ("auditDate".equals(sBean.orderBy))
		sBean.orderBy = "status<>'Inactive' DESC,DAYOFYEAR(auditDate)=0 DESC,DAYOFYEAR(auditDate),name";
	if ("Paid".equals(action)) {
		permissions.tryPermission(OpPerms.BillingUpgrades, OpType.Edit);
		String amount = request.getParameter("amount");
		cBean.upgradePayment(action_id, permissions.getUsername(), amount);
	}
	if ("Inv".equals(action)) {
		permissions.tryPermission(OpPerms.BillingUpgrades, OpType.Edit);
		String invoiceAmount = request.getParameter("invoiceAmount");
		cBean.setFromDB(action_id);
		cBean.lastInvoiceDate = DateBean.getTodaysDate();
		cBean.billingAmount = invoiceAmount;
		cBean.addAdminNote(action_id, "(" + permissions.getName() + ")", "Invoiced for $" + cBean.billingAmount+ " membership level", cBean.lastInvoiceDate);
		if ("".equals(cBean.membershipDate)) {
			cBean.membershipDate = DateBean.getTodaysDate();
			cBean.addNote(action_id, "(" + permissions.getName() + ")", "Membership Date set today to "+ cBean.membershipDate, DateBean.getTodaysDateTime());
		}
		cBean.writeToDB();
	}
	if ("Recalculate Upgrade Amounts".equals(action)) {
		permissions.tryPermission(OpPerms.BillingUpgrades, OpType.Edit);
		new Billing().updateAllPayingFacilities();
	}

	sBean.isUpgradePaymentReport = true;
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
%>
<html>
<head>
<title>Upgrade Payments</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script language="JavaScript">
  	function sendInvoice( conId, amountField )
  	{
		var elm = document.getElementById( amountField );
		  	
		var pars = 'action_id='+conId+'&invoiceAmount='+ elm.value+'&action=Inv';
		var divName = 'inv_history_'+conId;
		var myAjax = new Ajax.Request('report_upgradePayment.jsp', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$(divName).innerHTML = "$"+elm.value; 
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});

		return false;
  	}

	function newPayment( conId, amountField )
  	{
  		var elm = document.getElementById( amountField );
  		
		var pars = 'action_id='+conId+'&amount='+elm.value+'&action=Paid';
		var divName = 'pay_history_'+conId;
		var myAjax = new Ajax.Request('report_upgradePayment.jsp',
			{
				method: 'post', 
				parameters: pars, 
				onComplete: function() {
					$(divName).innerHTML = "$"+elm.value;			
					new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});		
		}});
		return false;
  	}


</script>  	
</head>
<body>
<h1>Upgrade Payments</h1>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show
Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#"
onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" method="post" action="report_upgradePayment.jsp" style="display: none">
		<table border="0" cellpadding="2" cellspacing="0">
			<tr>
				<td><input name="name" type="text" class="forms"
					value="<%=sBean.selected_name%>" size="20"
					onFocus="clearText(this)"> <%=SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain",
							sBean.selected_generalContractorID)%>
					<input name="imageField" type="image" src="images/button_search.gif"
					width="70" height="23" border="0"></td></tr>
				<tr><td>			
				<%=Inputs.inputSelect("invoicedStatus", "blueMain", sBean.selected_invoicedStatus,
							SearchBean.INVOICED_SEARCH_ARRAY)%>
				</td>
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

<form name="form2" id="form2" method="post" action="report_upgradePayment.jsp?changed=0&showPage=<%=sBean.showPage%>">
<input class="buttons" type="submit" name="action" value="Recalculate Upgrade Amounts">
<table class="report">
	<thead><tr>
		<td colspan="2" width="150"><a
			href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
		<td align="center"><a
			href="?changed=0&showPage=1&orderBy=payingFacilities DESC"
			class="whiteTitle">Pay Fac</a></td>
		<td align="center"><a
			href="?changed=0&showPage=1&orderBy=membershipDate"
			class="whiteTitle">Member Since</a></td>
		<td align="center"><a
			href="?changed=0&showPage=1&orderBy=paymentExpires"
			class="whiteTitle">Expires</a></td>
		<td colspan=2 align="center"><a
			href="?changed=0&showPage=1&orderBy=lastInvoiceDate DESC"
			class="whiteTitle"><nobr>Last Inv</nobr></a></td>
		<td colspan=2 align="center"><a
			href="?changed=0&showPage=1&orderBy=newBillingAmount DESC"
			class="whiteTitle"><nobr>New Inv</nobr> Level</a></td>
		<td align="center">Inv Amt</td>
		<td colspan=2 align="center"><a
			href="?changed=0&showPage=1&orderBy=lastPayment" class="whiteTitle">Last
		Pmt</a></td>
		<td colspan=2 align="center"><a
			href="?changed=0&showPage=1&orderBy=newBillingAmount"
			class="whiteTitle">New Pmt Level</a></td>
	</tr></thead>
	<%
		int totalNewInvoices = 0;
		int totalToCollect = 0;
		while (sBean.isNextRecord()) {
			int newInvoice = Integer.parseInt(sBean.cBean.newBillingAmount)
					- Integer.parseInt(sBean.cBean.billingAmount);
			int toCollect = Integer.parseInt(sBean.cBean.newBillingAmount)
					- Integer.parseInt(sBean.cBean.lastPaymentAmount);
			totalToCollect += toCollect;
			totalNewInvoices += newInvoice;
	%>
	<tr <%=sBean.getBGColor()%>>
		
		<td align="right"><%=sBean.count - 1%></td>
		<td><a href="accounts_edit_contractor.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>"> <%=sBean.getActiveStar()%><%=sBean.aBean.name%></a></td>
		<td><%=sBean.cBean.payingFacilities%></td>
		<td><%=sBean.cBean.membershipDate%></td>
		<td><%=sBean.cBean.paymentExpires%></td>
		<td id="inv_history_<%= sBean.aBean.id %>">$<%=sBean.cBean.billingAmount%></td>
		<td><%=sBean.cBean.lastInvoiceDate%></td>
		<td><input type="text" id="inv_amount_<%= sBean.cBean.id %>" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=invoiceAmount></td>
		<td><input type="submit" onclick="javascript: return sendInvoice( '<%= sBean.aBean.id %>','inv_amount_<%= sBean.cBean.id %>');" class="buttons" value="Inv"/></td>
		<td><nobr>$<%=newInvoice%></nobr></td>
		<td id="pay_history_<%= sBean.aBean.id %>">$<%=sBean.cBean.lastPaymentAmount%></td>
		<td><%=sBean.cBean.lastPayment%></td>
		<td><nobr><input type="text" id="pay_amount_<%= sBean.aBean.id %>" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=amount>/<%=sBean.cBean.billingCycle%>yr</nobr></td>
		<td valign="middle"><input name="action" type="submit" onClick="javascript: return newPayment( '<%= sBean.aBean.id %>','pay_amount_<%= sBean.cBean.id %>');" class="buttons" value="Paid"></td>
  </tr>
	<%
		}//while
	%>
<tr bgcolor="#003366" class="whiteTitle">
		<td colspan="4">Total</td>
		<td align="right" colspan="11">To Invoice: $<%=totalNewInvoices%>
		| To Collect: $<%=totalToCollect%></td>
</tr>	
</table>
<input name="invoicedStatus" type="hidden" value="<%=sBean.selected_invoicedStatus%>">
<input name="action_id" type="hidden" value="<%=sBean.aBean.id%>">
<input name="orderBy" type="hidden" value="<%=sBean.orderBy%>">
</form>
<br>
<div><center>
<%=sBean.getLinksWithDynamicForm()%>
</center></div>
<%
	sBean.closeSearch();
%>
</body>
</html>