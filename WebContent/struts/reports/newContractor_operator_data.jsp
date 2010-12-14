<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="actionErrors.size > 0">
	<s:include value="../actionMessages.jsp" />
</s:if>
<s:elseif test="report.allRows == 0">
	<div class="alert">Sorry, no companies found matching your search criteria.
Try broadening your search criteria or submit a <a href="RequestNewContractor.action">new Registration Request</a> and PICS will contact that company for you.</div>
	<pics:permission perm="RequestNewContractor" type="Edit">
		<div class="info">
			Can't find the company you need? Submit a <a href="RequestNewContractor.action">New Contractor Request</a> and PICS will help that company register.
		</div>
	</pics:permission>
</s:elseif>
<s:else>
<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('NewContractorSearch');" 
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
		<td>#</td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor Name</a></td>
		<s:if test="permissions.operator">
			<td style="white-space: nowrap">
				<a href="#" class="cluetip help" title="Preflag" rel="#watchtip"></a> PreFlag
				<div id="watchtip">
					The PreFlag represents the evaluation using minimum requirements that the Contractor has already completed. Factors such as PQF, safety stats, and insurance limits are included if applicable. Custom criteria such as site-specific audits are not included in the pre-flag calculation.
				</div>
			</td>
			<s:if test="operatorAccount.approvesRelationships">
				<pics:permission perm="ViewUnApproved">
					<td>Status</td>
				</pics:permission>
			</s:if>
		</s:if>
		<s:if test="permissions.accountName.startsWith('Roseburg') || permissions.accountName.startsWith('Ashland')">
			<td>PICS Score</td>
		</s:if>
		<td>Action</td>
		<s:if test="showContact">
			<td>Primary Contact</td>
			<td>Phone</td>
			<td>Email</td>
			<td>Primary Address</td>
			<td><a href="javascript: changeOrderBy('form1','a.country, a.state, a.city, a.name');">City, State</a></td>
		</s:if>
		<s:if test="showTrade">
			<td>Primary Trade</td>
		</s:if>
		<td>Insurance</td>
	</tr>
	</thead>
	<tbody>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
				<td><s:property value="get('name')" />
					<s:if test="get('dbaName') > '' && get('name') != get('dbaName')"><br />DBA: <s:property value="get('dbaName')" /></s:if>
				</td>
				<s:if test="permissions.operator">
					<td class="center">
						<s:if test="worksForOperator(get('id'))">
							<img src="images/icon_<s:property value="get('lflag')"/>Flag.gif" width="12" height="15" border="0" />
						</s:if>
						<s:else>
							<img width="12" height="15" border="0"
								src="images/icon_<s:property value="getOverallFlag(get('id')).toString().toLowerCase()"/>Flag.gif" />
						</s:else>
					</td>
					<s:if test="operatorAccount.approvesRelationships">
						<pics:permission perm="ViewUnApproved">
							<td><s:property value="get('workStatus')"/></td>
						</pics:permission>
					</s:if>
				</s:if>
				<s:if test="permissions.accountName.startsWith('Roseburg') || permissions.accountName.startsWith('Ashland')">
					<td><s:property value="get('score')"/></td>
				</s:if>	
				<td class="center">
					<s:if test="get('genID') > 0">
						<a href="ContractorView.action?id=<s:property value="get('id')"/>">View</a>
						<pics:permission perm="RemoveContractors">
							<s:if test="permissions.corporate">
								<a class="remove" href="ContractorFacilities.action?id=<s:property value="get('id')"/>">Remove</a>
							</s:if>
							<s:else>
								<a class="remove" href="?button=remove&id=<s:property value="get('id')"/>">Remove</a>
							</s:else>
						</pics:permission>
					</s:if>
					<s:else>
						<pics:permission perm="AddContractors">
							<s:if test="permissions.corporate">
								<a class="add" href="ContractorFacilities.action?id=<s:property value="get('id')"/>">Add</a>
							</s:if>
							<s:else>
								<a class="add" href="?button=add&id=<s:property value="get('id')"/>">Add</a>
							</s:else>
						</pics:permission>
					</s:else>
				</td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/>, <s:property value="get('state')"/>
					<s:if test="get('state') == ''">
						<s:property value="get('country')"/>
					</s:if>
					</td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
				</s:if>
					<td>
						<s:if test="get('answer2074') != null">
							<span style="font-size: 9px;">GL Each Occurrence = <s:property value="getFormattedDollarAmount(get('answer2074'))"/></span> <br/>
						</s:if>
						<s:if test="get('answer2079') != null">
							<span style="font-size: 9px;">GL General Aggregate = <s:property value="getFormattedDollarAmount(get('answer2079'))"/></span> <br/>
						</s:if>
						<s:if test="get('answer2155') != null">
							<span style="font-size: 9px;">AL Combined Single = <s:property value="getFormattedDollarAmount(get('answer2155'))"/></span> <br/>
						</s:if>
						<s:if test="get('answer2149') != null">
							<span style="font-size: 9px;">WC Each Accident = <s:property value="getFormattedDollarAmount(get('answer2149'))"/></span> <br/>
						</s:if>
						<s:if test="get('answer2161') != null">
							<span style="font-size: 9px;">EX Each Occurrence = <s:property value="getFormattedDollarAmount(get('answer2161'))"/></span>
						</s:if>
					</td>
			</tr>
		</s:iterator>
	</tbody>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<div class="info">
	Is the company you need not listed? Submit a <a href="RequestNewContractor.action">New Registration Request</a> and PICS will contact that company for you.
</div>
</s:else>
