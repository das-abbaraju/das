<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="EmailWizard.title" /></title>

<s:include value="../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<style type="text/css">
.back_to_step {
	display: none;
}
</style>
<script type="text/javascript">
$(function() {
	<s:if test="type != null">
		selectList('<s:property value="type"/>');
	</s:if>
	$('.target_sample').delegate('.target_recipient', 'click', function() {
		selectList($(this).data('type'));
	}).delegate('#back_to_step1', 'click', function(e) {
		e.preventDefault();
		
		$('#back_to_step1').hide();
		$('#target_recipients').show('normal');
		$('#filter_recipients').fadeOut('normal');
		$('#report_data').empty();
		$('#selectedListType').hide();

		$.ajax({
			url: 'ReportFilterAjax.action',
			data: {
				clear: true
			}
		});
	});
});

function selectList(listType) {
	$('#filter_recipients').show();
	$('#filter_recipients').html(translate('JS.EmailWizard.LoadingFilters' + listType + 's'));
	$('#report_data').empty();
	$('#target_recipients').hide('normal');
	$('#back_to_step1').show();
	$('#filter_recipients').load('ReportFilterAjax.action', {listType:listType}, 
		function(response, status) {
			if (status=='success') {
				loadFiltersCallback();
				$('#selectedListType').html(translate('JS.EmailWizard.' + listType + 'List')).fadeIn('normal');
			}
		}
	);
}
</script>
</head>
<body>

<h1><s:text name="EmailWizard.title" /></h1>

<s:if test="listSize > 0">
	<div class="alert"><s:text name="EmailWizard.RecordsInMailingList"><s:param value="%{listSize}" /></s:text></div>
</s:if>

<div class="clear"></div>
<!--Step 1-->
<div class="target_sample">
	<div class="instructions"><s:text name="EmailWizard.ChooseAMethod" /></div>
	<span class="step_number">1</span>
	<div id="back_to_step1" class="back_to_step" title="<s:text name="EmailWizard.StartOver" />">
		<s:text name="EmailWizard.StartOver" />
	</div>
	<h2 id="selectedListType" style="text-align: center"></h2>
	<div id="target_recipients">
		<div class="target_recipient" data-type="Contractor">
			<s:text name="EmailWizard.Contractors" />
			<span class="shadow_sample"></span>
		</div>
		
		<div class="target_recipient" data-type="User">
			<s:text name="EmailWizard.Users" />
			<span class="shadow_sample"></span>
		</div>
		
		<div class="target_recipient" data-type="Audit">
			<s:text name="EmailWizard.ContractorsByAudit" />
			<span class="shadow_sample"></span>
		</div>
		<div class="clear"></div>
	</div>
</div>

<!--Step 2-->
<div class="target_sample">
	<div class="instructions"><s:text name="EmailWizard.FindRecipients" /></div>
	<span class="step_number">2</span>
	<div id="filter_recipients"></div>
</div>

<!--Step 3-->
<div class="target_sample">
	<div class="instructions"><s:text name="EmailWizard.ReviewRecipients" /></div>
	<span class="step_number">3</span>
	<div id="report_data"></div>
</div>

<span id="email_previousSTP"><a href="#"><s:text name="EmailWizard.PreviousStep" /></a></span>
</body>
</html>