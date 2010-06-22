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
		
		<s:if test="result.id > 0">
			<legend><span>Edit Assessment Result</span></legend>
		</s:if>
		<s:else>
			<legend><span>Add New Assessment Result</span></legend>
		</s:else>
		<ol>
			<li><label for="test">Assessment Test:</label>
				<s:select list="tests" listKey="id" listValue="%{qualificationMethod + ' - ' + description}"
					headerKey="0" headerValue="- Assessment Test -" name="testID" /></li>
			<li><label for="company">Company:</label><s:select list="companies" listKey="id" 
				listValue="name" headerKey="0" headerValue="- Company -" onchange="getEmployee(this.value, %{resultID})" value="companyID"></s:select></li>
			<li id="employeeList"></li>
			<s:if test="result.id > 0">
				<script type="text/javascript">getEmployee(<s:property value="companyID" />, <s:property value="resultID" />);</script>
			</s:if>
			<li><label for="effective">Effective Date:</label>
				<input type="text" name="result.effectiveDate" id="effective" class="datepicker"
					value="<s:property value="maskDateFormat(result.effectiveDate)" />" /></li>
			<li><label for="expiration">Expiration Date:</label>
				<input type="text" name="result.expirationDate" id="expiration" class="datepicker"
					value="<s:property value="maskDateFormat(result.expirationDate)" />" /></li>
		</ol>
		<div style="margin-left: 20px; margin-bottom: 10px;">
			<input type="submit" name="button" value="Save" class="picsbutton positive" />
			<input type="button" value="Cancel" class="picsbutton negative" 
				onclick="$('#newForm').hide(); $('#addLink').show();" />
		</div>
	</fieldset>
</form>