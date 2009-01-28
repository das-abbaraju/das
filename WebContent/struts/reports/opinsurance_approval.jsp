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
	
	function syncSelects( elm ) {
		var objs = document.getElementsByClassName('statusSelects');
		
		for( var i = 0; i < objs.length; i++  ) {
			var current = objs[i];
			current.value = elm.value;
		}
		return false;
	}
</SCRIPT>
</head>
<body>
<h1>Insurance Policy Approval</h1>
<s:include value="filters.jsp" />
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="approveInsuranceForm" method="post" cssClass="forms">
<br/><br/>
	<table class="report">
		<thead>
			<tr>
				<td>Select All<br/><input title="Check all" type="checkbox" onclick="setAllChecked(this);" checked="checked"/></td>
				<td title="PICS Recommendation" style="cursor: help;">Recommend</td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Policy</a></td>
				<s:if test="requiresActivePolicy">	
					<td>Status</td>
				</s:if>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
				<td>Limits</td>
				<td title="Waiver of Subrogation">Waiver</td>
				<td>Additional Insured<br/>(Click For File)</td>
				<td>Notes</td>
				<td>Approval<br/>Status</td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td style="text-align: center;">
					<input type="checkbox" class="massCheckable" name="caoids" value="<s:property value="get('caoId')"/>"/>
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
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a> -
					<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a> 
				</td>
				<s:if test="requiresActivePolicy">
					<td><s:property value="get('auditStatus')"/></td>
				</s:if>
				<td class="reportDate"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'limits')">
						<s:property value="new java.text.DecimalFormat('$#,##0').format(new java.lang.Long(answer))"/> - <s:property value="question.question"/><br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'aiWaiverSub')">
						<s:property value="answer"/><br/>
					</s:iterator>
				</td>
				<td>
					<s:iterator value="getDataForAudit(get('auditID'),'aiName')">
						<s:iterator value="getDataForAudit(get('auditID'),'aiFile')">
							<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK"><s:property value="[1].answer"/></a><br/>
						</s:iterator>
					</s:iterator>
				</td>
				<td><s:textfield name="caos[%{get('caoId')}].notes" value="%{get('caoNotes')}"/></td>
				<td><s:property value="get('caoStatus')"/></td>
			</tr>
		</s:iterator>
		<tr>
				<td style="text-align: center;">
					<input title="Check all" type="checkbox" onclick="setAllChecked(this);" checked="checked"/>
				</td>
				<td colspan="8" style="text-align: left;">
					Set all statuses to: <s:select cssClass="statusSelects" onchange="javascript: return syncSelects(this);" name="newStatuses" list="#{'':'No Change','Approved':'Approved','Awaiting':'Awaiting','NotApplicable':'Not Applicable','Rejected':'Rejected'}"/>
					<input type="submit" onclick="javascript: return saveRows('approveInsuranceForm');" value="Update Selected"/>		
				</td>
		</tr>
	</table>
		
		
	</div>
</s:form>
<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
</body>
</html>
				