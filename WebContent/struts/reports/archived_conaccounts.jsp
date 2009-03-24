<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Archived Contractor Accounts</title>
<s:include value="reportHeader.jsp" /></head>
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
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Phone2</td>
			<td>Email</td>
			<td>Office Address</td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
			<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
			<td>Zip</td>
			<td>Second Contact</td>
			<td>Second Phone</td>
			<td>Second Email</td>
			<td>Web_URL</td>
		</s:if>
		<s:if test="showTrade">
			<td>Trade</td>
			<td>Industry</td>			
		</s:if>
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
			<s:if test="showContact">
				<td><s:property value="get('contact')"/></td>
				<td><s:property value="get('phone')"/></td>
				<td><s:property value="get('phone2')"/></td>
				<td><s:property value="get('email')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('secondContact')"/></td>
				<td><s:property value="get('secondPhone')"/></td>
				<td><s:property value="get('secondEmail')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
				<td><s:property value="get('industry')"/></td>
			</s:if>
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
