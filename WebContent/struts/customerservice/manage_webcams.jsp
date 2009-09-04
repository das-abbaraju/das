<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Webcams</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript">

function loadForm(id) {
	$('#webcam_edit').load('ManageWebcamsAjax.action', {'webcam.id': id, button: 'load'});
}

</script>
</head>
<body>
<h1>Manage Webcams</h1>

<s:include value="../actionMessages.jsp"/>

<div class="left" id="webcam_list">
<table class="report">
<thead>
	<tr>
		<td>Make 123</td>
		<td>Model</td>
		<td>Active?</td>
	</tr>
</thead>
	<s:iterator value="list">
		<tr class="clickable" onclick="loadForm(<s:property value="id"/>)">
			<td><s:property value="make" /></td>
			<td><s:property value="model" /></td>
			<td><s:property value="active" /></td>
		</tr>
	</s:iterator>
</table>

</div>

<div class="left" id="webcam_edit">
<s:include value="manage_webcams_form.jsp"/>
</div>

<br clear="all"/>
</body>
</html>