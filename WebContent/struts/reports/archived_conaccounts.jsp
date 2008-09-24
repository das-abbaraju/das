<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Archived Contractor Accounts</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Archived Contractor Accounts</h1>

<s:include value="filters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<div class="helpOnRight">
These contractors have allowed their PICS membership to lapse. If you expect to do additional work
with any of these contractors, please encourage them to renew their membership by contacting PICS.
</div>

<table class="report" style="clear : none;">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<td>Contact</td>
		<td>Phone Number</td>
		<td>Email Address</td>
		<pics:permission perm="RemoveContractors">
		<s:if test="permissions.operator">
		<td>Remove</td>
		</s:if>
		</pics:permission>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:property value="get('name')" /></td>
			<td><s:property value="get('contact')" /></td>
			<td><s:property value="get('phone')" /><br/>
			<s:property value="get('phone2')" />
			</td>
			<td><s:property value="get('email')"/></td>
			<pics:permission perm="RemoveContractors">
				<s:if test="permissions.operator">
				<td>
				<s:form action="ArchivedContractorAccounts" method="POST">
					<s:submit value="Remove" name="button"/>
					<s:hidden value="%{get('id')}" name="conID"/>
				</s:form>
				</td>
				</s:if>
			</pics:permission>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
