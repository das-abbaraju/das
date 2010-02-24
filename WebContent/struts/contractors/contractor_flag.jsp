<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function() {
	$('.datepicker').datepicker();
});
</script>
</head>
<body>

<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<div style="text-align: center; width: 100%">
<s:if test="co.waitingOn.ordinal() > 0"><div class="info" style="float: right; width: 200px">Currently waiting on <b><s:property value="co.waitingOn"/></b></div></s:if>
<table style="text-align: center;">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property
			value="co.flagColor.bigIcon" escape="false" /></td>
		<td style="vertical-align: middle;"><b>Overall Flag Status at <s:property value="co.operatorAccount.name"/></b>
		<br/><a href="http://help.picsauditing.com/wiki/Reviewing_Flag_Status" class="help">What does this mean?</a><br/></td>
	</tr>
	<tr>
		<td>
		<s:if test="opID == permissions.getAccountId() || permissions.corporate">
			<s:if test="co.forcedFlag">
			<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
				Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
				<br/>
				<s:hidden name="id" />
				<s:hidden name="opID" 	/>
				<pics:permission perm="EditForcedFlags">
					<s:if test="permissions.corporate">
						<s:checkbox name="overrideAll"/><label>Check to Cancel the Force the Flag Color at all your Facilities in your database</label><br/>
					</s:if>
					Reason:<br><s:textarea name="forceNote" value="" rows="4" cols="15"></s:textarea>
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
<div class="helpOnRight">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> are listed in this page. 
		If any requirements exceed the acceptable threshold or answer, those requirements will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>
</s:if>

<table class="report" style="clear: none;">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Requirement</td>
			<td>Upcoming</td>
		</tr>
	</thead>
	<s:iterator id="data" value="flagData">
		<s:if test="#data.criteria.auditType != null">
			<tr class="<s:property value="#data.flag" />">
				<td class="center"><s:property value="#data.flag.smallIcon"
					escape="false" />
				</td>
				<td><s:property value="#data.criteria.auditType.auditName" /></td>
				<td>
					<s:iterator id="con" value="contractor.audits">
						<s:if test="#data.criteria.auditType == #con.auditType">
							<s:if test="#data.criteria.auditType.classType.policy && !(#con.auditStatus.expired)">
								<s:iterator value="#con.operators">
									<s:if test="visible && (opID == operator)">
										<s:if test="!status.approved && !status.notApplicable">
											<s:if test="isCanSeeAudit(auditType)">
												<a href="Audit.action?auditID=<s:property value="#con.id" />"><s:property value="auditType.auditName" /></a>
												<s:property value="status"/><br/>
											</s:if>
										</s:if>
									</s:if>
								</s:iterator>
							</s:if>
							<s:else>
								<s:if test="#con.auditStatus.pendingSubmitted || #con.auditStatus.incomplete">
									<s:if test="isCanSeeAudit(auditType)">
										<a href="Audit.action?auditID=<s:property value="#con.id" />"><s:property value="auditFor" /> <s:property value="auditType.auditName" /></a>
									</s:if>
									<s:property value="auditStatus" /><br />
								</s:if>
							</s:else>
						</s:if>
					</s:iterator>
				</td>
			</tr>
		</s:if>
	</s:iterator>
	
	<pics:permission perm="ManageOperators">
		<tr><td colspan="3" class="center">
			Operator Matrix <br />
			[<a href="AuditOperator.action?oID=<s:property value="co.operatorAccount.inheritAudits.id" />">For Audits</a>]
			[<a href="AuditOperator.action?oID=<s:property value="co.operatorAccount.inheritInsurance.id" />">For Policies</a>]
		</td></tr>
	</pics:permission>
</table>

<s:if test="oshaFlagged">
	<table class="report" style="clear: none">
		<thead>
			<tr>
				<td>Flag</td>
				<td>OshaRateType</td>
				<td>MultiYearScope</td>
				<td>Value</td>
				<td>Verified</td>
				<td>Criteria</td>
				<td></td>
			</tr>
		</thead>
		<s:iterator id="data" value="flagData">
			<s:if test="#data.criteria.oshaType != null && #data.criteria.oshaType == co.operatorAccount.oshaType">		
				<tr class="<s:property value="#data.flag" />">
					<td class="center"><s:property
						value="#data.flag.smallIcon" escape="false" /></td>
					<td><s:property value="#data.criteria.oshaRateType.description" /></td>
					<td><s:property value="#data.criteria.multiYearScope" /></td>
					<s:iterator id="conCriteria" value="contractor.flagCriteria">					
						<s:if test="#data.criteria == #conCriteria.criteria">
							<td class="right">
								<s:property value="#conCriteria.answer" />  
							</td>
							<td>
								<s:if test="#conCriteria.verified">Verified</s:if>
								<s:else>Unverified</s:else>
							</td>	
						</s:if>	
						</s:iterator>
					<td>> 
						<s:if test="#data.criteria.allowCustomValue == false">
							<s:property value="#data.criteria.defaultValue"/>
						</s:if>
						<s:else>
							<s:iterator id="opCriteria" value="co.operatorAccount.flagQuestionCriteriaInherited">
								<s:if test="#opCriteria.criteria == #data.criteria">
									<s:property value="#opCriteria.hurdle"/> = <s:property value="#opCriteria.flag"/>
								</s:if>
							</s:iterator>
						</s:else>
					</td>
					<td>Link to Audit</td>
				</tr>
			</s:if>
		</s:iterator>
	</table>
</s:if>

<table class="report" style="clear: none">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Answer</td>
			<td>Criteria</td>
			<td>For</td>
			<td>Question</td>
			<td></td>
		</tr>
	</thead>
	<s:iterator id="data" value="flagData">
		<s:if test="#data.criteria.question != null && !#data.criteria.question.auditType.classType.policy">
			<tr class="<s:property value="#data.flag" />">
				<td class="center"><s:property
					value="#data.flag.smallIcon" escape="false" /></td>
				<td class="center">
					<s:iterator id="conCriteria" value="contractor.flagCriteria">					
						<s:if test="#data.criteria == #conCriteria.criteria">
							<s:property value="#conCriteria.answer" />
						</s:if>
					</s:iterator>				
				</td>
				<td><s:property value="#data.criteria.comparison"/> 
					<s:iterator id="opCriteria" value="co.operatorAccount.flagQuestionCriteriaInherited">
						<s:if test="#opCriteria.criteria == #data.criteria">
							<s:if test="!#data.criteria.allowCustomValue">
								<s:property value="#data.criteria.defaultValue"/>
							</s:if>
							<s:else>
								<s:property value="#opCriteria.hurdle"/> 
							</s:else>
							= <s:property value="#opCriteria.flag"/>
						</s:if>
					</s:iterator>
				</td>
				<td><s:property value="#data.criteria.question.auditType.auditName" /> <s:property value="#data.criteria.multiYearScope" /></td>
				<td>
					<s:property value="#data.criteria.question.question" escape="false" />
				</td>
				<td>
					Link to Audit
				</td>
			</tr>
		</s:if>
	</s:iterator>
	<pics:permission perm="EditFlagCriteria">
		<tr><td colspan="5" class="center"><a 
			href="ManageFlagCriteriaOperator.action?id=<s:property value="co.operatorAccount.inheritFlagCriteria.id" />">Edit Flag Criteria</a></td></tr>
	</pics:permission>
</table>


<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
	<s:if test="co.workStatusPending">
		<div class="alert">The operator has not approved this contractor yet.</div>
	</s:if>
	<s:if test="co.workStatusRejected">
		<div class="alert">The operator did not approve this contractor.</div>
	</s:if>
</s:if>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
