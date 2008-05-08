<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@page import="com.picsauditing.mail.*"%>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%	
if (!permissions.isAdmin()) throw new com.picsauditing.access.NoRightsException("Admin");
try{
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String message = "";
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "dateCreated DESC";
	if ("Send".equals(action)) {
		EmailContractorBean emailer = new EmailContractorBean();
		/*
		if (email_welcome_attachfile)
			String fileName = "attachments/welcome.doc";
		*/
		emailer.sendMessage(EmailTemplates.welcome, actionID, permissions);
		message += "A welcome email was sent to "+emailer.getSentTo();
		emailer.getContractorBean().welcomeEmailDate = DateBean.getTodaysDate();
		emailer.getContractorBean().writeToDB();
		return;
	}
	if ("Called".equals(action)) {
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.welcomeCallDate = DateBean.getTodaysDate();
		cBean.addNote(actionID, "("+permissions.getUsername()+")", "Welcome call today:", DateBean.getTodaysDateTime());
		cBean.writeToDB();
		message += "Welcome call recorded";
		return;
	}//if
	if ("Assign".equals(action)) {
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.welcomeAuditor_id = request.getParameter("assignedWelcomeAuditorID");
		cBean.addNote(actionID, "("+permissions.getUsername()+")", AUDITORS.getNameFromID(cBean.welcomeAuditor_id)+" assigned to make welcome call", DateBean.getTodaysDateTime());
		cBean.writeToDB();
		message += AUDITORS.getNameFromID(cBean.welcomeAuditor_id)+" has been assigned a welcome call";
		return;
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
		cBean.updateLastPayment(actionID, permissions.getUsername(), amount);
		return;
	}//if Paid
	if ("Inv".equals(action)){
		String invoiceAmount = request.getParameter("invoiceAmount");
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		cBean.lastInvoiceDate = DateBean.getTodaysDate();
		cBean.billingAmount = invoiceAmount;
		cBean.addAdminNote(actionID, "("+permissions.getUsername()+")", "Invoiced for $"+cBean.billingAmount+" membership level",cBean.lastInvoiceDate);
		if ("".equals(cBean.membershipDate)){
			cBean.membershipDate = DateBean.getTodaysDate();
			cBean.addNote(actionID, "("+permissions.getUsername()+")", "Membership Date set today to "+cBean.membershipDate,DateBean.getTodaysDateTime());
		}//if
		cBean.writeToDB();
		return;
	}//if Inv
	String BUTTON_VALUE = "Remove";
	if (BUTTON_VALUE.equals(action)){
		sBean.aBean.setFromDB(actionID);
		sBean.cBean.setFromDB(actionID);
		//sets to temporary account date until actual first login. This accoutn date will not be displayed BJ 1-15-05
		sBean.cBean.accountDate = ContractorBean.REMOVE_FROM_REPORT;
		message += "<b>"+sBean.aBean.name+"</b> has been removed";
		sBean.cBean.writeToDB();
		return;
	}//if
	
	// Don't run this here anymore
	//new Billing().updateAllPayingFacilities(application);
	
	sBean.setIsActivationReport();
	
	sBean.doSearch(request, SearchBean.ACTIVE_AND_NOT, 50, pBean, pBean.userID);
%>
<%@page import="com.picsauditing.mail.EmailContractorBean"%>
<html>
<head>
<title>Activation</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script language="JavaScript">
  	function sendInvoice( conId, amountField )
  	{
		var elm = document.getElementById( amountField );
		  	
		var pars = 'actionID='+conId+'&invoiceAmount='+ elm.value+'&action=Inv';
		var divName = 'inv_history_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$(divName).innerHTML = elm.value + ' on <%= DateBean.getTodaysDate() %>'; 
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});

		return false;
  	}
  	function newPayment( conId, amountField )
  	{
  		var elm = document.getElementById( amountField );
  		
		var pars = 'actionID='+conId+'&amount='+elm.value+'&action=Paid';
		var divName = 'pay_history_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp',
			{
				method: 'post', 
				parameters: pars, 
				onComplete: function() {
					$(divName).innerHTML = elm.value + ' on <%= DateBean.getTodaysDate() %>';			
					new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});		
		}});
		return false;
  	}
  	function selectAuditor( conId, valueField )
  	{
  		
		var pars = 'actionID='+conId+'&action=Assign&assignedWelcomeAuditorID='+valueField.value;

		var divName = 'auditor_assign_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp',
			{
				method: 'post', 
				parameters: pars, 
				onComplete: function() {			
					new Effect.Highlight($(divName), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});		
		}});
		return false;
  	}
  	function logWelcomeCall( conId )
  	{
		var pars = 'actionID='+conId+'&action=Called';
		var divName = 'welcome_call_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$(divName).innerHTML = '<%= DateBean.getTodaysDate() %>'; 
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});

		return false;
  	}
  	function logWelcomeEmail( conId )
  	{
		var pars = 'actionID='+conId+'&action=Send';
		var divName = 'welcome_email_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp', 
				{
					method: 'post', 
					parameters: pars
				});

		$(divName).innerHTML = '<%= DateBean.getTodaysDate() %>'; 
		new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});

		return false;
  	}
  	function makeVisible( conId )
  	{
		var pars = 'actionID='+conId+'&action=Yes';
		var divName = 'visible_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$(divName).innerHTML = 'Y'; 
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});
		return false;
  	}
  	function removeCon( conId )
  	{
		var pars = 'actionID='+conId+'&action=Remove';
		var divName = 'remove_'+conId;
		var myAjax = new Ajax.Request('report_activation.jsp', 
				{
					method: 'post', 
					parameters: pars,
					onSuccess: function(transport) {
						$(divName).innerHTML = ''; 
						new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
					}
				});
		return false;
  	}
