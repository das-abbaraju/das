<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportIncidenceRate');" 
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
			<th><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></th>
			<td>Location</td>
			<td>Type</td>
			<td>Rate</td>
			<s:if test="filter.shaType.toString().equals('COHS')">
				<td>Cad7</td>
				<td>Neer</td>
			</s:if>
			<td>Year</td>
		    <th><a href="javascript: changeOrderBy('form1','c.trirAverage');">Average</a></th>
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
				<td>Industry</td>			
			</s:if>
		</tr>
	</thead>
	<!--TODO Add in the Contractor FlagColor-->
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property
				value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property
				value="[0].get('name')" /></a></td>
			<td><s:if test="%{[0].get('location') == 'Corporate'}">
				<s:property value="[0].get('location')" />
			</s:if><s:else>
				<s:property
					value="%{[0].get('location')+'-'+[0].get('description')}" />
			</s:else></td>
			<td><s:property value="[0].get('SHAType')" /></td>
			<!--Need to fix this before the year end-->
			<td class="right">
				<s:property
					value="%{new java.text.DecimalFormat('#,##0.00').format(get('incidenceRate'))}" />
			</td>
			<s:if test="get('SHAType').toString().equals('COHS')">
				<td><s:property value="%{new java.text.DecimalFormat('#,##0.00').format(get('cad7'))}"/></td>
				<td><s:property value="%{new java.text.DecimalFormat('#,##0.00').format(get('neer'))}"/></td>
			</s:if>
			<td><s:property value="get('auditFor')" /></td>
			<td><s:property value="get('trirAverage')" /></td>
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
				<td><s:property value="get('industry')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>


<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
