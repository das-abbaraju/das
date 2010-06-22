<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker();
});
</script>

<form id="newForm" action="ManageAssessmentResults.action" method="POST">
	<fieldset class="form">
		<s:hidden name="id" />
		<s:hidden name="result.id" />
		<s:hidden name="result.createdBy.id" />
		<s:hidden name="result.creationDate" />
		<s:hidden name="result.updatedBy.id" />
		<s:hidden name="result.updateDate" />
		
		<s:if test="test.id > 0">
			<legend><span>Edit Assessment Result</span></legend>
		</s:if>
		<s:else>
			<legend><span>Add New Assessment Result</span></legend>
		</s:else>
		<ol>
			<li><label for="test">Assessment Test</label>
				<s:select list="tests" listKey="id" listValue="%{qualificationMethod + ' - ' + description}"
					headerKey="0" headerValue="- Assessment Test -" name="result.assessmentTest" /></li>
			<li><label for="company">Company</label>
				<!-- List of companies? -->
				</li>
			<li><label for="employee">Employee</label>
			<!-- Gets all 20k+ employees. Removing for now.
				<s:select list="employees" listKey="id" listValue="displayName" headerKey="0" 
					headerValue="- Employees -" name="result.employee" />
			 -->
			 </li>
			<li><label for="effective">Effective Date</label>
				<input type="text" name="result.effectiveDate" id="effective" class="datepicker"
					value="<s:property value="maskDateFormat(test.effectiveDate)" />" /></li>
			<li><label for="expiration">Expiration Date</label>
				<input type="text" name="result.expirationDate" id="expiration" class="datepicker"
					value="<s:property value="maskDateFormat(test.expirationDate)" />" /></li>
		</ol>
		<div style="margin-left: 20px; margin-bottom: 10px;">
			<input type="submit" name="button" value="Save" class="picsbutton positive" />
			<input type="button" value="Cancel" class="picsbutton negative" 
				onclick="$('#newForm').hide(); $('#addLink').show();" />
		</div>
	</fieldset>
</form>