<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="ReportContractorOperatorFlagMatrix.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:include value="filters.jsp" />
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportContractorOperatorFlagMatrix');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</s:if>
</pics:permission>
<div><s:text name="ReportContractorOperatorFlagMatrix.NumberOfContractors"><s:param value="%{reportData.size()}" /></s:text></div>
<s:form id="contractorOperatorFlagMatrix" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<th>&nbsp;</th>
				<s:iterator value="operatorList">
					<th><s:property value="name"/></th>
				</s:iterator>
			</tr>
		</thead>
		<s:iterator value="reportData.keySet()">
			<s:set name="thisContractor" value="top"/>
			<tr>
				<th><s:property value="#attr.thisContractor.name"/></th>
				<s:iterator value="operatorList">
					<s:set name="thisFlag" value="reportData.get(#attr.thisContractor).get(top)"/>
					<td class="center">
						<s:if test="#attr.thisFlag != null">
							<s:url id="flagUrl" action="ContractorFlag">
								<s:param name="id" value="%{#attr.thisContractor.id}"/>
								<s:param name="opID" value="%{top.id}"/>
							</s:url>
							<s:a href="%{flagUrl}"><img src="images/icon_<s:property value="#attr.thisFlag.toLowerCase()"/>Flag.gif" width="12" height="15"></s:a>
						</s:if>
					</td>
				</s:iterator>
			</tr>
		</s:iterator>
	</table>
</s:form>
</body>
</html>
				