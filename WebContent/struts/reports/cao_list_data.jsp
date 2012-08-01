<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
	<div class="right">
		<a class="excel" <s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
			href="javascript: download('ReportCAOList');" title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
			<s:text name="global.Download" />
		</a>
	</div>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" ><s:text name="global.Contractor" /></a></th>
	    <td><s:text name="ReportCAOList.header.DocumentType" /></td>
	    <td><a href="javascript: changeOrderBy('form1','caoAccount.name');"><s:text name="Audit.header.OperatorScope" /></a></td>
	    <td><a href="javascript: changeOrderBy('form1','cao.status');" ><s:text name="global.Status" /></a></td>
	    <td><a href="javascript: changeOrderBy('form1','cao.statusChangedDate DESC');"><s:text name="global.Date" /></a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" ><s:text name="ReportCAOList.header.ExpiredDate" /></a></td>
	    <s:if test="permissions.picsEmployee">
		    <td><a href="javascript: changeOrderBy('form1','auditor.name');" ><s:text name="global.SafetyProfessional" /></a></td>
	    </s:if>
		<s:if test="permissions.accountName.startsWith('Roseburg')">
			<td><s:text name="ContractorAccount.score" /></td>
		</s:if>
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
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text name="%{get('atype.name')}" /> <s:property value="get('auditFor')"/></a></td>
		<td><s:property value="get('caoAccountName')" /></td>
		<td><s:text name="AuditStatus.%{get('auditStatus')}"/></td>
		<td class="center"><s:date name="get('statusChangedDate')" /></td>
		<td class="center"><s:date name="get('expiresDate')" /></td>
	    <s:if test="permissions.picsEmployee">
			<td><s:property value="get('auditor_name')"/></td>
		</s:if>
		<s:if test="permissions.accountName.startsWith('Roseburg')">
			<td><s:if test="get('scoreable') == 1"><s:property value="get('auditScore')"/></s:if><s:else><s:text name="global.NA" /></s:else></td>
		</s:if>
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
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
