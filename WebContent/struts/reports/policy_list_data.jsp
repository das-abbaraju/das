<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="report.allRows == 0">
	<div id="alert">No rows found matching the given criteria. Please try again.</div>
</s:if>
<s:else>
<s:if test="!filter.allowMailMerge">
	<div class="right"><a 
		class="excel" 
		<s:if test="report.allRows > 500">onclick="return confirm('Are you sure you want to download all <s:property value="report.allRows"/> rows? This may take a while.');"</s:if> 
		href="javascript: download('ReportPolicyList');" 
		title="Download all <s:property value="report.allRows"/> results to a CSV file"
		>Download</a></div>
</s:if>

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="policyList" method="post" cssClass="forms">
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <td><a href="javascript: changeOrderBy('form1','atype.auditName');" >Type</a></td>
	    <s:if test="permissions.operator || permissions.corporate">
		    <td>Approval Status</td>
	    </s:if>
	    <td><a href="javascript: changeOrderBy('form1','ca.creationDate DESC');" >Created</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.completedDate DESC');" >Submitted</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.closedDate DESC');" >Closed</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.expiresDate DESC');" >Expired</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.auditStatus DESC');" >Status</a></td>
	    <td><a href="javascript: changeOrderBy('form1','ca.percentComplete');" >Comp%</a></td>
	    <td>File</td>
	    
	</tr>
	</thead>
	<s:iterator value="data" status="stat">
	<tr>
		<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
		<td><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('auditName')"/> <s:property value="get('auditFor')"/></a></td>
	    <s:if test="permissions.operator || permissions.corporate">
		    <td><s:property value="get('CaoStatus')"/></td>
	    </s:if>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('completedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('closedDate')" format="M/d/yy" /></td>
		<td class="center"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
		<td><s:property value="get('auditStatus')"/></td>
		<td class="right"><s:property value="get('percentComplete')"/>%</td>
		<td>
		
				<s:set name="aiFiles" value="getDataForAudit(get('auditID'),'policyFile')"/>
				<s:if test="( #aiFiles != null ) && (#aiFiles.size() > 0 )">
					<s:iterator value="#aiFiles">
						<s:set name="currentFile" value="top"/>
						<s:iterator value="getDataForAudit(get('auditID'),'aiName')">
							<s:set name="nameQuestion" value="top"/>
							<s:if test="#attr.nameQuestion != null">
								<s:if test="#attr.currentFile.parentAnswer.id == #nameQuestion.id">
									<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="#attr.currentFile.id"/>" target="_BLANK">
										<s:if test="#nameQuestion.answer == 'All'">
											<img src="images/icon_DA.gif"/>
										</s:if>
										<s:else>
											<img src="images/icon_insurance.gif"/>
										</s:else>
									</a> 
								</s:if>
							</s:if>
							<s:else>
								<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="#attr.currentFile.id"/>" target="_BLANK"><img src="images/icon_insurance.gif"/></a><br/>
							</s:else>
						</s:iterator>
						
					</s:iterator>
				</s:if>
		
		
		</td>
	</tr>
	</s:iterator>
</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:else>
