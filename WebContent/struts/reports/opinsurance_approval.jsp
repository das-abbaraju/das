<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Insurance Policy Approval</title>
<s:include value="reportHeader.jsp" />

<SCRIPT type="text/javascript">
	function setAllChecked( elm ) {
		var boxes = document.getElementsByClassName('massCheckable');
		
		for( var i = 0; i < boxes.length; i++  ) {
			var box = boxes[i];
			box.checked = elm.checked;
		}
		return false;
	}
	
	function saveRows( formName ) {
	
		var pars = $(formName).serialize();
		
		var myAjax = new Ajax.Updater('', 'ReportInsuranceApprovalSave.action', 
		{
			method: 'post', 
			parameters: pars,
			onException: function(request, exception) {
				alert(exception);
			},
			onSuccess: function(transport) {
				clickSearch('form1');
			}
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
<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<br/><br/>
		<table class="report">
		<thead>
			<tr>
				<td>Select</td>
				<td title="PICS Recommendation" style="cursor: help;">Compliant?</td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td><a href="javascript: changeOrderBy('form1','atype.auditName');">Policy</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
				<td>Limits</td>
				<td title="Waiver of Subrogation">Waiver</td>
				<td>Additional Insured</td>
				<td>Cert</td>
				<td>Notes</td>
				<td>Approval<br/>Status</td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td style="text-align: center;">
					<input type="checkbox" onclick="javascript: return syncSelects()" class="massCheckable" name="caoids" value="<s:property value="get('caoId')"/>"/>
				</td>
				<td style="text-align: center;" >
					<s:if test="( get('caoRecommendedStatus') == null ) || ( get('caoRecommendedStatus') == 'Awaiting' )"></s:if>
					<s:elseif test="get('caoRecommendedStatus') == 'Approved'">
						<img src="images/okCheck.gif" width="18" height="15" border="0" />
					</s:elseif>
					<s:else>
						<img src="images/notOkCheck.gif" width="18" height="15" border="0" />
					</s:else>
				</td>
				<td>
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td>
					<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a> 
				</td>
				<td class="reportDate"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'limits')">
						<s:property value="getFormattedDollarAmount(answer)"/> - <s:property value="question.question"/><br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'aiWaiverSub')">
						<s:property value="answer"/><br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'aiName')">
						<s:set name="nameQuestion" value="top"/>
						<s:property value="getAiNameOrSupercededName(#nameQuestion)"/></a><br/>						
					</s:iterator>
				</td>
				<td>
					<s:set name="aiFiles" value="getDataForAudit(get('auditID'),'policyFile')"/>
					<s:if test="( #aiFiles != null ) && (#aiFiles.size() > 0 )">
						<s:iterator value="#aiFiles">
							<s:if test="parentAnswer.id == #nameQuestion.id">
								<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK"><img src="images/icon_insurance.gif"/></a><br/>
							</s:if>
						</s:iterator>
					</s:if>
				</td>
				<td><s:textfield name="caos[%{get('caoId')}].notes" value="%{get('caoNotes')}"/></td>
				<td><s:property value="get('caoStatus')"/></td>
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
				