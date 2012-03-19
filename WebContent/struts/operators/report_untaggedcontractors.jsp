<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ReportUntaggedContractors.title" /></title>
<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
<h1><s:text name="ReportUntaggedContractors.title" /></h1>
<s:include value="../reports/filters.jsp" />

<pics:permission perm="ContractorDetails">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportUntaggedContractors');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</pics:permission>

<div id="report_data">
<s:if test="report.allRows == 0">
	<div class="info"><s:text name="ReportUntaggedContractors.NoContractorMissingTags" /></div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinks" escape="false" />
</div>
<s:form>
<s:hidden name="operator" />
<s:set name="columnsDisplayed" value="3" />
<table class="report">
	<thead>
	<tr>
		<th colspan="2"><s:text name="global.ContractorName" /></th>
		<s:if test="showTrade">
			<th><s:text name="Trade" /></th>
			<th><s:text name="ContractorAccount.tradesSelf" /></th>
			<th><s:text name="ContractorAccount.tradesSub" /></th>
			<s:set name="columnsDisplayed" value="6" />	
		</s:if>
		<th></th>
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
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
				<td><s:property value="get('tradesSelf')"/></td>
				<td><s:property value="get('tradesSub')"/></td>		
			</s:if>
			
			<td><s:checkbox name="contractors" fieldValue="%{get('id')}" />
		</tr>
	</s:iterator>
	<s:if test="data.size() > 0">
		<tr>
			<td colspan=<s:property value="#columnsDisplayed" /> class="right">
				<s:select list="operatorTags" listKey="id" listValue="%{tag + ' ('+ operator.name + ')' + (isRequired(id) ? '*' : '')}"
					headerValue="- %{getText('ReportUntaggedContractors.ContractorTags')} -" headerKey="0" name="tag" />
				<s:submit cssClass="picsbutton positive" method="save" value="%{getText('button.Save')}" />
				<br/>
				<s:text name="ReportUntaggedContractors.RequiredTag" />
			</td>
		</tr>
	</s:if>
	</tbody>
</table>
</s:form>
<div>
<s:property value="report.pageLinks" escape="false" />
</div>
</s:else>
</div>

</body>
</html>
