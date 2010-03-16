<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<style type="text/css">
table.report a {
	text-decoration: underline;
}

.hide {
	display: none;
}

.hover {
	margin-left: 10px;
	display: none;
}
small {
	font-size: x-small;
}
.flagCategories td {
	padding-right: 10px;
}

.details {
	display: none;
}
</style>
<s:include value="../jquery.jsp" />
<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker();
})
</script>
</head>
<body>

<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<!-- OVERALL FLAG -->
<div style="text-align: center; width: 100%">
<s:if test="permissions.operatorCorporate || permissions.admin">
	<div class="info" style="float: right; clear: right; width: 25%;">
	<s:form>
		<s:hidden name="id" />
		<s:hidden name="opID" />
		This contractor's flags were last calculated <s:date name="co.flagLastUpdated" nice="true" /><br />
		<button class="picsbutton" type="submit" name="button" value="Recalculate Now">Recalculate Now</button>
	</s:form>
	</div>
</s:if>
<s:if test="co.waitingOn.ordinal() > 0"><div class="info" style="float: right; clear: right; width: 25%;">Currently waiting on <b><s:property value="co.waitingOn"/></b></div></s:if>
<table style="text-align: center;">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property	value="co.flagColor.bigIcon" escape="false" /></td>
		<td style="vertical-align: middle;">
			<b>Overall Flag Status at <s:property value="co.operatorAccount.name"/></b>
			<pics:permission perm="EditFlagCriteria">
				(<a href="ManageFlagCriteriaOperator.action?id=<s:property value="co.operatorAccount.inheritFlagCriteria.id" />">View Flag Criteria</a>)		
			</pics:permission>
			<br/><a href="http://help.picsauditing.com/wiki/Reviewing_Flag_Status" class="help">What does this mean?</a><br/>
		</td>
	</tr>
	<tr>
		<td>
		<s:if test="opID == permissions.getAccountId() || permissions.corporate">
			<s:if test="co.forcedFlag">
				<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
					Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
					<br/>
					<s:hidden name="id" />
					<s:hidden name="opID" />
					<pics:permission perm="EditForcedFlags">
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Cancel the Force the Flag Color at all your Facilities in your database</label><br/>
						</s:if>
						Reason:<br><s:textarea name="forceNote" value="" rows="3" cols="15"></s:textarea>
						<div>
							<button class="picsbutton positive" type="submit" name="button" value="Cancel Override">Cancel Override</button>
						</div>
						<br />
					</pics:permission>
				</s:form>
			</s:if>
			<s:else>
				<pics:permission perm="EditForcedFlags">
					<div id="override" style="display: none">
					<s:form id="form_override">
						<s:hidden name="id" />
						<s:hidden name="opID" />
						<s:select list="flagList" name="forceFlag" />
						until 
						<input id="forceEnd" name="forceEnd" size="8" type="text" class="datepicker"/>
						<br/>
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label><br/>
						</s:if>
						Reason: <s:textarea name="forceNote" value="" rows="4" cols="15"></s:textarea><br />
						<span class="redMain">* All Fields are required</span>
						
						<div>
							<button class="picsbutton positive" type="submit" name="button" value="Force Flag">Force Flag</button>
						</div>
					</s:form>
					<a href="#" onclick="$('#override_link').show(); $('#override').hide(); return false;">Nevermind</a>
					</div>
					<a id="override_link" href="#" onclick="$('#override').show(); $('#override_link').hide(); return false;">Manually Force Flag Color</a>
				</pics:permission>
			</s:else>
		</s:if>
		<s:else>
			<s:if test="co.forcedFlag">
			<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
				Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
			</s:form>
			</s:if>
		</s:else>
		</td>
	</tr>
</table>
</div>

<s:if test="permissions.contractor">
<div class="helpOnRight" style="clear: right;">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> are listed in this page. 
		If any requirements exceed the acceptable threshold or answer, those requirements will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>
</s:if>

<span id="thinking"></span>

