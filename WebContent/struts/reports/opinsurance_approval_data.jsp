<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="data.size > 0">
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>

<table class="report">
<thead>
	<tr>
		<td></td>
		<td title="PICS Recommendation" style="cursor: help;"></td>
		<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
		<td><a href="javascript: changeOrderBy('form1','atype.auditName');">Policy</a></td>
		<s:if test="requiresActivePolicy">	
			<td>Status</td>
		</s:if>
		<td align="center"><a href="javascript: changeOrderBy('form1','expiresDate ASC');">Expires</a></td>
		<td>Limits</td>
		<td>AMBest</td>
		<td title="Additional Requirements">Add'l</td>
		<td>Cert</td>
		<td>Admin Notes</td>
	</tr>
</thead>
<s:iterator value="data" status="stat">
	<tr>
		<td style="text-align: center;">
			<input id="cao_cb<s:property value="get('caoId')"/>" type="checkbox" class="massCheckable" name="caoids" value="<s:property value="get('caoId')"/>"/>
		</td>
		<td style="text-align: center;" >
			<s:property value="@com.picsauditing.jpa.entities.FlagColor@getSmallIcon(get('caoRecommendedFlag').toString())" escape="false"/>
		</td>
		<td>
			<a href="ContractorView.action?id=<s:property value="get('id')"/>"><s:property value="[0].get('name')"/></a>
		</td>
		<td>
			<a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>" title="<s:property value="[0].get('auditName')"/> for <s:property value="[0].get('caoName')"/>"><s:property value="[0].get('auditName')"/></a>
			<s:if test="permissions.corporate">
				<br><s:property value="get('caoOperatorName')"/>
			</s:if>
		</td>
		<s:if test="requiresActivePolicy">
			<td><s:property value="get('auditStatus')"/></td>
		</s:if>
		<td class="reportDate">
			<s:if test="get('auditName') == 'Workers Comp'">
				<s:iterator value="getDataForAudit(get('auditID'),'GoodStanding')">
					<s:if test='answer == "X"'>
						In Good Standing
						<br />
					</s:if>
				</s:iterator>
			</s:if>
			<s:date name="get('expiresDate')" format="M/d/yy" />
		</td>
		<td>
			<s:iterator value="getDataForAudit(get('auditID'),'Limits')">
				<nobr><s:property value="getFormattedDollarAmount(answer)"/> = <span style="font-size: 9px;"><s:property value="question.columnHeader"/></span></nobr>
				<br/>
			</s:iterator>
		</td>
		<td>
			<s:iterator value="getDataForAudit(get('auditID'),'AMBest')">
				<s:property value="getAMBestRatings(comment)" escape="false"/>
			</s:iterator>
		</td>
		<td class="center">
			<s:if test="get('valid').toString() == 'Yes'">Yes</s:if>
			<s:else>No</s:else>
			<s:if test="get('reason') != null && get('reason').length() > 0">
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
				onclick="$('#caoNote_<s:property value="get('caoId')"/>').show(); $('#caoNote_<s:property value="get('caoId')"/>').focus(); $('#show_<s:property value="get('caoId')"/>Text').hide(); return false;"
				title="<s:property value="get('caoNotes')"/>">
			<s:if test="get('caoNotes') != null">
				<s:property value="@com.picsauditing.util.Strings@trim(get('caoNotes'),30)"/>
			</s:if>
			<s:else>...</s:else>
			</a>
			<s:textarea id="caoNote_%{get('caoId')}" rows="4" cols="20" name="caos[%{get('caoId')}].notes" 
				value="%{get('caoNotes')}" cssStyle="display: none;"
				onkeyup="$('#cao_cb%{get('caoId')}').attr({checked: true});"/>
		</td>
	</tr>
</s:iterator>
	<tr>
		<td class="center" colspan="2">
			<center><input title="Check all" type="checkbox" onclick="setAllChecked(this);"/><br/>Select<br/>All</center>
		</td>
		<td colspan="7">
			<s:radio cssClass="statusSelects" name="newStatuses" list="#{'Approved':'Approve Selected','Rejected':'Reject Selected','NotApplicable':'Mark as N/A'}"/>
			<div class="buttons">
				<a class="picsbutton positive" href="#" onclick="return saveRows();">Save Changes</a>
			</div>
			</td>
	</tr>
</table>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</s:if>
<s:else>
	<div id="info">There are no matching policies to review at this time.</div>
</s:else>
