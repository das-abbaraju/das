<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="EmailError.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../reports/reportHeader.jsp"/>
<script type="text/javascript" src="js/jquery/fancybox/jquery.fancybox-1.3.1.pack.js?v=${version}"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/fancybox/jquery.fancybox-1.3.1.css?v=${version}" />
<script type="text/javascript">
$(function() {
	$('.fancybox').fancybox({
		frameWidth:  640,
		frameHeight: 480,
		hideOnContentClick: false
	});
	
	$('#queue_table').delegate('.remove', 'click', function(e) {
		e.preventDefault();
		var id = $(this).closest('tr').attr('id').split('_')[1];
		
		if (confirm(translate('JS.EmailQueueList.confirm.DeleteEmail'))) {
			$.ajax({
				url: 'EmailQueueList!delete.action',
				data: {id: id},
				success: function() {
					$('#tr_'+id).fadeOut();
				}
			});
		}
	});
});
</script>
</head>
<body>
<h1><s:text name="EmailError.title" /></h1>

<s:include value="../reports/filters_email.jsp" />
<br />
<table class="report" id="queue_table">
	<thead>
	<tr>
		<th><s:text name="EmailQueueList.header.Order" /></th>
		<th><s:text name="EmailQueueList.header.Priority" /></th>
		<th><s:text name="EmailQueueList.header.Added" /></th>
		<th><s:text name="global.Status" /></th>
		<th><s:text name="global.Contractor" /></th>
		<s:if test="permissions.admin">
			<th><s:text name="EmailQueueList.header.From" /></th>
		</s:if>
		<th><s:text name="EmailQueueList.header.To" /></th>
		<th><s:text name="EmailQueueList.header.CC" /></th>
		<th><s:text name="EmailQueueList.header.Subject" /></th>
		<th><s:text name="EmailQueueList.header.Preview" /></th>
		<pics:permission perm="EmailQueue" type="Delete">
			<th></th>
		</pics:permission>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr id="tr_<s:property value="get('emailID')"/>">
			<td class="right"><s:property value="#stat.index + 1 " /></td>
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
			<td class="center">
				<a href="EmailQueueList!previewAjax.action?id=<s:property value="get('emailID')"/>"
					class="fancybox iframe preview" title="<s:property value="get('subject')"/>"></a>
			</td>
			<pics:permission perm="EmailQueue" type="Delete">
				<td>
					<a href="#" title="<s:text name="EmailQueueList.title.RemoveFromQueue" />" class="remove"></a>
				</td>
			</pics:permission>
		</tr>
	</s:iterator>
	</tbody>
</table>

</body>
</html>