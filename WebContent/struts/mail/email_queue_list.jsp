<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Email Queue List</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script type="text/javascript">

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
<s:if test="emails.size() == 0">
	<div id="info">You have no pending emails waiting to be sent.</div>
</s:if>
<s:else>
		<div id="alert">There are <b><s:property value="emailsInQueue.size()" /></b> emails before yours in the queue. After we send those, we will start sending your email(s).</div>
	<table class="report">
		<thead>
		<tr>
			<td>Order</td>
			<td>Priority</td>
			<td>Added</td>
			<td>Contractor</td>
			<td>From</td>
			<td>To</td>
			<td>Subject</td>
			<pics:permission perm="EmailQueue" type="Delete">
			<td></td>
			</pics:permission>
		</tr>
		</thead>
		<s:iterator value="emails" status="stat">
			<tr id="tr<s:property value="id"/>">
				<td class="right"><s:property value="#stat.index + emailsInQueue.size() + 1 " /></td>
				<td><s:property value="priority" /></td>
				<td><s:date name="creationDate" /></td>
				<td><a href="ContractorView.action?id=<s:property value="contractorAccount.id"/>">
				<s:property value="contractorAccount.name" /></a></td>
				<td><s:property value="fromAddress" /></td>
				<td><s:property value="toAddresses" /></td>
				<td><s:property value="subject" /></td>
				<pics:permission perm="EmailQueue" type="Delete">
					<td><a href="javascript: deleteEmail(<s:property value="id"/>);" title="Remove from queue"><img src="images/cross.png" /></a>
					</td>
				</pics:permission>
			</tr>
		</s:iterator>
	</table>
</s:else>

</body>
</html>