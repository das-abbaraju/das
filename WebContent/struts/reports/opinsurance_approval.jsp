<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="ReportInsuranceApproval.title" /></title>
<s:include value="reportHeader.jsp" />

<script type="text/javascript">
$(function() {
	$('.buttons').delegate('.searchByFlag', 'click', function() {
		var flag = $(this).data('color');
		$('[name="filter.recommendedFlag"]').val(flag);
		return clickSearch('form1');
	});
	
	$('#report_data').delegate('.positive', 'click', function(e) {
		e.preventDefault();
		
		var caoIDs = new Array();
		$('#approveInsuranceForm').find('input[name="caoIDs"]:checked').each(function() {
			caoIDs.push($(this).val());
		});

		var status = $('#approveInsuranceForm').find('input[name="newStatuses"]:checked').val();
		if (status == undefined) {
			alert("Please choose a status");
			return false;
		}
		
		var data= {
			status: status,
			caoIDs: caoIDs,
			insurance: true
		};

		$('#noteAjax').load('CaoSaveAjax!loadStatus.action', data, function(){
	        $.blockUI({ message:$('#noteAjax'), css: { width: '350px'} });
	         
	        if($('.clearOnce').val()=='')
				$('#clearOnceField').val(0);
			
		    $('#yesButton').click(function(){
		        $.blockUI({message: 'Saving Status, please wait...'});
		        data.note = $('#addToNotes').val();
		        data.insurance = true;
		        $.post('CaoSaveAjax!save.action', data, function() { $.unblockUI(); clickSearch('form1') });
		    });
		     
		    $('#noButton').click(function(){
		        $.unblockUI();
		        return false;
		    });
		});
	}).delegate('#setAllCheckboxes', 'click', function() {
		$('.massCheckable').attr('checked', $(this).is(':checked'));
	});
});
</script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<link rel="stylesheet" href="js/jquery/blockui/blockui.css" type="text/css" />
<link rel="stylesheet" href="css/audit.css" type="text/css" />

<style type="text/css">
.closeButton{
	bottom: 0px !important;	
}
</style>

</head>
<body>
<h1><s:text name="ReportInsuranceApproval.title" /></h1>

<div class="buttons">
	<button class="picsbutton searchByFlag" data-color="Green">
		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Green.bigIcon" escape="false"/>
		<s:text name="ReportInsuranceApproval.ShowPoliciesToApprove" />
	</button>
	<button class="picsbutton searchByFlag" data-color="Red">
		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/>
		<s:text name="ReportInsuranceApproval.ShowPoliciesToReject" />
	</button>
</div>
<div class="clear"></div>

<s:include value="filters.jsp" />

<div id="messages"></div>
<div id="noteAjax" style="display: none;"></div>

<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<div id="report_data">
	<s:include value="opinsurance_approval_data.jsp" />
</div>
</s:form>

</body>
</html>