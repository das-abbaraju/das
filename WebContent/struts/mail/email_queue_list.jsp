<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Email Queue List</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../reports/reportHeader.jsp"/>
<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css" />
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
<div class="alert">There are <b><s:property value="emailsInQueue.size()" /></b> emails before yours in the queue. After we send those, we will start sending your email(s).</div>

<s:include value="../reports/filters_email.jsp" />
<br />
<table class="report" id="queue_table">
	<thead>
	<tr>
		<th>Order</th>
		<th>Priority</th>
		<th>Added</th>
		<th>Status</th>
		<th>Contractor</th>
		<s:if test="permissions.admin">
			<th>From</th>
		</s:if>
		<th>To</th>
		<th>CC</th>
		<th>Subject</th>
		<th></th>
		<pics:permission perm="EmailQueue" type="Delete">
			<th></th>
		</pics:permission>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr id="tr<s:property value="get('emailID')"/>">
			<td class="right"><s:property value="#stat.index + emailsInQueue.size() + 1 " /></td>
			<td><s:property value="get('priority')" /></td>
			<td><s:property value="get('created')" /></td>
			<td><s:property value="get('status')" /></td>
			<td>
				<a href="ContractorView.action?id=<s:property value="get('conID')"/>">
				<s:property value="get('name')" /></a>
			</td>
			<s:if test="permissions.admin">
				<td><s:property value="get('fromAddress')" /></td>
			</s:if>
			<td><s:property value="get('toAddresses')" /></td>
			<td><s:property value="get('ccAddresses')" /></td>
			<td><s:property value="get('subject')" /></td>
			<td>
				<a href="EmailQueueListAjax.action?button=preview&id=<s:property value="get('emailID')"/>"
					class="fancybox iframe" title="<s:property value="get('subject')"/>">Preview</a>
			</td>
			<pics:permission perm="EmailQueue" type="Delete">
				<td>
					<a href="#" onclick="deleteEmail(<s:property value="get('emailID')"/>); return false;"
						title="Remove from queue" class="remove"></a>
				</td>
			</pics:permission>
		</tr>
	</s:iterator>
	</tbody>
</table>

</body>
</html>