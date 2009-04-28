<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h1><s:property value="operator.name" /><span class="sub"><s:property value="subHeading" /></span></h1>

<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="FacilitiesEdit.action?opID=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('operator_edit')">class="current"</s:if>>Edit</a></li>
	<s:if test="operator.corporate">
		<li><a href="AuditOperator.action?oID=<s:property value="operator.id"/>">Audits</a></li>
	</s:if>
	<li><a href="UsersManage.action?accountId=<s:property value="operator.id"/>">Users</a></li>
	<li><a href="op_editFlagCriteria.jsp?opID=<s:property value="operator.id"/>">Flag Criteria</a></li>
	<li><a href="OperatorFlagCriteria.action?id=<s:property value="operator.id"/>">Flag Criteria 2</a></li>
	<li><a href="OperatorTags.action?id=<s:property value="operator.id"/>"
		<s:if test="requestURI.contains('operator_tags')">class="current"</s:if>>Tags</a></li>
</ul>
</div>

<s:include value="../actionMessages.jsp"></s:include>
