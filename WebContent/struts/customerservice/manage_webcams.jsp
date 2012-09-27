<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Webcams</title>
<link rel="stylesheet" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp" />
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js?v=${version}"></script>
<script type="text/javascript">

function loadForm(id) {
	$('.picsbutton').attr('disabled', 'disabled');
	$('#webcam_edit').load('ManageWebcamsAjax.action', {'webcam.id': id, button: 'load'},
		function (responseText, textStatus, XMLHttpRequest) {
			$('.picsbutton').removeAttr('disabled');
		});
}

function wireClueTips() {
	$("a.contractorQuick").cluetip({
		sticky: true, 
		hoverClass: 'cluetip', 
		clickThrough: true, 
		ajaxCache: true,
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		hoverIntent: {interval: 200},
		arrows: true,
		dropShadow: false,
		width: 400,
		cluetipClass: 'jtip',
		ajaxProcess:      function(data) {
			data = $(data).not('meta, link, title');
			return data;
		}
	});
}

$(function() {
	
	$(window).bind('hashchange', function(e) {
		loadForm($.bbq.getState('webcam'));
	});

	$('tr.clickable').live('click', function(e) {
		e.preventDefault();
		$.bbq.pushState({webcam: $(this).attr('id') });
	});
});
</script>
<style>
td.webcam_list {
	width: 50%;
	vertical-align: top;
	padding-right: 30px;
}

div.webcam_list {
	height: 500px;
	overflow: auto;
	background-color: #F6F6F0;
	border: 2px solid #C3C3C3;
}
</style>
</head>
<body>
<h1>Manage Webcams</h1>

<s:include value="../actionMessages.jsp" />

<table width="100%">
	<tr>
		<td class="webcam_list">
		<div><a href="?button=all" class="picsbutton">All</a> <a href="?button=in" class="picsbutton">In</a> <a
			href="?button=out" class="picsbutton">Out</a> <a href="?" class="picsbutton">Active</a></div>
		<div class="webcam_list">
		<table class="report">
			<thead>
				<tr>
					<td>#</td>
					<td>Active</td>
					<td>Contractor</td>
					<td>Sent</td>
					<td>Received</td>
					<td>Comment</td>
				</tr>
			</thead>
			<s:iterator value="list">
				<tr class="clickable" id="<s:property value="id"/>">
					<td style="font-size-adjust: 120%; font-weight: bold;"><s:property value="id" /></td>
					<td class="center"><s:property value="%{active ? 'Y' : 'N'}" /></td>
					<td><s:property value="contractor.name" /></td>
					<td><s:date name="sentDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
					<td><s:date name="receivedDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
					<td><s:property value="model" /></td>
				</tr>
			</s:iterator>
		</table>
		</div>
		</td>
		<td id="webcam_edit" valign="top"><s:include value="manage_webcams_form.jsp" /></td>
	</tr>
</table>

</body>
</html>