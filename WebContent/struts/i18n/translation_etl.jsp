<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Import/Export Translations</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"></s:include>
<script type="text/javascript" src="js/zeroclipboard/ZeroClipboard.js"></script>
<script type="text/javascript">
function showImport() {
	$('#translationsArea').val('');
	$('#exportDiv').hide();
	$('#exportOptions').hide();
	$('#importDiv').show();
	$('#translationETL').show();
	$('#importedTable').html('<input type="button" class="picsbutton positive" id="importButton" value="Check" onclick="importTranslations(); return false;" />');
}

function showExport() {
	$('#translationETL').hide();
	$('#exportDiv').show();
	$('#importDiv').hide();
}

function importTranslations() {
	var translations = $('#translationsArea').val();
	$('#importedTable').html('<img src="images/ajax_process.gif" />');
	$('#importedTable').load('TranslationETL!importTranslationAjax.action', { importTranslations: true, translations: translations },
		function(){
			$('#translationETL').show();
			$('#exportDiv').hide();
			$('#importDiv').show();
		}
	);
}

function exportTranslations() {
	$('#translationETL').html('<img src="images/ajax_process.gif" />');
	$('#translationETL').load('TranslationETL!exportTranslationAjax.action', 
		{ startDate: $('#translationDate').val(), importTranslations: false },
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
	ZeroClipboard.setMoviePath( "http://localhost:8080/picsWeb2/js/zeroclipboard/ZeroClipboard10.swf" );
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
});
</script>
</head>
<body>
<s:include value="../actionMessages.jsp" />
<s:form>
	<input type="button" class="picsbutton" value="Import..." onclick="showImport(); return false;" />
	<input type="button" class="picsbutton" value="Export..." onclick="showExport(); return false;" />
	<div id="exportDiv" style="display: none; clear: both;">
		Start date: <s:textfield name="startDate" cssClass="datepicker" id="translationDate" /><br />
		<input type="button" class="picsbutton positive" value="Export" onclick="exportTranslations(); return false;" />
		<!-- <s:submit action="TranslationETL!exportTranslation" value="Export" cssClass="picsbutton positive" /> -->
	</div>
	<div id="translationETL" style="clear: both; display: none;">
		<s:textarea name="translations" id="translationsArea" rows="20" cssStyle="width: 100%;" />
	</div>
	<div id="importDiv" style="display: none; clear: both;">
		<div id="importedTable">
			<input type="button" class="picsbutton positive" id="importButton" value="Check" onclick="importTranslations(); return false;" />
		</div>
	</div>
</s:form>
</body>
</html>