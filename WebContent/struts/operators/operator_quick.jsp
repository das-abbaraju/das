<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
</head>
<body>
<div id="tabs">
	<ul>
		<li><a href="#tabs-general">General</a></li>
		<li><a href="#tabs-audits">PQF/Audits</a></li>
		<li><a href="#tabs-users">Users</a></li>
		<s:if test="operator.description.length() > 0">
			<li><a href="#tabs-description">Description</a></li>
		</s:if>
		<li><a href="#tabs-notes">Notes</a></li>
	</ul>
	<div id="tabs-general">
		<s:iterator value="operator.corporateFacilities">
		<label>Parent:</label> <s:property value="corporate.name"/><br />
		</s:iterator>
		<label>Location:</label> <s:property value="operator.city"/>, <s:property value="operator.countrySubdivision"/><br />
		
		<label>Primary Contact:</label> <s:property value="operator.primaryContact.name"/> <s:property value="operator.primaryContact.phone"/><br />
		<label>Industry:</label>
			<s:property value="operator.industry.description"/><br />
		
		<s:iterator value="operator.accountUsers">
			<s:if test="current">
				<label><s:property value="role.description"/>:</label> <s:property value="user.name"/><br />
			</s:if>
		</s:iterator>
		
		<label>Other:</label>
		<s:if test="operator.approvesRelationships.true">| Approves Relationships</s:if>
		
		<s:if test="operator.doContractorsPay == 'No'">| FREE account</s:if>
		
		<s:if test="operator.doContractorsPay == 'Multiple'">| FREE if only account</s:if>
		|
	</div>
	<div id="tabs-audits">
		<a href="ContractorSimulator.action?operatorIds=<s:property value="operator.id" />">Run Contractor Simulator</a><br />
		<pics:permission perm="EditFlagCriteria">
			[<a 
			href="ManageFlagCriteriaOperator.action?id=<s:property value="operator.inheritFlagCriteria.id" />">Flag Criteria</a>]		
		</pics:permission>
	</div>
	<div id="tabs-users">
		<s:iterator value="operator.users">
			<s:if test="activeB && !group">| <a href="UsersManage.action?account=<s:property value="account.id"/>&user=<s:property value="id"/>"><s:property value="name"/></a></s:if>
		</s:iterator>
		|<br /><br />
		<a href="UsersManage.action?account=<s:property value="operator.id"/>">Manage Users</a>
	</div>
	
	<div id="tabs-description">
		<s:property value="operator.description"/>
	</div>
	<div id="tabs-notes">
		<s:include value="../notes/account_notes_embed.jsp"></s:include>
	</div>
</div>

<script type="text/javascript">
$(function() {
	$("#tabs").tabs({
		event: 'mouseover'
	});
});
</script>
</body>
</html>
