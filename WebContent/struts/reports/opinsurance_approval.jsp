<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Insurance Policy Approval</title>
<s:include value="reportHeader.jsp" />

<script type="text/javascript">
function setAllChecked(elm) {
	$('.massCheckable').attr({checked: $(elm).is(':checked')});
	return false;
}

function searchByFlag(flag) {
	$('[name=filter.recommendedFlag]').val(flag);
	return clickSearch('form1');
}

function saveRows() {
	var pars = $('#approveInsuranceForm').serialize();
	pars += '&button=save';

	startThinking({div: 'messages', message: 'Saving changes', type: 'large'});

	$.post('ReportInsuranceApprovalAjax.action', pars, function(text, status) {
			$('#messages').html(text);
			if (status=='success')
				clickSearch('form1');
		}
	);

	return false;
}

function changeAuditStatus() {
	var caoIDs = new Array();
	$('#approveInsuranceForm').find('input[name=caoIDs]:checked').each(function() {
		caoIDs.push($(this).val());
	});

	var status = $('#approveInsuranceForm').find('input[name=newStatuses]:checked').val();
	if (status == undefined) {
		alert("Please choose a status");
		return false;
	}
	
	var data= {
		status: status,
		caoIDs: caoIDs,
		button: 'statusLoad',
		insurance: true
	};

	$('#noteAjax').load('CaoSaveAjax.action', data, function(){
        $.blockUI({ message:$('#noteAjax'), css: { width: '350px'} });
         
        if($('.clearOnce').val()=='')
			$('#clearOnceField').val(0);
		
	    $('#yesButton').click(function(){
	        $.blockUI({message: 'Saving Status, please wait...'});
	        data.button = '';
	        data.note = $('#addToNotes').val();
	        data.insurance = true;
	        $.post('CaoSaveAjax.action', data, function() { $.unblockUI(); });
	    });
	     
	    $('#noButton').click(function(){
	        $.unblockUI();
	        return false;
	    });
	});
		
	return false;
}

</script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
</head>
<body>
<h1>Policies Awaiting Decision</h1>

<div class="buttons">
	<button class="picsbutton" onclick="return searchByFlag('Green');">
		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Green.bigIcon" escape="false"/> Show Policies to Approve
	</button>
	<button class="picsbutton" onclick="return searchByFlag('Red');">
		<s:property	value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/> Show Policies to Reject
	</button>
</div>
<div class="clear"></div>

<s:include value="filters.jsp" />

<div id="messages"></div>
<div id="noteAjax"></div>

<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<div id="report_data">
	<s:include value="opinsurance_approval_data.jsp" />
</div>
</s:form>

</body>
</html>