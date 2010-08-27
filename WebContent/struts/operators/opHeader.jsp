<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="operator.name" /><span class="sub"><s:property value="subHeading" escape="false"/></span></h1>

<s:if test="permissions.admin">
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="FacilitiesEdit.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('operator_edit')">class="current"</s:if>>Edit</a></li>
	<li><a href="OperatorNotes.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('account_notes')">class="current"</s:if>>Notes</a></li>
	<s:if test="operator.equals(operator.inheritInsurance) || operator.equals(operator.inheritAudits)">
		<li><a href="AuditOperator.action?oID=<s:property value="operator.id"/>">Audits</a></li>
	</s:if>
	<li><a href="UsersManage.action?accountId=<s:property value="operator.id"/>">Users</a></li>
	<pics:permission perm="ManageEmployees">
		<li><a href="ManageEmployees.action?id=<s:property value="operator.id"/>">Employees</a></li>
	</pics:permission>
	<li><a href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('op_manage_flag_criteria') && !insurance">class="current"</s:if>>Flag Criteria</a></li>
	<s:if test="operator.canSeeInsurance.toString() == 'Yes'">
		<li><a href="ManageInsuranceCriteriaOperator.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('op_manage_flag_criteria') && insurance">class="current"</s:if>>Insurance Criteria</a></li>
	</s:if>
	<pics:permission perm="ManageProjects">
		<li><a href="ManageProjects.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('op_job_sites')">class="current"</s:if>>Projects</a></li>
	</pics:permission>
	<pics:permission perm="ContractorTags">
		<li><a href="OperatorTags.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('operator_tags')">class="current"</s:if>>Tags</a></li>
	</pics:permission>
	<pics:permission perm="ManageCategoryRules">
		<li><a href="OperatorCategoryRules.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('CategoryRules')">class="current"</s:if>>Category Rules</a></li>
	</pics:permission>
	<pics:permission perm="ManageAuditTypeRules">
		<li><a href="OperatorAuditTypeRules.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('AuditTypeRules')">class="current"</s:if>>Audit Type Rules</a></li>
	</pics:permission>
	<li><a href="ContractorList.action?filter.status=Active&filter.status=Demo<s:property value="operatorIds"/>">Contractors</a></li>
</ul>
</div>
</s:if>

<s:include value="../actionMessages.jsp"></s:include>
