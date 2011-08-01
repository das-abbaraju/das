<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Forced Flags</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Forced Flags</h1>

<s:include value="filters.jsp" />

<s:form id="form1"> 
	<s:hidden name="filter.ajax" value="false" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>
<pics:permission perm="ForcedFlagsReport">
	<div class="right"><a class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
		href="javascript: download('ReportConForcedFlags');" 
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
		><s:text name="global.Download" /></a></div>
</pics:permission>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<td colspan="2"><a href="?orderBy=a.name" ><s:text name="global.ContractorName" /></a></td>
			<s:if test="!permissions.operator">
				<td><a href="?orderBy=o.name" >Operator Name</a></td>
			</s:if>
			<td><a href="?orderBy=flag" ><s:text name="global.Flag" /></a></td>
			<td><a href="?orderBy=fLabel" >Flag Issue</a></td>
			<td><a href="?orderBy=u.name" >ForcedBy</a></td>
			<td><a href="?orderBy=ff.forceBegin" >Start Date</a></td>
			<td><a href="?orderBy=ff.forceend" >End Date</a></td>
			<td><a href="?orderBy=u.id" >Notes</a></td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>">
			<s:property value="get('name')" /></a></td>
			<s:if test="!permissions.operator">
				<td><a><s:property value="get('opName')" /></a></td>
			</s:if>
			<td class="center">
				<s:if test="get('opType').toString() == 'Operator'">
				<a
				href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="get('opId')"/>"
				title="Click to view Flag Color details"> <img
				src="images/icon_<s:property value="get('flag')"/>Flag.gif"
				width="12" height="15" border="0"></a>
				</s:if>
				<s:else>
					<img src="images/icon_<s:property value="get('flag')"/>Flag.gif"
					width="12" height="15" border="0">
				</s:else>
			</td>
			<td class="center"><s:property value="get('fLabel')" /></td>
			<td title="<s:property value="get('forcedByAccount')"/>"><s:property value="get('forcedBy')"/></td>
			<td><s:date name="get('forceBegin')" format="MMM d, yyyy" /></td>
			<td><s:date name="get('forceend')" format="MMM d, yyyy" /></td>
			<td>
				<s:if test="get('forcedById') != null">
					<a href="ContractorNotes.action?id=<s:property value="get('id')"/>&filter.userID=<s:property value="get('forcedById')"/>&filter.category=Flags&filter.keyword=Forced">Notes</a>
				</s:if>
			</td>
		</tr>
	</s:iterator>
</table>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>

</body>
</html>
