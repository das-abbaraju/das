<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Webcams</title>
</head>
<body>

<div>
<table>
	<s:iterator value="list">
		<tr>
			<td><s:property value="active" /></td>
			<td><s:property value="make" /></td>
			<td><s:property value="model" /></td>
		</tr>
	</s:iterator>
</table>

</div>

<div>
select a webcam from the left
</div>

</body>
</html>