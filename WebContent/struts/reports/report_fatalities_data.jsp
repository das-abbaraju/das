<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportFatalities');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<table class="report">
	<thead>
		<tr>
			<td></td>
			<th><a href="?orderBy=a.name">Contractor</a></th>
			<s:if test="permissions.operator">
				<td><a href="?orderBy=flag DESC">Flag</a></td>
			</s:if>
			<td><a href="?orderBy=ca.auditFor DESC">For</a></td>
			<td><a href="?orderBy=os.SHAType DESC">SHAType</a></td>
			<td>Fatalities</td>
			<td>Verified</td>
			<s:if test="showContact">
				<td>Primary Contact</td>
				<td>Phone</td>
				<td>Email</td>
				<td>Office Address</td>
				<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
				<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
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
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<s:if test="permissions.operator">
				<td class="center"><a
					href="ContractorFlag.action?id=<s:property value="[0].get('id')"/>"
					title="<s:property value="[0].get('flag')"/> - Click to view details"><img
					src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif"
					width="12" height="15" border="0"></a></td>
			</s:if>
			<td class="center"><s:property value="get('auditFor')" /></td>
			<td><s:property value="get('SHAType')"/></td>
			<td class="center"><s:property value="get('fatalities')" /></td>
			<td><s:if test="get('verifiedDate') != null">
		    	<span class="verified" style="font-size: 16px;"></span></s:if>
		    </td>
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
<br>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
