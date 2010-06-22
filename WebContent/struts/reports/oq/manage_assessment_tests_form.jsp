<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker();
});
</script>

<form id="newForm" action="ManageAssessmentTests.action" method="POST">
	<fieldset class="form">
		<s:hidden name="id" />
		<s:hidden name="test.id" />
		
		<s:if test="test.id > 0">
			<legend><span>Edit Assessment Test</span></legend>
		</s:if>
		<s:else>
			<legend><span>Add New Assessment Test</span></legend>
		</s:else>
		<ol>
			<li><label for="qualType">Qualification Type:</label>
				<s:textfield name="test.qualificationType" id="qualType" /></li>
			<li><label for="qualMethod">Qualification Method:</label>
				<s:textfield name="test.qualificationMethod" id="qualMethod" /></li>
			<li><label for="description">Description:</label>
				<s:textfield name="test.description" id="description" /></li>
			<li><label for="effective">Effective Date:</label>
				<input type="text" name="test.effectiveDate" id="effective" class="datepicker"
					value="<s:property value="maskDateFormat(test.effectiveDate)" />" /></li>
			<li><label for="verifiable">Verifiable:</label>
				<s:checkbox name="test.verifiable" id="verifiable" /></li>
			<li><label for="months">Months To Expire:</label>
				<s:textfield name="test.monthsToExpire" id="months" /></li>
		</ol>
		<div style="margin-left: 20px; margin-bottom: 10px;">
			<input type="submit" name="button" value="Save" class="picsbutton positive" />
			<input type="button" value="Cancel" class="picsbutton negative" 
				onclick="$('#newForm').hide(); $('#addLink').show();" />
		</div>
	</fieldset>
</form>