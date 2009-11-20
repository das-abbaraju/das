<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Operator Flag Criteria</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Operator Flag Criteria</h1>
<s:include value="filters.jsp" />

<pics:permission perm="ContractorDetails">
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportOperatorCriteria');" 
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
		<td></td>
		<td colspan="2">Contractor Name</td>
		<td>Flag</td>
		<td>Risk Level</td>
		<s:iterator value="operatorAccount.visibleAudits">
			<s:if test="minRiskLevel > 0">
				<s:if test="auditType.annualAddendum">
					<td><s:property value="auditType.auditName"/> 08 Status</td>
					<td><s:property value="auditType.auditName"/> 07 Status</td>
					<td><s:property value="auditType.auditName"/> 06 Status</td>
					<td><s:property value="auditType.auditName"/> 05 Status</td>
				</s:if>
				<s:else>
					<td><s:property value="auditType.auditName"/> Status</td>
				</s:else>
			</s:if>
		</s:iterator>
		<s:iterator value="operatorAccount.inheritFlagCriteria.flagQuestionCriteria">
			<s:if test="flagColor.toString().equals(filter.flagStatus[0]) && checked">
				<s:if test="!auditQuestion.auditType.classType.policy">
					<s:if test="auditQuestion.id == 2034">
						<s:if test="multiYearScope.description.equals('All Three Years')">
							<td>EMR 08</td>
							<td>EMR 07</td>
							<td>EMR 06</td>
							<td>EMR 05</td>
						</s:if>	
						<s:elseif test="multiYearScope.description.equals('Last Year Only')">
							<td><s:if test="%{get('answer2008')} != null">
								EMR 08</s:if>
								<s:else>EMR 07</s:else>
							</td>
						</s:elseif>
						<s:elseif test="multiYearScope.description.equals('Three Year Average')">
							<td>EMR AVG</td>
						</s:elseif>
					</s:if>
					<s:else>
						<td><s:property value="auditQuestion.columnHeader"/></td>
					</s:else>
				</s:if>	
			</s:if>
		</s:iterator>
		<s:if test="hasFatalities">
			<td>Fatalities '08</td>
			<td>Fatalities '07</td>
			<td>Fatalities '06</td>
			<td>Fatalities '05</td>
		</s:if>
		<s:if test="hasTrir">
			<td>TRIR '08</td>
			<td>TRIR '07</td>
			<td>TRIR '06</td>
			<td>TRIR '05</td>
		</s:if>
		<s:if test="hasTrirAvg">
			<td>TRIR AVG</td>
		</s:if>
		<s:if test="hasLwcr">
			<td>LWCR '08</td>
			<td>LWCR '07</td>
			<td>LWCR '06</td>
			<td>LWCR '05</td>
		</s:if>
		<s:if test="hasLwcrAvg">
			<td>LWCR AVG</td>
		</s:if>
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
			<td class="right">
				<s:property value="#stat.index + report.firstRowNumber" />
			</td>
			<td colspan="2"><nobr><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>">
				<s:property value="[0].get('name')"/></a></nobr>
			</td>
			<td class="center">
				<a href="ContractorFlag.action?id=<s:property value="get('id')"/>&opID=<s:property value="operatorID"/>" title="Click to view Flag Color details">
				<img src="images/icon_<s:property value="[0].get('lflag')"/>Flag.gif" width="12" height="15" border="0"></a>
			</td>
			<td class="center">
				<s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('riskLevel'))" />
			</td>
			<s:iterator value="operatorAccount.visibleAudits">		
				<s:if test="minRiskLevel > 0">
					<s:if test="auditType.annualAddendum">
						<td><span title="Completed - <s:property value="%{get('annual2008 Completed')}"/>%"><s:property value="%{get('annual2008 Status')}"/></span></td>
						<td><span title="Completed - <s:property value="%{get('annual2007 Completed')}"/>%"><s:property value="%{get('annual2007 Status')}"/></span></td>
						<td><span title="Completed - <s:property value="%{get('annual2006 Completed')}"/>%"><s:property value="%{get('annual2006 Status')}"/></span></td>
						<td><span title="Completed - <s:property value="%{get('annual2005 Completed')}"/>%"><s:property value="%{get('annual2005 Status')}"/></span></td>
					</s:if>
					<s:else>
						<td><span title="Completed - <s:property value="%{get(auditType.auditName + ' Completed')}"/>%"><s:property value="%{get(auditType.auditName + ' Status')}"/></span></td>
					</s:else>
				</s:if>
			</s:iterator>
			<s:iterator value="operatorAccount.inheritFlagCriteria.flagQuestionCriteria">
				<s:if test="flagColor.toString().equals(filter.flagStatus[0]) && checked">
					<s:if test="!auditQuestion.auditType.classType.policy">
						<s:if test="auditQuestion.id == 2034">
							<s:if test="multiYearScope.description.equals('All Three Years')">
								<td><s:property value="%{get('answer2008')}"/></td>
								<td><s:property value="%{get('answer2007')}"/></td>
								<td><s:property value="%{get('answer2006')}"/></td>
								<td><s:property value="%{get('answer2005')}"/></td>
							</s:if>	
							<s:elseif test="multiYearScope.description.equals('Last Year Only')">
								<td><s:if test="%{get('answer2008')} != null">
										<s:property value="%{get('answer2008')}"/>
									</s:if>
									<s:else>
										<s:property value="%{get('answer2007')}"/>
									</s:else>
								</td>
							</s:elseif>
							<s:elseif test="multiYearScope.description.equals('Three Year Average')">
								<td><s:property value="%{get('emrAverage')}"/></td>				
							</s:elseif>
						</s:if>
						<s:else>
							<td><s:property value="%{get('answer' + auditQuestion.id)}"/></td>
						</s:else>
					</s:if>
				</s:if>
			</s:iterator>
			<s:if test="hasFatalities">
				<td><s:property value="get('fatalities2008')"/></td>
				<td><s:property value="get('fatalities2007')"/></td>
				<td><s:property value="get('fatalities2006')"/></td>
				<td><s:property value="get('fatalities2005')"/></td>
			</s:if>
			<s:if test="hasTrir">
				<td><s:property value="get('trir2008')"/></td>
				<td><s:property value="get('trir2007')"/></td>
				<td><s:property value="get('trir2006')"/></td>
				<td><s:property value="get('trir2005')"/></td>
			</s:if>
			<s:if test="hasTrirAvg">
				<td><s:property value="get('trirAverage')"/></td>
			</s:if>
			<s:if test="hasLwcr">
				<td><s:property value="get('lwcr2008')"/></td>
				<td><s:property value="get('lwcr2007')"/></td>
				<td><s:property value="get('lwcr2006')"/></td>
				<td><s:property value="get('lwcr2005')"/></td>
			</s:if>
			<s:if test="hasLwcrAvg">
				<td><s:property value="get('lwcrAverage')"/></td>
			</s:if>
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

<div class="info">This report may contain the same contractor more than once if they  
	have duplicate audits or insurance certificates. This usually occurs when the contractor 
	has an existing audit that about to expire along with an audit that is nearly complete.</div>

</body>
</html>
