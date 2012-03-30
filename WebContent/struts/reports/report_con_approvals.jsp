<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<head>
	<title><s:text name="ContractorApproval.title" /></title>
	<s:include value="reportHeader.jsp" />
</head>
<body>
	<h1><s:text name="ContractorApproval.title" /></h1>
	
	<s:include value="filters.jsp" />
	
	<s:form id="approveContractorForm" method="post" cssClass="forms">
	<div id="report_data">
		<s:include value="report_con_approvals_data.jsp" />
	</div>
	</s:form>
	<script type="text/javascript">
	$(function() {
		$('#report_data').delegate('#selectAll', 'click', function() {
			$('.massCheckable').attr('checked', $(this).is(':checked'));
		}).delegate('#saveChanges', 'click', function(e) {
			e.preventDefault();
			
			var pars = $('#approveContractorForm').serialize();
			startThinking({div: 'messages', message: translate('JS.ContractorApproval.SavingChanges'), type: 'large'});
	
			$.post('ContractorApprovalAjax!save.action', pars, function(text, status) {
				if (status=='success')
					clickSearch('form1');
				}
			);
		});
	});
	</script>
</body>