<%@ taglib prefix="s" uri="/struts-tags"%>
<s:include value="../actionMessages.jsp"/>

<script type="text/javascript" src="js/jquery/jquery.fieldfocus.js"></script>
<script type="text/javascript">
$(function(){
	$('#auditName').val($('#auditName').val()+'_Copy');
});
</script>
<s:form id="textForm">
<fieldset class="form" style="border: none">
<ol>
	<li class="required"><label>Name:</label>
		<s:textfield id="auditName" name="auditType.auditName"></s:textfield>
		<div class="fieldhelp">
			<h3>Change Name:</h3>
			A <strong>Unique</strong> name is required for copying an audit
		</div>
	</li>
	<li><label>Class:</label>
		<s:select list="classList" name="auditType.classType"></s:select>
	</li>
	<li><label>Description:</label>
		<s:textfield name="auditType.description"></s:textfield>
	</li>
	<li><label>Has Multiple:</label>
		<s:checkbox name="auditType.hasMultiple" />
	</li>
	<li><label>Can Renew:</label>
		<s:checkbox name="auditType.renewable" />
	</li>
	<li><label>Is Scheduled:</label>
		<s:checkbox name="auditType.scheduled" />
	</li>
	<li><label>Has Safety Professional:</label>
		<s:checkbox name="auditType.hasAuditor" />
	</li>
	<li><label>Contractor Can View:</label>
		<s:checkbox name="auditType.canContractorView" />
	</li>
	<li><label>Contractor Can Edit:</label>
		<s:checkbox name="auditType.canContractorEdit" />
	</li>
	<li><label title="Add the operator or corporateID only if requested by 1 account.">
		Required By Operator:</label>
		<s:textfield name="operatorID" value="%{auditType.account.id}" /> 
	</li>
	<li><label>Months to Expire:</label>
		<s:textfield name="auditType.monthsToExpire" /> 
	</li>
	<li><label>Order:</label>
		<s:textfield name="auditType.displayOrder" />
	</li>
		<li><label>Email Template:</label>
		<s:select list="templateList" name="emailTemplateID" 
			headerKey="" headerValue="- Email Template -"
			listKey="id" listValue="templateName" />
	</li>
</ol>
</fieldset>
</s:form>
