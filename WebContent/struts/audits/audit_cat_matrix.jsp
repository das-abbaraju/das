<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:text name="AuditCategoryMatrix.subheading"/></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function getCategories(id) {
	$('#tableLoad').empty();
	$('#filterLoad').empty();
	$('#categoryLoad').empty();
	getTable(id, 0);
}

function getTable(auditTypeID, categoryID) {
	$('#form1').find('input[name="auditTypeID"]').val(auditTypeID);
	
	if (auditTypeID == 2 && categoryID == 0) {
		$('#tableLoad').html('<div class="error">Please select a category</div>');
	} else {
		if (auditTypeID > 0) {
			var data = {
				auditTypeID: auditTypeID,
				categoryID: categoryID
			};
		
			startThinking({div: "tableLoad", message: "Building matrix..."});
			$('#tableLoad').load("AuditCategoryMatrix!tableAjax.action", data);
		} else
			$('#tableLoad').html('<div class="error">Please select an audit type</div>');
	}
}

function toggle(auditTypeID, itemID, categoryID, checkbox) {
	var data = {
		auditTypeID: auditTypeID,
		itemID: itemID,
		categoryID: categoryID,
		checked: checkbox.checked
	};

	$.getJSON("AuditCategoryMatrix!toggleAjax.action", data, function(json) {
			if (json.reset == true)
				checkbox.checked = !checkbox.checked;
			$.gritter.add({
				title: json.title,
				text: json.msg
			})
		}
	);
}

function addFilter(auditTypeID, categoryID, itemID, type) {
	startThinking({div: "tableLoad", message: "Building Matrix..."});
	$('#tableLoad').load("AuditCategoryMatrix!tableAjax.action?" + $('#form1').serialize());
}

function toggleBox(name) {
	var box = $('#'+name+'_select');
	var result = $('#'+name+'_query');
	result.hide();
	box.toggle();
	if (box.is(':visible'))
		return;

	updateQuery(name);
	result.show();
}

function updateQuery(name) {
	var box = $("#"+name);
	var result = $("#"+name+'_query');
	var queryText = '';
	box.find('option:selected').each(function(i,e){
		if (queryText != '') queryText += ", ";
		queryText += $(e).text();
	});
	
	if (queryText == '') {
		queryText = 'NONE';
	}
	result.text(queryText);
}

function clearSelected(name) {
	$("#"+name).find('option').attr({'selected': false});
	updateQuery(name);
}

function selectAll(name) {
	$("#"+name).find('option').attr({'selected': true});
	updateQuery(name);
}

function editTable() {
	startThinking({div: "table_thinking", message: "Updating table..."});
	$('#tableLoad').load("AuditCategoryMatrix!tableAjax.action?" + $('#form1').serialize(), { editTable: true });
}

function viewTable() {
	startThinking({div: "table_thinking", message: "Updating table..."});
	$('#tableLoad').load("AuditCategoryMatrix!tableAjax.action?" + $('#form1').serialize(), { editTable: false });
}
</script>
</head>
<body>
<h1><s:text name="AuditCategoryMatrix.subheading"/></h1>

<div id="messages" style="display: none;"><s:include value="../actionMessages.jsp" /></div>
<div id="search">
	<s:form id="form1">
		<div class="filterOption">
			<s:text name="AuditCategoryMatrix.label.Audit"/><s:select list="#{100:getText('AuditCategoryMatrix.label.CompetencyReview')}" headerKey="0" headerValue="%{getText('global.SelectAuditType')}" 
				onchange="getCategories(this.value);" name="auditTypeID" />
		</div>
		<div class="clear"></div>
		<div class="filterOption"><div id="categoryLoad"><s:hidden name="categoryID" value="0" /></div></div>
		<div class="clear"></div>
		<div id="filterLoad"></div>
		<div class="clear"></div>
	</s:form>
</div>
<div id="tableLoad"></div>
</body>
</html>