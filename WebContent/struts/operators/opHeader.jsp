<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="operator.name" /><span class="sub"><s:property value="subHeading" /></span></h1>

<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="FacilitiesEdit.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('operator_edit')">class="current"</s:if>>Edit</a></li>
	<li><a href="" class="inactive">Notes</a></li>
	<s:if test="operator.equals(operator.inheritInsurance) || operator.equals(operator.inheritAudits)">
		<li><a href="AuditOperator.action?oID=<s:property value="operator.id"/>">Audits</a></li>
	</s:if>
	<li><a href="UsersManage.action?accountId=<s:property value="operator.id"/>">Users</a></li>
	<li><a href="OperatorFlagCriteria.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('op_flag_criteria')">class="current"</s:if>>Flag Criteria</a></li>
	<pics:permission perm="ContractorTags">
		<li><a href="OperatorTags.action?id=<s:property value="operator.id"/>"
			<s:if test="requestURI.contains('operator_tags')">class="current"</s:if>>Tags</a></li>
	</pics:permission>
</ul>
</div>

<s:include value="../actionMessages.jsp"></s:include>
