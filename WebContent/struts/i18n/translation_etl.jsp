<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Import/Export Translations</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"></s:include>
<script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js?v=${version}"></script>
<script type="text/javascript">
function showImport() {
	$('#translationsArea').val('').show();
	$('#exportDiv').hide();
	$('#exportOptions').hide();
	$('#importDiv').show();
	$('#translationETL').show();
	$('#importedTable').html('<input type="button" class="picsbutton positive" id="importTranslationsButton" value="Check" />');
	$('#importTranslationsButton').click(function (e) {
		e.preventDefault();
		importTranslations();
	});
}

function showExport() {
	$('#translationETL').hide();
	$('#exportDiv').show();
	$('#importDiv').hide();
}

function importTranslations() {
	var translations = $('#translationsArea').val();
	
	$('#importedTable').html('<img src="images/ajax_process.gif" />');
	$('#importedTable').load("TranslationETL!importTranslationAjax.action",
		{ importTranslations: true, translations: translations },
		function() {
			$('#translationETL').show();
			$('#exportDiv').hide();
			$('#importDiv').show();
		}
	);
}

function exportTranslations() {
	var params = $('#etlForm').serializeArray().filter(function(t) { return t.name != 'translations'; });
	$('#translationETL').html('<img src="images/ajax_process.gif" />');
	
	params.push({name: "importTranslations", value: false});
	$('#translationETL').load('TranslationETL!exportTranslationAjax.action', 
		params,
		function(){
			$('#translationETL').show();
			$('#exportDiv').show();
			$('#importDiv').hide();
			clip.glue( 'clipButton', 'clipDiv' );
		}
	);
}

$(function() {
	$('.datepicker').datepicker();
	ZeroClipboard.setMoviePath( "js/zeroclipboard/ZeroClipboard.swf" );
	// Create our clipboard object as per usual
	clip = new ZeroClipboard.Client();
	clip.setHandCursor( true );
	
	clip.addEventListener('mouseOver', function (client) {
		// update the text on mouse over
		clip.setText( $('#translationsArea').val() );
	});
	
	clip.addEventListener('complete', function (client) {
		$('#clipButton').text("Copied to clipboard");
		$('#clipButton').css("background-image", 'url("images/okCheck.png")');
	});
	
	$('#translationsArea').live('keyup', function(){
		clip.setText( $('#translationsArea').val() );
		$('#clipButton').text("Copy to clipboard...");
		$('#clipButton').css("background-image", 'url("images/plus.png")');
	});
	
	$('#importButton').click(function (e) {
		e.preventDefault();
		showImport();
	});
	
	$('#exportButton').click(function (e) {
		e.preventDefault();
		showExport();
	});
	
	$('#importTranslationsButton').click(function (e) {
		e.preventDefault();
		importTranslations();
	});
	
	$('#exportTranslationsButton').click(function (e) {
		e.preventDefault();
		exportTranslations();
	});
});
</script>
</head>
<body>
	<s:include value="../actionMessages.jsp" />
	<s:form enctype="multipart/form-data" method="post" id="etlForm">
		<input type="button" class="picsbutton" value="Import..." id="importButton" />
		<input type="button" class="picsbutton" value="Export..." id="exportButton" />
		<div id="exportDiv" style="display: none; clear: both;">
			Start date:
			<s:textfield name="startDate" cssClass="datepicker" id="translationDate" />
			<br />
			<input type="button" class="picsbutton positive" value="Export" id="exportTranslationsButton" />
		</div>
		<div id="translationETL" style="clear: both; display: none;">
			<s:textarea name="translations" id="translationsArea" rows="20" cssStyle="width: 100%;" />
		</div>
		<div id="importDiv" style="display: none; clear: both;">
			<label>File Upload:</label>
			<s:file name="file" value="%{file}" size="50" id="translationFile"></s:file>
			<s:submit value="Upload" action="TranslationETL!upload" />
			<br />
			<br />
			<div id="importedTable">
				<input type="button" class="picsbutton positive" id="importTranslationsButton" value="Check" />
			</div>
		</div>
	</s:form>
</body>
</html>