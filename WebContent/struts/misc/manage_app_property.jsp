<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage App Properties</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<style type="text/css">
.new, #addNew {
	display: none;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function edit(prop) {
	$('.old').show();
	$('.new').hide();
	$('#'+prop+' .new').show();
	$('#'+prop+' .old').hide();
}

function save(prop) {
	self.location = 'ManageAppProperty.action?button=Save&' + $('#'+prop+' input').serialize();
}

function remove(prop) {
	self.location = 'ManageAppProperty.action?button=Remove&' + $('#'+prop+' input').serialize();
}
</script>
</head>
<body>
<h1>Manage App Properties</h1>

<s:include value="../actionMessages.jsp"/>

<table class="report">
	<thead>
		<tr>
			<th>Property</th>
			<th>Value</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="all">
			<tr id="<s:property value="property" />">
				<td>
					<span class="old"><s:property value="property" /></span>
					<span class="new"><input type="text" id="newProperty" name="newProperty" value="<s:property value="property" />" /></span>
				</td>
				<td>
					<span class="old"><s:property value="value" /></span>
					<span class="new"><input type="text" id="newValue" name="newValue" value="<s:property value="value" />" /></span>
				</td>
				<td>
					<span class="old"><a href="#" onclick="edit('<s:property value="getSafe(property)" />'); return false;" class="edit">&nbsp;</a></span>
					<span class="new"><a href="#" onclick="save('<s:property value="getSafe(property)" />'); return false;" class="save">&nbsp;</a></span>
				</td>
				<td><a href="#" onclick="remove('<s:property value="getSafe(property)" />'); return false;" class="remove">&nbsp;</a></td>
			</tr>
		</s:iterator>
	</tbody>
</table>
<a href="#" onclick="$('#addNew').show(); $(this).hide(); return false;" class="add">Add New App Property</a>
<div id="addNew">
	<s:form>
		<fieldset class="form">
			<ol>
				<li>
					<label>Property:</label>
					<input type="text" name="newProperty" />
				</li>
				<li>
					<label>Value:</label>
					<input type="text" name="newValue" />
				</li>
			</ol>
			<input type="submit" value="Save" name="button" class="picsbutton positive" />
		</fieldset>
	</s:form>
</div>
</body>
</html>
