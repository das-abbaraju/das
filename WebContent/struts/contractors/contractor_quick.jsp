<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
<script type="text/javascript">
$(function() {
	$("#tabs").tabs({
		event: 'mouseover'
	});
});
</script>
</head>
<body>
<div>

<div id="tabs">
	<ul>
		<li><a href="#tabs-1">General</a></li>
		<li><a href="#tabs-2">Facilities</a></li>
		<li><a href="#tabs-3">Aenean lacinia</a></li>
	</ul>
	<div id="tabs-1">
		<s:if test="contractor.dbaName.length() > 0">
			<p class="fn org">DBA <s:property value="contractor.dbaName" /></p>
		</s:if>
		<label>Address:</label>
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
			<li>sdf
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
		<p>Mauris eleifend est et turpis. Duis id erat. Suspendisse potenti. Aliquam vulputate, pede vel vehicula accumsan, mi neque rutrum erat, eu congue orci lorem eget lorem. Vestibulum non ante. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Fusce sodales. Quisque eu urna vel enim commodo pellentesque. Praesent eu risus hendrerit ligula tempus pretium. Curabitur lorem enim, pretium nec, feugiat nec, luctus a, lacus.</p>
		<p>Duis cursus. Maecenas ligula eros, blandit nec, pharetra at, semper at, magna. Nullam ac lacus. Nulla facilisi. Praesent viverra justo vitae neque. Praesent blandit adipiscing velit. Suspendisse potenti. Donec mattis, pede vel pharetra blandit, magna ligula faucibus eros, id euismod lacus dolor eget odio. Nam scelerisque. Donec non libero sed nulla mattis commodo. Ut sagittis. Donec nisi lectus, feugiat porttitor, tempor ac, tempor vitae, pede. Aenean vehicula velit eu tellus interdum rutrum. Maecenas commodo. Pellentesque nec elit. Fusce in lacus. Vivamus a libero vitae lectus hendrerit hendrerit.</p>
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

</div>
</body>
</html>
