<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
</head>
<body>
<div id="tabs">
	<ul>
		<li><a href="#tabs-general">General</a></li>
		<li><a href="#tabs-contact">Contact</a></li>
		<li><a href="#tabs-facilities">Facilities</a></li>
		<li><a href="#tabs-audits">Documents</a></li>
		<s:if test="permissions.admin || permissions.contractor">
			<li><a href="#tabs-membership">Membership</a></li>
		</s:if>
	</ul>
	<div id="tabs-general">
		<s:iterator value="contractor.operatorTags">
			<s:if test="permissions.admin">
				<s:property value="tag.tag"/> - <s:property value="tag.operator.name"/> <br/>
			</s:if>
			<s:if test="permissions.operator && permissions.accountId == tag.operator.id">
				<s:property value="tag.tag"/><br/> 
			</s:if>
		</s:iterator>
		<s:if test="contractor.dbaName.length() > 0">
			<label>DBA:</label>
			<s:property value="contractor.dbaName" />
			<br />
		</s:if>
		<label>PICS #:</label>
		<s:property value="contractor.id" />
		<br />
		<label>Operator Qualification:</label>
		<s:if test="contractor.requiresOQ">Enabled</s:if><s:else>Disabled</s:else>
		<br />
		<label>HSE Competency Review:</label>
		<s:if test="contractor.requiresCompetencyReview">Enabled</s:if><s:else>Disabled</s:else>
		<br />
		<label>Trade:</label>
		<s:property value="contractor.mainTrade" />
		<br />
		<label>Risk Level:</label>
		<s:property value="contractor.riskLevel" />
		<br />
		<label>Location:</label>
		<s:property value="contractor.city" />,
		<s:if test="contractor.country.hasStates">
			<s:property value="contractor.state.name" />
		</s:if>
		<s:if test="permissions.country != contractor.country.isoCode || !contractor.country.hasStates">
			<s:property value="contractor.country.name" />
		</s:if>
		<br />
		<a href="ContractorNotes.action?id=<s:property value="contractor.id" />">Notes</a>
		<s:if test="permissions.admin || permissions.contractor">
			| <a href="ContractorEdit.action?id=<s:property value="contractor.id" />">Edit</a>
		</s:if>
		<pics:permission perm="SwitchUser">
			<s:iterator value="contractor.users">
				| <a href="Login.action?button=login&switchToUser=<s:property value="id"/>">Login as <s:property value="name" /></a>
			</s:iterator>
		</pics:permission>
	</div>
	<div id="tabs-contact">
		<label>Primary Contact:</label>
		<s:property value="contractor.primaryContact.name"/>
		<br />
		<label>Phone:</label>
		<s:property value="contractor.primaryContact.phone"/>
		<br />	
		<label>Fax:</label>
		<s:property value="contractor.primaryContact.fax"/>
		<br />	
		<label>Email:</label>
		<s:property value="contractor.primaryContact.email"/>
		<br />	
	</div>
	<div id="tabs-facilities">
		<table class="report">
			<thead>
			<tr>
				<th></th>
				<th>Operator</th>
				<th>Waiting On</th>
				<pics:permission perm="ContractorApproval">
					<th>Work Status</th>
				</pics:permission>
			</tr>
			</thead>
			<s:iterator value="activeOperators">
			<tr>
				<td><s:property value="flagColor.smallIcon" escape="false" /></td>
				<td><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a></td>
				<td><s:property value="waitingOn"/></td>
				<pics:permission perm="ContractorApproval">
					<td><s:property value="workStatus"/></td>
				</pics:permission>
			</tr>
			</s:iterator>
		</table>
		<s:if test="permissions.admin || permissions.contractor">
			<a class="edit" href="ContractorFacilities.action?id=<s:property value="contractor.id" />">Edit Facilities</a>
		</s:if>
	</div>
	<div id="tabs-audits">
		<table class="report">
			<thead>
				<tr>
					<th>Document</th>
					<th>Status</th>
				</tr>
			</thead>
			<s:iterator value="activeAudits">
				<tr>
					<td><a href="Audit.action?auditID=<s:property value="id" />">
						<s:if test="auditFor.length() > 0"><s:property value="auditFor" /></s:if>
						<s:property value="auditType.auditName" /></a></td>
						<td><s:iterator value="getCaoStats(permissions).keySet()" id="status">
							<nobr><s:if test="getCaoStats(permissions).get(#status) > 1"><s:property value="getCaoStats(permissions).get(#status)"/></s:if>
							 <s:property value="#status"/></nobr><br/>							
						</s:iterator></td>
				</tr>
			</s:iterator>
		</table>
	</div>
	<s:if test="permissions.admin || permissions.contractor">
		<div id="tabs-membership">
			<s:iterator value="operatorTags"><s:property value="tag"/> </s:iterator>
			<label>PICS Membership:</label><s:property value="contractor.status"/>
			<br />
			<label>Membership:</label>
			<s:property value="contractor.membershipLevel.fee" />
			<br />
			<label>Balance:</label>
			$<s:property value="contractor.balance" />
			<br />
			<label>Member Since:</label>
			<s:date name="contractor.membershipDate" format="M/d/yyyy" />
			<br />
			<a href="BillingDetail.action?id=<s:property value="contractor.id" />">Billing Details</a>
		</div>
	</s:if>
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
