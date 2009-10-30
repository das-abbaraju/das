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

	$('#messages').load('ReportInsuranceApprovalAjax.action', pars, function(text, status) {
			if(status=='success')
				clickSearch('form1');
		}
	);

	return false;
}

</script>
</head>
<body>
<h1>Policies Awaiting Decision</h1>

<div class="buttons">
	<a class="picsbutton button" href="#" onclick="return searchByFlag('Green');"><s:property 
		value="@com.picsauditing.jpa.entities.FlagColor@Green.bigIcon" escape="false"/>
		Show Policies to Approve</a>
	<a class="picsbutton button" href="#" onclick="return searchByFlag('Red');"><s:property 
		value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/>
		Show Policies to Reject</a>
</div>
<div class="clear"></div>

<s:include value="filters.jsp" />

<div id="messages">
</div>

<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<div id="report_data">
	<s:include value="opinsurance_approval_data.jsp" />
</div>
</s:form>

</body>
</html>