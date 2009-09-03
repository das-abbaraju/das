<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Webcams</title>
<link rel="stylesheet" href="css/reports.css"/>
<link rel="stylesheet" href="css/forms.css"/>
</head>
<body>
<h1>Manage Webcams</h1>

<div class="left">
<table class="report">
<thead>
	<tr>
		<td>Make</td>
		<td>Model</td>
		<td>Active?</td>
	</tr>
</thead>
	<s:iterator value="list">
		<tr>
			<td><s:property value="make" /></td>
			<td><s:property value="model" /></td>
			<td><s:property value="active" /></td>
		</tr>
	</s:iterator>
</table>

</div>

<div class="left" style="width:40%">
<s:form>
	<fieldset class="form">
		<legend><span>Webcam</span></legend>
		<ol>
			<li><label>ID:</label><s:textfield name="webcam.id" disabled="true"/></li>
			<li><label>Make:</label><s:textfield name="webcam.make"/></li>
			<li><label>Model:</label><s:textfield name="webcam.model"/></li>
			<li><label>Active:</label><s:checkbox name="webcam.active"/></li>
			<li><label>Serial Number:</label><s:textfield name="webcam.serialNumber"/></li>
			<li><label>Replacement Cost:</label><s:textfield name="webcam.replacementCost"/></li>
		</ol>
	</fieldset>
</s:form>
</div>

<br clear="all"/>
</body>
</html>