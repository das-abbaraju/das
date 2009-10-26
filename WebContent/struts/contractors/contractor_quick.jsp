<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
</head>
<body>
<div id="tabs">
	<ul>
		<li><a href="#tabs-1">General</a></li>
		<li><a href="#tabs-2">Facilities</a></li>
		<li><a href="#tabs-3">PQF/Audits</a></li>
	</ul>
	<div id="tabs-1">
		<s:if test="contractor.dbaName.length() > 0">
			<p class="fn org">DBA <s:property value="contractor.dbaName" /></p>
		</s:if>
		<label>Location:</label>
		<s:property value="contractor.city" />, <s:property value="contractor.state" />
		<br />
		<label>Primary Contact:</label>
		<s:property value="contractor.contact" />
		<br />
		<label>Industry:</label>
		<s:property value="contractor.industry.description" />
		<br />
		<label>Trade:</label>
		<s:property value="contractor.mainTrade" />
		<br />
		<s:iterator value="operatorTags"><s:property value="tag"/> </s:iterator>
		<label>PICS Contractor ID:</label>
		<s:property value="contractor.id" />
		<br />
		<label>Risk Level:</label>
		<s:property value="contractor.riskLevel" />
		<br />
	</div>
	<div id="tabs-2">
		<ul style="list-style-type: none;">
			<s:iterator value="contractor.operators">
			<li>
				<s:if test="flag != null">
					<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flag.flagColor.smallIcon" escape="false" /></a>
				</s:if>
				<s:else>
					<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><img src="images/icon_Flag.gif" width="10" height="12" border="0" title="Blank"/></a>
				</s:else>
				<a title="Waiting On : <s:property value="flag.waitingOn"/>" href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a>
			</li>
			</s:iterator>
		</ul>
	</div>
	<div id="tabs-3">
		<s:iterator value="audits">
			<a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /> for </a><br />
		</s:iterator>
	</div>
</div>

<table width="100%" class="navbar">
	<tr>
		<td><a href="ContractorNotes.action?id=<s:property value="contractor.id" />">Notes</a></td>
		<td><a href="ContractorEdit.action?id=<s:property value="contractor.id" />">Edit</a></td>
		<td><a href="BillingDetail.action?id=<s:property value="contractor.id" />">Billing</a></td>
		<td><a href="ContractorNotes.action?id=<s:property value="contractor.id" />">Auto Login</a></td>
		<td><a href="ContractorNotes.action?id=<s:property value="contractor.id" />">Facilities</a></td>
	</tr>
</table>

<script type="text/javascript">
$(function() {
	$("#tabs").tabs({
		event: 'mouseover'
	});
});
</script>
</body>
</html>
