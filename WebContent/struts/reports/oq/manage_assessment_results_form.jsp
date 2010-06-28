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
		<input type="hidden" name="resultID" value="<s:property value="result.id" />" />
		
		<s:if test="result.id > 0">
			<legend><span>Edit Assessment Result</span></legend>
		</s:if>
		<s:else>
			<legend><span>Add New Assessment Result</span></legend>
		</s:else>
		<ol>
			<li><label for="company">Company:</label><s:select list="companies" listKey="id" 
				listValue="name" headerKey="0" headerValue="- Company -" name="companyID" onchange="getEmployee(this.value, %{resultID})" value="companyID"></s:select></li>
			<li id="employeeList"><s:if test="result.id > 0"><label for="employee">Employee:</label><s:select list="employees" listKey="id" 
				listValue="displayName" name="employeeID" value="employeeID" /></s:if></li>
			<li><label for="effective">Assessment Date:</label>
				<input type="text" name="result.effectiveDate" id="effective" class="datepicker"
					value="<s:property value="maskDateFormat(result.effectiveDate)" />" /></li>
			<li><label for="test">Assessment Test:</label>
				<s:select list="tests" listKey="id" listValue="%{qualificationMethod + ' - ' + description}"
					headerKey="0" headerValue="- Assessment Test -" name="testID" /></li>
		</ol>
		<div style="margin-left: 20px; margin-bottom: 10px;">
			<input type="submit" name="button" value="Save" class="picsbutton positive" />
			<s:if test="result.id > 0"><input type="submit" name="button" value="Remove" class="picsbutton negative" /></s:if>
			<input type="button" value="Cancel" class="picsbutton" 
				onclick="$('#newForm').slideUp(500); $('#addLink').show();" />
		</div>
	</fieldset>
</form>