<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="report.allRows == 0">
	<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
</s:if>
<s:else>
<div class="right"><a 
	class="excel" 
	<s:if test="report.allRows > 500">onclick="return confirm('<s:text name="JS.ConfirmDownloadAllRows"><s:param value="%{report.allRows}" /></s:text>');"</s:if> 
	href="javascript: download('ReportWcbAccounts');" 
	title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>"
	><s:text name="global.Download" /></a></div>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<td><a href="?orderBy=wcbAccountNumber ASC">WCB Account Number</a></td>
			<td><a href="?orderBy=name ASC">Contractor Name</a></td>
			<td><a href="?orderBy=id ASC">PICS Account Number</a></td>
			<td><a href="?orderBy=province ASC">WCB Province</a></td>
			<td><a href="?orderBy=creationDate ASC">Date</a></td>
		</tr>
	</thead>
	<s:iterator value="data">
		<tr>
			<td><s:property value="get('wcbAccountNumber')"/></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" title="<s:property value="get('name')"/>">
				<s:property value="get('name')"/>
			</td>
			<td><s:property value="get('id')"/></td>
			<td><s:property value="get('province')"/></td>
			<td><s:date name="get('creationDate')" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/></td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
