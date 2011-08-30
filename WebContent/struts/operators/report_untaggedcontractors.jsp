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
<table class="report">
	<thead>
	<tr>
		<th colspan="2"><s:text name="global.ContractorName" /></th>
		<th></th>
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
			<td><s:checkbox name="contractors" fieldValue="%{get('id')}" />
		</tr>
	</s:iterator>
	<s:if test="data.size() > 0">
		<tr>
			<td colspan="3" class="right">
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
