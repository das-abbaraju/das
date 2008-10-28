<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Email Queue List</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript" src="js/prototype.js"></script>
<script language="JavaScript">

function deleteEmail(id) {
	var deleteMe = confirm('Are you sure you want to delete this email ?');
	if (!deleteMe)
		return;
	var pars = "button=delete&id=" + id;

	var myAjax = new Ajax.Updater('','EmailQueueList.action',
		{
			method: 'post',
			parameters: pars,
			onSuccess: function(transport) {
				$('tr'+id).hide();
			}
		});
}
</script>
</head>
<body>
<h1>Email Queue List</h1>
<s:if test="emailsInQueue.size() > 0">
	<div id="info">Your emails are <b><s:property value="emailsInQueue.size()" /></b> in queue to be sent out.</div>
	<table class="report">
		<thead>
		<tr>
			<td></td>
			<td>Priority</td>
			<td>Added</td>
			<td>From</td>
			<td>To</td>
			<td>Subject</td>
			<td></td>
		</tr>
		</thead>
		<s:iterator value="emails" status="stat">
			<tr id="tr<s:property value="id"/>">
				<td class="right"><s:property value="#stat.index + 1" /></td>
				<td><s:property value="priority" /></td>
				<td><s:date name="creationDate" nice="true" /></td>
				<td><s:property value="fromAddress" /></td>
				<td><s:property value="toAddresses" /></td>
				<td><s:property value="subject" /></td>
				<td><a href="javascript: deleteEmail(<s:property value="id"/>);" title="Remove from queue"><img src="images/cross.png" /></a>
				</td>
			</tr>
		</s:iterator>
	</table>
	
	
</s:if>
<s:else>
	<div id="info">There are no pending emails to be sent</div>
</s:else>
</body>
</html>