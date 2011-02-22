<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>

<title>Manage Translations</title>

<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/translate/jquery.translate-1.4.7-debug-all.js"></script>
<script type="text/javascript">
$(function(){
	$('table.report .showEdit').click(function() {
		//$("body").translate('ru');
		$(this).closest("td").addClass("editMode");
		return false;
	});
	
	$('table.report button.cancel').click(function() {
		$(this).closest("td").removeClass("editMode");
	});

	$('table.report button.save').click(function() {
		var that = $(this).closest("td");
		that.addClass("saving");
		
		var params = $(this).closest("form").serialize();
		
		
		$.post('ManageTranslationsAjax.action', params, function(result) {
			if (result.success) {
				that.find("input[name|='translation']").val(result.id);
				that.find("span").html(that.find("textarea").val());
				that.removeClass("editMode");
			} else {
				alert(result.reason);
			}
			that.removeClass("saving");
		}, "json");
	});
});

function translateAll() {
	$("body").translate('hi');
}

</script>
<style type="text/css">
td.saving, td.saving textarea, td.saving button {
	cursor: wait;
}

table.report td .edit {
	display: none;
}
table.report td.editMode .edit {
	display: inherit;
}
table.report td.editMode .view {
	display: none;
}
span.view {
	padding-left: 10px;
}
</style>
</head>
<body>
<h1>Manage Translations</h1>

<div class="search">
<s:form method="GET">
	From: <s:select
		list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
		name="localeFrom" listValue="displayName"></s:select>
	To: <s:select
		list="@com.picsauditing.jpa.entities.AppTranslation@getLocales()"
		name="localeTo" listValue="displayName"></s:select>
		
	Key: <s:textfield name="key" />
	<s:submit name="button" value="Search"></s:submit>
</s:form>
</div>

<table class="report" style="width: 100%">
	<thead>
		<tr>
			<td width="20%">Key</td>
			<td width="40%"><s:property value="localeFrom.displayName"/></td>
			<td width="40%"><s:property value="localeTo.displayName"/></td>
		</tr>
	</thead>
	<s:iterator value="list">
		<tr class="translate" id="row<s:property value="from.id"/>">
			<td><s:property value="from.key"/></td>
			<s:iterator value="items">
				<td class="phrase">
					<form onsubmit="return false;">
						<input type="hidden" name="translation" value="<s:property value="id"/>">
						<s:if test="!(id > 0)">
							<input type="hidden" name="translation.locale" value="<s:property value="localeTo"/>">
							<input type="hidden" name="translation.key" value="<s:property value="from.key"/>">
						</s:if>
						<input type="hidden" name="button" value="save">
						<a href="#" class="showEdit view">Edit</a>
						<span class="view">
							<s:property value="value"/>
						</span>
						<div class="edit">
							<s:textarea name="translation.value" value="%{value}" cols="50"></s:textarea>
							<br/>
							<button name="button" class="save">Save</button>
							<button class="cancel">Cancel</button>
						</div>
					</form>
				</td>
			</s:iterator>
		</tr>
	</s:iterator>
</table>

<s:form>
<input type="hidden" name="translation.locale" value="<s:property value="localeFrom"/>">
Add New Key <input type="text" name="translation.key"> for <s:property value="localeFrom.displayName"/><br />
<s:textarea name="translation.value" cols="50"></s:textarea><br />
<button name="button" class="save" value="save">Save</button>
</s:form>

</body>
</html>