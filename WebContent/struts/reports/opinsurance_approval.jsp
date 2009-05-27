<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Insurance Policy Approval</title>
<s:include value="reportHeader.jsp" />

<SCRIPT type="text/javascript">
	function setAllChecked(elm) {
		$$('.massCheckable').each( function(ele) {
			ele.checked = elm.checked;
		});
		return false;
	}
</SCRIPT>
</head>
<body>
<h1><s:property value="reportName"/></h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>

<div class="buttons">
	<a class="button" href="ReportInsuranceApproval.action?filter.caoStatus=Verified&filter.recommendedFlag=Green"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Green.bigIcon" escape="false"/>Show Policies to Approve</a>
	<a class="button" href="ReportInsuranceApproval.action?filter.caoStatus=Verified&filter.recommendedFlag=Red"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/>Show Policies to Reject</a>
</div>
<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<s:include value="../actionMessages.jsp"/>
<br/><br/>
		<table class="report">
		<thead>
			<tr>
				<td>Select</td>
				<td title="PICS Recommendation" style="cursor: help;"></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td><a href="javascript: changeOrderBy('form1','atype.auditName');">Policy</a></td>
				<s:if test="requiresActivePolicy">	
					<td>Status</td>
				</s:if>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
				<td>Limits</td>
				<td title="Additional Requirements">Add'l</td>
				<td>Cert</td>
				<td>Admin Notes</td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td style="text-align: center;">
					<input type="checkbox" onclick="javascript: return syncSelects()" class="massCheckable" name="caoids" value="<s:property value="get('caoId')"/>"/>
				</td>
				<td style="text-align: center;" >
					<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('caoRecommendedFlag').toString())" escape="false"/>
				</td>
				<td>
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td>
					<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a> 
				</td>
				<s:if test="requiresActivePolicy">
					<td><s:property value="get('auditStatus')"/></td>
				</s:if>
				<td class="reportDate"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'Limits')">
						<nobr><s:property value="getFormattedDollarAmount(answer)"/> = <span style="font-size: 9px;"><s:property value="question.columnHeader"/></span></nobr>
						<br/>
					</s:iterator>
				</td>
				<td class="center">
					<s:if test="get('valid') == 1">Yes</s:if>
					<s:else>No</s:else>
					<s:if test="get('reason') != null">
						<br><img src="images/icon_notes.gif" title="<s:property value="get('reason')"/>">
					</s:if>
				</td>
				<td>
					<s:if test="get('certificateID') != null">
						<a href="CertificateUpload.action?id=<s:property value="get('id')"/>&certID=<s:property value="get('certificateID')"/>&button=download"
						target="_BLANK"><img src="images/icon_insurance.gif" /></a>	
					</s:if>
					<s:else></s:else>
				</td>
				<td>
					<a id="show_<s:property value="get('caoId')"/>Text" href="#" class="edit" 
						onclick="$('approveInsuranceForm_caos_<s:property value="get('caoId')"/>__notes').show(); $('show_<s:property value="get('caoId')"/>Text').hide(); return false;"
						title="<s:property value="get('caoNotes')"/>">
					<s:if test="get('caoNotes') != null">
						<s:property value="@com.picsauditing.util.Strings@trim(get('caoNotes'),30)"/>
					</s:if>
					<s:else>...</s:else>
					</a>
					<s:textarea rows="4" cols="20" name="caos[%{get('caoId')}].notes" value="%{get('caoNotes')}" cssStyle="display: none;"/>
				</td>
			</tr>
		</s:iterator>
		<tr>
				<td style="text-align: center;">
					<center><input title="Check all" type="checkbox" onclick="setAllChecked(this);"/><br/>Select<br/>All</center>
				</td>
				<td colspan="10" style="text-align: left;">
										<s:radio cssClass="statusSelects" name="newStatuses" list="#{'Approved':'Approve Selected','Rejected':'Reject Selected','NotApplicable':'Mark as N/A'}"/>
					<input type="submit" onclick="javascript: return saveRows('approveInsuranceForm');" value="Update"/>		
				</td>
		</tr>
	</table>
	
</s:form>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>
				