</script>
</head>
<body>
<h1>Activation Report
<span class="sub">Contractors that haven't ever logged in</span></h1>
<div id="search">
<div id="showSearch"><a href="#" onclick="showSearch()">Show Filter Options</a></div>
<div id="hideSearch" style="display: none"><a href="#" onclick="hideSearch()">Hide Filter Options</a></div>
<form id="form1" name="form1" method="post" style="display: none">
              <table border="0" cellpadding="2" cellspacing="0">
                <tr align="center">
                  <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                    <%=SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID)%>
                    <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onClick="runSearch( 'form1')" onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
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

<span class="redMain"><br><%=message%></span>
<table class="report">
	<thead>
	<tr>
		<td></td>
		<td colspan="2" bgcolor="#003366"><a href="javascript: changeOrderBy('form1','name');" class="whiteTitle">Contractor</a></td>
		<td  bgcolor="#003366"><a href="javascript: changeOrderBy('form1','dateCreated DESC');" class="whiteTitle">Created</a></td>
		<td  bgcolor="#003366"><a href="javascript: changeOrderBy('form1','membershipDate DESC');" class="whiteTitle">Member</a></td>
		<td  bgcolor="#003366"><a href="javascript: changeOrderBy('form1','requestedByID DESC');" class="whiteTitle">Requester</a></td>
		<td ><a href="javascript: changeOrderBy('form1','payingFacilities');" class="whiteTitle">Paying Fac.</a></td>
		<td colspan=2 ><a href="javascript: changeOrderBy('form1','lastInvoiceDate DESC');" class="whiteTitle"><nobr>Last Invoice</nobr></a></td>
		<td colspan=2 ><a href="javascript: changeOrderBy('form1','newBillingAmount DESC');" class="whiteTitle"><nobr>New Invoice</nobr></a></td>
		<td colspan=2 ><a href="javascript: changeOrderBy('form1','lastPayment DESC');" class="whiteTitle">Last Payment</a></td>
		<td colspan=2 ><a href="javascript: changeOrderBy('form1','billingCycle');" class="whiteTitle">New Payment</a></td>
		<td colspan=4  bgcolor="#6699CC">Welcome Call</td>
		<td colspan=2  bgcolor="#6699CC">Welcome Email</td>
		<td  bgcolor="#336699">Visible?</td>
		<td width="25"  bgcolor="#336699">Y/N</td>
		<td>&nbsp;</td>
	</tr>
	</thead>
