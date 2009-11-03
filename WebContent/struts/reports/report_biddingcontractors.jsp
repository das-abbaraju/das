<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>BID-ONLY Contractor Accounts </title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>BID-ONLY Contractor Accounts List</h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:if test="report.allRows == 0">
	<div id="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2">Contractor Name</td>
		<s:if test="permissions.operator">
			<td><a
				href="?orderBy=flag DESC">Flag</a></td>
			<td>Waiting On</td>
		</s:if>
		<pics:permission perm="ViewTrialAccounts" type="Edit">
			<td>Notes</td>
			<td></td>
			<td></td>
		</pics:permission>
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
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"
				rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
				class="contractorQuick" title="<s:property value="get('name')" />"
				><s:property value="get('name')" /></a>
			</td>
			<s:if test="permissions.operator">
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="get('id')"/>" 
						title="<s:property value="get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
				<td><a href="ContractorFlag.action?id=<s:property value="get('id')"/>" ><s:property value="@com.picsauditing.jpa.entities.WaitingOn@valueOf(get('waitingOn'))"/></a></td>
			</s:if>
			<pics:permission perm="ViewTrialAccounts" type="Edit">
				<s:form action="BiddingContractorSearch" method="POST">
					<s:hidden value="%{get('id')}" name="conID"/>
					<td><s:textarea name="operatorNotes" cols="15" rows="4"/></td>
					<td><input type="submit" class="picsbutton positive" name="button" value="Upgrade"/></td>
					<td><input type="submit" class="picsbutton negative" name="button" value="Reject"/></td>
				</s:form>
			</pics:permission>
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
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
</div>

</body>
</html>
