<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Email Queue List</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox.css"/>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.2.1.pack.js"></script>
<script type="text/javascript">
function deleteEmail(id) {
	if (confirm('Are you sure you want to delete this email ?')){
		$.ajax({
			url: 'EmailQueueList.action',
			data: {button: 'delete', id: id},
			success: function() {
				$('#tr'+id).fadeOut();
			}
		});
	}
}

$(function() {
	$('.fancybox').fancybox({
		frameWidth:  640,
		frameHeight: 480,
		hideOnContentClick: false
	});
});
</script>
</head>
<body>
<h1>Email Queue List</h1>
<s:if test="emails.size() == 0">
	<div class="info">You have no pending emails waiting to be sent.</div>
</s:if>
<s:else>
	<div class="alert">There are <b><s:property value="emailsInQueue.size()" /></b> emails before yours in the queue. After we send those, we will start sending your email(s).</div>
	<table class="report" id="queue_table">
		<thead>
		<tr>
			<td>Order</td>
			<td>Priority</td>
			<td>Added</td>
			<td>Contractor</td>
			<td>From</td>
			<td>To</td>
			<td>Subject</td>
			<td></td>
			<pics:permission perm="EmailQueue" type="Delete">
				<td></td>
			</pics:permission>
		</tr>
		</thead>
		<tbody>
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
				<td><a href="EmailQueueListAjax.action?button=preview&id=<s:property value="id"/>" class="fancybox iframe" title="<s:property value="subject"/>">Preview</a></td>
				<pics:permission perm="EmailQueue" type="Delete">
					<td><a href="#" onclick="deleteEmail(<s:property value="id"/>); return false;" title="Remove from queue"><img src="images/cross.png" /></a>
					</td>
				</pics:permission>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</s:else>

</body>
</html>