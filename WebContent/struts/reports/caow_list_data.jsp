<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<pics:permission perm="ContractorDetails">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="javascript.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportCAOByStatusList');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</pics:permission>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="#" onclick="changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <td><a href="#">Document Type</a></td>
	    <pics:permission perm="AllOperators">
		    <td><a href="#" onclick="changeOrderBy('form1','caoAccount.name');" >Scope</a></td>
	    </pics:permission>
	    <td><a href="#" onclick="changeOrderBy('form1','caow.status');" >Status Changed</a></td>
	    <td><a href="#" onclick="changeOrderBy('form1','cao.statusChangedDate DESC');" >Date</a></td>
	    <td><a href="#" onclick="changeOrderBy('form1','ca.expiresDate DESC');" >Expired Date</a></td>
	    <s:if test="permissions.picsEmployee">
		    <td><a href="#" onclick="changeOrderBy('form1','auditor.name');" >Safety Pro</a></td>
	    </s:if>
		<s:if test="permissions.accountName.startsWith('Roseburg')">
			<td>Score</td>
		</s:if>
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Email</td>
			<td>Office Address</td>
			<td><a href="#" onclick="changeOrderBy('form1','a.city,a.name');">City</a></td>
			<td><a href="#" onclick="changeOrderBy('form1','a.state,a.name');">State</a></td>
			<td>Zip</td>
			<td>Web_URL</td>
		</s:if>
		<s:if test="showTrade">
			<td>Trade</td>
		</s:if>
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text name="%{get('atype.name')}" /> <s:property value="get('auditFor')"/></a></td>
	    <pics:permission perm="AllOperators">
			<td><s:property value="get('caoAccountName')" /></td>
		</pics:permission>
		<td><s:property value="get('caowStatus')"/></td>
		<td class="center"><s:date name="get('statusChangedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
	    <s:if test="permissions.picsEmployee">
			<td><s:property value="get('auditor_name')"/></td>
		</s:if>
		<s:if test="permissions.accountName.startsWith('Roseburg')">
			<td><s:if test="get('scoreable') == 1"><s:property value="get('auditScore')"/></s:if><s:else>N/A</s:else></td>
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
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
