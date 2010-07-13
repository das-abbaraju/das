<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<script type="text/javascript">
$().ready(function() {
	$('.datepicker').datepicker();
	$('#company_add').autocomplete('ContractorSelectAjax.action', 
		{
			minChars: 3,
			extraParams: {'filter.accountName': function() {return $('#company_add').val();} },
			formatResult: function(data,i,count) { return data[0]; }
		}
	).result(function(event, data){
		$('input#companyID').val(data[1]);
		getEmployee('employee_add', $('input#companyID').val(), $('input#resultID').val());
	});
});
</script>

<form id="newForm" action="ManageAssessmentResults.action" method="POST">
	<fieldset class="form">
		<s:hidden name="id" />
		<s:hidden name="result.id" />
		<input type="hidden" name="resultID" value="<s:property value="result.id" />" id="resultID" />
		<input type="hidden" name="companyID" value="<s:property value="companyID" />" id="companyID" />
		
		<s:if test="result.id == 0">
			<h2 class="formLegend">Add New Assessment Result</h2>
		</s:if>
		<ol>
			<li><label for="company">Company:</label>
				<s:textfield name="company" id="%{result.id == 0 ? 'company_add' : 'company_edit'}" />
			</li>
			<s:if test="result.id == 0"><li id="employee_add"></li></s:if>
			<s:else>
				<li id="employee_edit"><label>Employee:</label>
					<s:select list="employees" listKey="id" listValue="displayName" name="employeeID" />
				</li>
			</s:else>
			<li><label for="effective">Effective Date:</label>
				<input type="text" name="result.effectiveDate" id="effective" class="datepicker"
					value="<s:property value="maskDateFormat(result.effectiveDate)" />" /></li>
			<li><label for="test">Assessment Test:</label>
				<s:select list="tests" listKey="id" listValue="%{qualificationMethod + ' - ' + description}"
					headerKey="0" headerValue="- Assessment Test -" name="testID" /></li>
		</ol>
		<div style="margin-left: 20px; margin-bottom: 10px;">
			<input type="submit" name="button" value="Save" class="picsbutton positive" />
			<s:if test="result.id > 0"><input type="submit" name="button" value="Remove" class="picsbutton negative" /></s:if>
			<s:if test="result.id == 0">
				<input type="button" value="Cancel" class="picsbutton" 
					onclick="$('#newForm').slideUp(500); $('#addLink').show();" />
			</s:if>
		</div>
	</fieldset>
</form>