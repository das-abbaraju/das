<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportTWIC');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
	
<s:form id="form1">
	<s:hidden name="filter.ajax" value="false"/>
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table class="report">
	<thead>
		<tr>
		<td></td>
		<td>Employee Name</td>
		<td>Title</td>
		<td>Contractor</td>
		<td>TWIC Expiration</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.count" />.</td>
		<td><s:property value="get('lastName')" />, <s:property value="get('firstName')" /></td>
		<td><s:property value="get('title')" /></td>
		<td><s:property value="get('name')" /></td>
		<td>
			<s:if test="get('twicExpiration')!=null">
				<s:property value="get('twicExpiration')" />
			</s:if>
			<s:else>
				No TWIC listed
			</s:else>
		</td>
	</tr>
	</s:iterator>
</table>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>