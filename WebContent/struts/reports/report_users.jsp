<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>User Search</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>User Search</h1>

<s:include value="userfilters.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Name</td>
		<td>Contact Name</td>
		<td>dateCreated</td>
		<td>Last Login</td>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><s:if test="[0].get('tableType') == 'Acct'">
					<a href="ContractorView.action?id=<s:property value="[0].get('accountID')"/>">
					<s:property value="[0].get('companyName')" /></a>
				</s:if>
				<s:else>
					<a href="UsersManage.action?accountId=<s:property value="[0].get('accountID')"/>">
					<s:property value="[0].get('companyName')" /></a>	
				</s:else>
			</td>
			<td><s:property value="[0].get('name')" /></td>
			<td><s:date name="[0].get('dateCreated')" format="MM/dd/yyyy"/></td>
			<td><s:date name="[0].get('lastLogin')" format="MM/dd/yyyy hh:mm" /></td>
		</tr>
	</s:iterator>

</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