<%	while (sBean.isNextRecord()){%>
			  <tr <%=sBean.getBGColor()%>> 
			  <form name="form2" method="post" action="report_activation.jsp?changed=0&showPage=<%=sBean.showPage%>">
				<td class="right"><%=sBean.count-1%></td>
		        <td><%=sBean.getActiveStar()%>
				  <a target="_blank" href="accounts_edit_contractor.jsp?id=<%=sBean.aBean.id%>" class="<%=sBean.getTextColor()%>">
				  <%=sBean.aBean.name%></a>
				</td>
				<td class="center"><%=sBean.aBean.dateCreated%></td>
				<td class="center"><%=sBean.cBean.membershipDate%></td>
				<td class="center"><%=FACILITIES.getNameFromID(sBean.cBean.requestedByID)%></td>
			    <td class="center"><%=sBean.cBean.payingFacilities%></td>
			    <td id="inv_history_<%= sBean.aBean.id %>" align="center" colspan="2"><%=sBean.cBean.billingAmount%> on <%=sBean.cBean.lastInvoiceDate%></td>
			    <td><input type="text" id="inv_amount_<%= sBean.cBean.id %>" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=invoiceAmount></td>
			    <td><input type="submit" onclick="javascript: return sendInvoice( '<%= sBean.aBean.id %>','inv_amount_<%= sBean.cBean.id %>');" class="buttons" value="Inv"/></td>
			    <td id="pay_history_<%= sBean.aBean.id %>" align="center" colspan="2">$<%=sBean.cBean.lastPaymentAmount%> on <%=sBean.cBean.lastPayment%></td>
			    <td valign="middle"><input id="pay_amount_<%= sBean.aBean.id %>" type="text" class=forms value="<%=sBean.cBean.newBillingAmount%>" size=3 name=amount></td>
			    <td><input name="action" type="submit" onClick="javascript: return newPayment( '<%= sBean.aBean.id %>','pay_amount_<%= sBean.cBean.id %>');" class="buttons" value="Paid"></td>
				<td id="auditor_assign_<%=sBean.aBean.id %>" colspan="2" align="center"><%=AUDITORS.getAuditorsSelect("assignedWelcomeAuditorID","forms",sBean.cBean.welcomeAuditor_id,   "selectAuditor('" + sBean.aBean.id + "',this);" ) %></td>
				<td id="welcome_call_<%= sBean.aBean.id %>" align="center"><%=sBean.cBean.welcomeCallDate%></td>
				<td align="center"><input name="action" type="submit" onclick="javascript: return logWelcomeCall( '<%= sBean.aBean.id %>' );" class="buttons" value="Called"></td>
				<td id="welcome_email_<%= sBean.aBean.id %>" align="center"><%=sBean.cBean.welcomeEmailDate%></td>
				<td align="center"><input name="action" type="submit" onclick="javascript: return logWelcomeEmail( '<%= sBean.aBean.id %>' );" class="buttons" value="Send"></td>
				<td align="center"><input name="action" type="submit" onclick="javascript: return makeVisible( '<%= sBean.aBean.id %>' );" class="buttons" value="Yes"> </td>
				<td id="visible_<%= sBean.aBean.id %>" align="center"><%=sBean.aBean.active%></td>             
				<td id="remove_<%= sBean.aBean.id %>" ><input name="action" type="submit" onclick="javascript: return removeCon( '<%= sBean.aBean.id %>' );" class="buttons" value="<%=BUTTON_VALUE%>"></td>
			   </form></tr>
<%	} %>
</table>
<div><center>
<%=sBean.getLinksWithDynamicForm()%>
</center></div>
<%	}finally{
		sBean.closeSearch();
	}
%>
</body>
</html>