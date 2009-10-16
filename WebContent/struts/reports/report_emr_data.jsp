<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportEmrRates');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>
</pics:permission>

<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
	    <th></th>
	    <th><a href="?orderBy=a.name" >Contractor</a></th>
	    <th>Year</th>
	    <th><a href="javascript: changeOrderBy('form1','d.answer');">Rate</a></th>
	    <th><a href="javascript: changeOrderBy('form1','c.emrAverage');">Average</a></th>
	    <td>Verified</td>
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Phone2</td>
			<td>Email</td>
			<td>Office Address</td>
			<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
			<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
			<td>Zip</td>
			<td>Second Contact</td>
			<td>Second Phone</td>
			<td>Second Email</td>
			<td>Web_URL</td>
		</s:if>
		<s:if test="showTrade">
			<td>Trade</td>
			<td>Industry</td>			
		</s:if>	    
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a></td>
			<td><s:property value="get('auditFor')"/></td>
			<td class="right"><s:property value="get('answer')"/></td>
			<td class="right"><s:property value="get('emrAverage')"/></td>
		    <td><s:if test="get('dateVerified') != null">
		    	<span class="verified" style="font-size: 16px;"></span></s:if>
		    </td>
			<s:if test="showContact">
				<td><s:property value="get('contact')"/></td>
				<td><s:property value="get('phone')"/></td>
				<td><s:property value="get('phone2')"/></td>
				<td><s:property value="get('email')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('zip')"/></td>
				<td><s:property value="get('secondContact')"/></td>
				<td><s:property value="get('secondPhone')"/></td>
				<td><s:property value="get('secondEmail')"/></td>
				<td><s:property value="get('web_URL')"/></td>
			</s:if>
			<s:if test="showTrade">
				<td><s:property value="get('main_trade')"/></td>
				<td><s:property value="get('industry')"/></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
	<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>