<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Contractor Operator Flag</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:include value="filters.jsp" />
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="javascript.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportContractorOperatorFlag');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</s:if>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<td>Contractor</td>
			<td>Operator</td>
			<td><s:text name="global.Flag" /></td>
			<s:if test="operatorAccount.approvesRelationships.isTrue()">
				<pics:permission perm="ViewUnApproved">
					<td><nobr>Approved</nobr></td>
				</pics:permission>
			</s:if>
			<s:if test="showContact">
				<td><s:text name="global.ContactPrimary" /></td>
				<td><s:text name="User.phone" /></td>
				<td><s:text name="User.email" /></td>
				<td><s:text name="global.OfficeAddress" /></td>
				<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
				<td><a href="javascript: changeOrderBy('form1','a.state,a.name');"><s:text name="State" /></a></td>
				<td><s:text name="global.ZipPostalCode" /></td>
				<td><s:text name="ContractorAccount.webUrl" /></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:text name="Trade" /></td>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><a
					href="ContractorView.action?id=<s:property value="get('id')"/>"
					><s:property value="get('name')" /></a>
				</td>
				<td><s:property value="get('opName')"/></td>
				<td class="center">
					<a href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>" 
						title="<s:property value="get('flag')"/> - Click to view details"><img 
						src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
				</td>
				<s:if test="operatorAccount.approvesRelationships.isTrue()">
					<pics:permission perm="ViewUnApproved">
						<td align="center">&nbsp;&nbsp;&nbsp;&nbsp;<s:property
							value="get('workStatus')" />
						</td>
					</pics:permission>
				</s:if>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
				</s:if>
			</tr>			
		</s:iterator>
	</tbody>		
</table>
</body>
</html>	