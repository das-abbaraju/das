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
	<table class="report">
		<thead>
			<tr>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td>Policy Type</td>
				<td>Status</td>
				<td align="center"><a href="javascript: changeOrderBy('form1','createdDate ASC');">Effective</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
				<td>Limits</td>
				<td>Additional Insured</td>
				<td title="Waiver of Subrogation">Waiver</td>
				<td>File</td>
				<td>Notes</td>
				<td>PICS Suggests</td>
				<td></td>
			</tr>
		</thead>
		<tr>
				<td colspan="11" style="text-align: right;">
					Set all statuses to: <s:select cssClass="statusSelects" onchange="javascript: return syncSelects(this);" name="newStatuses" list="#{'':'No Change','Approved':'Approved','Missing':'Missing','Rejected':'Rejected'}"/>	
					<input type="submit" onclick="javascript: return saveRows('approveInsuranceForm');" value="Update Checked"/>		
				</td>
				<td style="text-align: center;">
					 Check all<br/>
					<input title="Check all" type="checkbox" onclick="setAllChecked(this);" checked="checked"/>
				</td>
		</tr>
		<s:iterator value="data" status="stat">
			<tr>
				<td style="font-size: smaller;"><a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td style="font-size: smaller;"><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td style="font-size: smaller;"><s:property value="get('caoStatus')"/></td>
				<td style="font-size: smaller;" class="reportDate"><s:date name="get('createdDate')" format="M/d/yy" /></td>
				<td style="font-size: smaller;" class="reportDate"><s:date name="get('expiresDate')" format="M/d/yy" /></td>
				<td class="limits" style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'limits')">
						<strong><s:property value="new java.text.DecimalFormat('$#,##0').format(new java.lang.Long(answer))"/></strong> - <s:property value="question.question"/><br/>
					</s:iterator>
				</td>
				<td style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiName')">
						<strong><s:property value="answer"/></strong><br/>
					</s:iterator>
				</td>
				<td style="font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiWaiverSub')">
						<strong><s:property value="answer"/></strong><br/>
					</s:iterator>
				</td>
				<td style="width: 45px; font-size: smaller;">
					<s:iterator value="getDataForAudit(get('auditID'),'aiFile')">
						<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" target="_BLANK">View File</a>
					</s:iterator>
				</td>
				<td style="width: 120px; font-size: smaller;" class="notes"><s:textarea name="caos[%{get('caoId')}].notes" value="%{get('caoNotes')}" rows="4" cols="20"/></td>
				<td style="font-size: smaller; text-align: center;" >
					<s:if test="( get('caoRecommendedStatus') == null ) || ( get('caoRecommendedStatus') == 'Missing' )"></s:if>
					<s:elseif test="get('caoRecommendedStatus') == 'Approved'">
						<img src="images/okCheck.gif" width="18" height="15" border="0" />
					</s:elseif>
					<s:else>
						<img src="images/notOkCheck.gif" width="18" height="15" border="0" />
					</s:else>
				</td>
				<td style="text-align: center;">
					<input type="checkbox" class="massCheckable" checked="checked" name="caoids" value="<s:property value="get('caoId')"/>"/>
				</td>
			</tr>
		</s:iterator>
		<tr>
				<td colspan="11" style="text-align: right;">
					Set all statuses to: <s:select cssClass="statusSelects" onchange="javascript: return syncSelects(this);" name="newStatuses" list="#{'':'No Change','Approved':'Approved','Missing':'Missing','Rejected':'Rejected'}"/>
					<input type="submit" onclick="javascript: return saveRows('approveInsuranceForm');" value="Update Checked"/>		
				</td>
				<td style="text-align: center;">
					 Check all<br/>
					<input title="Check all" type="checkbox" onclick="setAllChecked(this);" checked="checked"/>
				</td>
		</tr>
	</table>
		
		
	</div>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
				