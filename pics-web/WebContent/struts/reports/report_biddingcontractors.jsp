<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="BiddingContractorSearch.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="BiddingContractorSearch.title" /></h1>

<s:include value="filters.jsp" />

<div id="report_data">
<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td colspan="2"><s:text name="global.ContractorName" /></td>
		<s:if test="permissions.operator">
			<td><s:text name="WaitingOn" /></td>
		</s:if>
		<pics:permission perm="ViewTrialAccounts" type="Edit">
			<td><s:text name="global.Notes" /></td>
			<td></td>
			<td></td>
		</pics:permission>
		<s:if test="showContact">
			<td><s:text name="global.ContactPrimary" /></td>
			<td><s:text name="User.phone" /></td>
			<td><s:text name="User.email" /></td>
			<td><s:text name="global.OfficeAddress" /></td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
			<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
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
				rel="ContractorQuick.action?id=<s:property value="get('id')"/>" 
				class="contractorQuick" title="<s:property value="get('name')" />"
				><s:property value="get('name')" /></a>
			</td>
			<s:if test="permissions.operator">
				<td><a href="ContractorFlag.action?id=<s:property value="get('id')"/>" ><s:property value="@com.picsauditing.jpa.entities.WaitingOn@fromOrdinal(get('waitingOn'))"/></a></td>
			</s:if>
			<pics:permission perm="ViewTrialAccounts" type="Edit">
				<s:form action="BiddingContractorSearch" method="POST">
					<s:hidden value="%{get('id')}" name="contractor"/>
					<td><s:textarea name="operatorNotes" cols="15" rows="4"/></td>
					<td><s:submit cssClass="picsbutton positive" method="upgrade" value="%{getText('BiddingContractorSearch.button.Upgrade')}" /></td>
					<td><s:submit cssClass="picsbutton negative" method="reject" value="%{getText('button.Reject')}" /></td>
				</s:form>
			</pics:permission>
			<s:if test="showContact">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('countrySubdivision')"/></td>
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
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
</div>

</body>
</html>
