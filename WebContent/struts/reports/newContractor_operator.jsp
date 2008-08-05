<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Search for New Contractors</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>Search for New Contractors</h1>
<s:include value="../actionMessages.jsp" />
<s:include value="filters.jsp" />
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
	    <td>Address</td>
	    <td>Contact</td>
	    <td>Phone</td>
		<s:if test="permissions.operator">
			<td><a href="?orderBy=flag DESC">Flag</a></td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
		</s:if>
		<td>Action</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="[0].get('id')"/>"
				><s:property value="[0].get('name')" /></a></td>
			<td><s:property value="[0].get('city')"/>, <s:property value="[0].get('state')"/></td>
			<td><s:property value="[0].get('contact')"/></td>
			<td><s:property value="[0].get('phone')"/><br />
			<s:property value="[0].get('phone2')"/></td>
			<s:if test="permissions.operator">
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>" 
						title="<s:property value="[0].get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
				<s:if test="operatorAccount.approvesRelationships">
					<pics:permission perm="ViewUnApproved">
						<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
							value="[0].get('workStatus')" />
						</td>
					</pics:permission>
				</s:if>
			</s:if>
			<td><s:property value="genID"/>  </td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

</body>
</html>