<!-- OVERRIDES -->
<s:if test="displayTable">
<table class="report" style="clear: none;">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Description</td>
			<td>Value</td>
		</tr>
	</thead>
	<tbody>
	<s:iterator id="key" value="flagDataMap.keySet()">
		<s:iterator id="data" value="flagDataMap.get(#key)">
			<s:if test="#data.flag.toString() == 'Red' || #data.flag.toString() == 'Amber' || isFlagDataOverride(#data)">
			<s:set name="flagoverride" value="%{isFlagDataOverride(#data)}"/>
				<tr class="<s:property value="#data.flag" />">
					<td>
						<s:property value="#data.flag.smallIcon" escape="false" />
						<s:if test="opID == permissions.getAccountId() || permissions.corporate">	
							<pics:permission perm="EditForcedFlags">
								<a id="override_link_flagdata_<s:property value="%{#data.id}" />" href="#" 
									onclick="$('#'+<s:property value="%{#data.id}" />+'_override').toggle(); return false;">
									<s:if test="#flagoverride != null">
										Flag has been forced
									</s:if>
									<s:else>
										Override
									</s:else>
								</a>
							</pics:permission>
						</s:if>
						<s:else>
							<s:if test="#flagoverride != null">
								Manual Force Flag <s:property value="#flagoverride.forceFlag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />
							</s:if>
						</s:else>
					</td>
					<td>
						<s:iterator id="opCriteria" value="co.operatorAccount.flagCriteriaInherited">
							<s:if test="#opCriteria.criteria.id == #data.criteria.id && #opCriteria.flag == #data.flag">
								<s:property value="#opCriteria.replaceHurdle" />
							</s:if>
						</s:iterator>
					</td>
					<td>
						<s:if test="#data.criteria.auditType != null">
							<s:iterator id="audit" value="contractor.audits">
								<s:if test="#data.criteria.auditType == #audit.auditType">
									<s:if test="#data.criteria.auditType.classType.policy && !(#audit.auditStatus.expired)">
										<s:iterator value="#audit.operators">
											<s:if test="visible && (co.operatorAccount.inheritInsurance == operator)">
												<s:if test="isCanSeeAudit(#audit.auditType)">
													<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#audit.auditType.auditName" /></a>
													<s:property value="status"/><br/>
												</s:if>
											</s:if>
										</s:iterator>
									</s:if>
									<s:else>
										<s:if test="#audit.auditStatus.pendingSubmitted || #audit.auditStatus.incomplete">
											<s:if test="isCanSeeAudit(#audit.auditType)">
												<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#audit.auditFor" /> <s:property value="#audit.auditType.auditName" /></a>
											</s:if>
											<s:property value="#audit.auditStatus" /><br />
										</s:if>
									</s:else>
								</s:if>
							</s:iterator>
						</s:if>
						<s:else>
							<s:iterator id="conCriteria" value="contractor.flagCriteria">					
								<s:if test="#data.criteria.id == #conCriteria.criteria.id">
									<s:if test="#data.criteria.dataType == 'number'">
										<s:property value="format(#conCriteria.answer)" />
									</s:if>
									<s:else>
										<s:property value="#conCriteria.answer" />
									</s:else>
									<s:if test="#conCriteria.answer2.length() > 0">
										<br /><s:property value="#conCriteria.answer2" escape="false"/>
									</s:if>
								</s:if>
							</s:iterator>
						</s:else>
					</td>
				</tr>
				<pics:permission perm="EditForcedFlags">
					<tr id="<s:property value="%{#data.id}" />_override" style="display: none">
						<td colspan="3"><form method="post">
							<s:hidden value="%{#data.id}" name="dataID" />
							<s:if test="#flagoverride != null">
								Manual Force Flag <s:property value="#flagoverride.forceflag.smallIcon" escape="false" /> until <s:date name="#flagoverride.forceEnd" format="MMM d, yyyy" />.
								<pics:permission perm="EditForcedFlags">
									<s:if test="permissions.corporate">
										<s:checkbox name="overrideAll"/><label>Check to Cancel the Force Flag Color at all your Facilities in your database</label>
									</s:if>
									&nbsp;Reason for Cancelling: <s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea>
									<input type="submit" value="Cancel Data Override" class="picsbutton positive" name="button" />
								</pics:permission>
							</s:if>
							<s:else>
								<s:select list="flagList" name="forceFlag" /> until 
								<input id="forceEnd_<s:property value="%{#data.id}" />" name="forceEnd" size="8" type="text" class="datepicker" />
								Reason for Forcing: <s:textarea name="forceNote" value="" rows="2" cols="15" cssStyle="vertical-align: top;"></s:textarea>
								<button class="picsbutton positive" type="submit" name="button" value="Force Data Override">Force Data Override</button>
							</s:else>
						</form></td>
					</tr>
				</pics:permission>
			</s:if>
		</s:iterator>
	</s:iterator>
	</tbody>
</table>
</s:if>

<a href="#" onclick="$('.details').toggle('slow'); return false;">Toggle details</a>

<!-- ALL FLAGS -->
<table class="flagCategories details">
	<tr>
	<s:iterator id="key" value="flagDataMap.keySet()">
		<td><table class="report">
			<thead>
				<tr>
					<td>Flag</td>
					<td><s:property value="#key"/></td>
				</tr>
			</thead>
			<s:iterator id="data" value="flagDataMap.get(#key)">
				<tr>
					<td class="center">
						<s:property value="flag.smallIcon" escape="false"/>
					</td>
					<td>
						<s:if test="criteria.auditType != null">
							<s:property value="criteria.auditType.auditName" />
						</s:if>
						<s:else>
							<s:property value="criteria.label" /> - 
							<s:iterator id="conCriteria" value="contractor.flagCriteria">					
								<s:if test="#data.criteria.id == #conCriteria.criteria.id">
									<s:if test="criteria.dataType == 'number'">
										<s:property value="format(#conCriteria.answer)" />
									</s:if>
									<s:else>
										<s:property value="#conCriteria.answer" />
									</s:else>
									<s:if test="#conCriteria.answer2.length() > 0">
										<br /><s:property value="#conCriteria.answer2" escape="false"/>
									</s:if>
								</s:if>	
							</s:iterator>
						</s:else>
					</td>			
				</tr>
			</s:iterator>
		</table></td>
	</s:iterator>
	</tr>
</table>

<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
	<s:if test="co.workStatusPending">
		<div class="alert">The operator has not approved this contractor yet.</div>
	</s:if>
	<s:if test="co.workStatusRejected">
		<div class="alert">The operator did not approve this contractor.</div>
	</s:if>
</s:if>

<div id="notesList" class="details"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
