<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Webcams</title>
<link rel="stylesheet" href="css/reports.css" />
<link rel="stylesheet" href="css/forms.css" />
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript">

function loadForm(id) {
	$('.picsbutton').attr('disabled', 'disabled');
	$('#webcam_edit').load('ManageWebcamsAjax.action', {'webcam.id': id, button: 'load'},
		function (responseText, textStatus, XMLHttpRequest) {
			$('.picsbutton').removeAttr('disabled');
		});
}

</script>
</head>
<body>
<h1>Manage Webcams</h1>

<s:include value="../actionMessages.jsp" />

<div class="left" id="webcam_list">
<table class="report" style="height: 100%; overflow: scroll;">
	<thead>
		<tr>
			<td>#</td>
			<td>Active</td>
			<td>Contractor</td>
			<td>Sent</td>
			<td>Received</td>
		</tr>
	</thead>
	<s:iterator value="list">
		<tr class="clickable" onclick="loadForm(<s:property value="id"/>)">
			<td style="font-size-adjust: 120%; font-weight: bold;"><s:property value="id" /></td>
			<td class="center"><s:property value="%{active ? 'Y' : 'N'}" /></td>
			<td><s:property value="contractor.name" /></td>
			<td><s:date name="sentDate" nice="true" /></td>
			<td><s:date name="receivedDate" nice="true" /></td>
		</tr>
	</s:iterator>
</table>
</div>

<div class="left" id="webcam_edit"><s:include
	value="manage_webcams_form.jsp" /></div>

<br clear="all" />
</body>
</html>