<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Audit Category Matrix</title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<script type="text/javascript" src="js/jquery/autocomplete/jquery.autocomplete.min.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocomplete/jquery.autocomplete.css" />
<script type="text/javascript" src="js/jquery/autocompletefb/jquery.autocompletefb.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/autocompletefb/jquery.autocompletefb.css" />
<style type="text/css">
#tableLoad input.edit { display: none; }
</style>
<script type="text/javascript">
function getCategories(id) {
	$('#tableLoad').empty();
	$('#filterLoad').empty();
	
	if (id == 2) {
		var data = {
			auditTypeID: id,
			button: "DesktopCategories"
		};
	
		startThinking({div: "categoryLoad", message: "Loading categories..."});
		$('#categoryLoad').load("AuditCategoryMatrixAjax.action", data);
	} else {
		$('#categoryLoad').empty();
		getTable(id, 0);
	}
}

function getTable(auditTypeID, categoryID) {
	$('#form1').find('input[name=auditTypeID]').val(auditTypeID);
	
	if (auditTypeID == 2 && categoryID == 0) {
		$('#tableLoad').html('<div class="error">Please select a category</div>');
	} else {
		if (auditTypeID > 0) {
			var data = {
				button: "Table",
				auditTypeID: auditTypeID,
				categoryID: categoryID
			};
		
			startThinking({div: "tableLoad", message: "Building matrix..."});
			$('#tableLoad').load("AuditCategoryMatrixAjax.action", data);
		} else
			$('#tableLoad').html('<div class="error">Please select an audit type</div>');
	}
}

function toggle(auditTypeID, itemID, categoryID, checkbox) {
	var data = {
		button: "Toggle",
		auditTypeID: auditTypeID,
		itemID: itemID,
		categoryID: categoryID,
		checked: checkbox.checked
	};

	$.getJSON("AuditCategoryMatrixAjax.action", data, function(json) {
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
	$('#tableLoad').load("AuditCategoryMatrixAjax.action?" + $('#form1').serialize(), { button: 'Table' });
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

function toggleEdit() {
	$('#tableLoad input.edit').toggle();
	$('#tableLoad img.view').toggle();
}
</script>
</head>
<body>
<h1>Audit Category Matrix</h1>

<div id="messages" style="display: none;"><s:include value="../actionMessages.jsp" /></div>
<div id="search">
	<s:form id="form1">
		<div class="filterOption">
			Audit: <s:select list="#{100:'Competency Review',2:'Manual Audit'}" headerKey="0" headerValue="- Select Audit Type -" 
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