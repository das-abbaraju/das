<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
<title>Contractor Approval </title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function setAllChecked(elm) {
	$('.massCheckable').attr({checked: $(elm).is(':checked')});
	return false;
}

function saveRows() {
	var pars = $('#approveContractorForm').serialize();
	pars += '&button=Save';
	startThinking({div: 'messages', message: 'Saving changes', type: 'large'});

	$.post('ContractorApprovalAjax.action', pars, function(text, status) {
			if (status=='success')
				clickSearch('form1');
		}
	);

	return false;
}

</script>

</head>
<body>
<h1>Contractor Approval</h1>

<s:include value="filters.jsp" />

<s:form id="approveContractorForm" method="post" cssClass="forms">
<div id="report_data">
	<s:include value="report_con_approvals_data.jsp" />
</div>
</s:form>

</body>
</html>
