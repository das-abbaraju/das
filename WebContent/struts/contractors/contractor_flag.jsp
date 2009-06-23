<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript" src="js/prototype.js"></script>
<script src="js/notes.js" type="text/javascript"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
	var cal1 = new CalendarPopup('caldiv1');
	cal1.offsetY = -110;
	cal1.offsetX = 0;
	cal1.addDisabledDates(null, "<%= com.picsauditing.PICS.DateBean.getTodaysDate() %>");
	cal1.showNavigationDropdowns();
	cal1.setCssPrefix("PICS");
</SCRIPT>
</head>
<body>

<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<div style="text-align: center; width: 100%">
<s:if test="co.flag.waitingOn.ordinal() > 0"><div id="info" style="float: right; width: 200px">Currently waiting on <b><s:property value="co.flag.waitingOn"/></b></div></s:if>

<table style="text-align: center;">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property
			value="co.flag.flagColor.bigIcon" escape="false" /></td>
		<td style="vertical-align: middle;"><b>Overall Flag Status at <s:property value="co.operatorAccount.name"/></b></td>
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
					Reason:<br><s:textarea name="forceNote" value=""></s:textarea>
					<div class="buttons">
						<button class="positive" type="submit" name="button" value="Cancel Override">Cancel Override</button>
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
						<input id="forceEnd" name="forceEnd" size="8" type="text" />
						<a onclick="cal1.select($('forceEnd'),'anchor_forceEnd','M/d/yy'); return false;"
							name="anchor_forceEnd" id="anchor_forceEnd"
							href="#"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
						<br/>
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label><br/>
						</s:if>
						<s:textarea name="forceNote" value=""></s:textarea><br />
						<div class="buttons">
							<button class="positive" type="submit" name="button" value="Force Flag" onclick="return checkForce();">Force Flag</button>
						</div>
					</s:form>
					<a href="#" onclick="$('override_link').show(); $('override').hide(); return false;">Nevermind</a>
					</div>
					<a id="override_link" href="#" onclick="$('override').show(); $('override_link').hide(); return false;">Manually Force Flag Color</a>
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
	<s:iterator id="op" value="co.operatorAccount.visibleAudits">
		<s:if test="contractorFlag != null">
			<tr class="<s:property value="contractorFlag" />">
				<td class="center"><s:property value="contractorFlag.smallIcon"
					escape="false" />
					</td>
				<td><s:property value="auditType.auditName" /></td>
				<td>
				<s:iterator id="con" value="contractor.audits">
					<s:if test="#op.auditType == #con.auditType">
						<s:if test="#op.auditType.classType.policy && !(#con.auditStatus.expired)">
							<s:iterator value="#con.operators">
								<s:if test="visible && (#op.operatorAccount == operator)">
									<s:if test="!status.approved && !status.notApplicable">
										<a href="Audit.action?auditID=<s:property value="#con.id" />"><s:property value="auditType.auditName" /></a>
										<s:property value="status"/><br/>
									</s:if>
								</s:if>
							</s:iterator>
						</s:if>
						<s:else>
							<s:if test="#con.auditStatus.pendingSubmitted || #con.auditStatus.incomplete">
								<a href="Audit.action?auditID=<s:property value="#con.id" />"><s:property value="auditFor" /> <s:property value="auditType.auditName" /></a>
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

<s:if test="oshaFatalitiesUsed || oshaLwcrUsed || oshaTrirUsed">
<s:if test="oshas.size > 0">
	<table class="report" style="clear: none">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Year</td>
				<s:if test="oshaFatalitiesUsed">
					<td>Fatalities</td>
					<td>Criteria</td>
				</s:if>
				<s:if test="oshaLwcrUsed">
					<td>LWCR</td>
					<td>Criteria</td>
				</s:if>
				<s:if test="oshaTrirUsed">
					<td>TRIR</td>
					<td>Criteria</td>
				</s:if>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="oshas">
			<s:iterator value="value">
					<tr class="<s:property value="value.flagColor" />">
						<td class="center"><s:property
							value="value.flagColor.smallIcon" escape="false" /></td>
						<td><s:property value="key" /></td>
						<s:if test="oshaFatalitiesUsed">
							<td class="right"><s:property value="value.fatalities" /></td>
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if
									test="fatalities.required && !key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG)">
								&gt; <s:property value="fatalities.hurdle" /> = <s:property
										value="flagColor" />
									<br />
								</s:if>
							</s:iterator></td>
						</s:if>
						<s:if test="oshaLwcrUsed">
							<td class="right"><s:property
								value="%{new java.text.DecimalFormat('#,##0.000').format(value.lostWorkCasesRate)}" /></td>
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if test="lwcr.required">
									<s:if
										test="(key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && lwcr.timeAverage) || (!key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && !lwcr.timeAverage)">
								&gt; <s:property value="lwcr.hurdle" /> = <s:property
											value="flagColor" />
										<br />
									</s:if>
								</s:if>
							</s:iterator></td>
						</s:if>
						<s:if test="oshaTrirUsed">
							<td class="right"><s:property
								value="%{new java.text.DecimalFormat('#,##0.000').format(value.recordableTotalRate)}" /></td>
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if test="trir.required">
									<s:if
										test="(key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && trir.timeAverage) || (!key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && !trir.timeAverage)">
	 									&gt; 
	 									<s:if test="trir.hurdleFlag.toString().equals('NAICS')">
	 										<s:property value="co.contractorAccount.naics.trir" />
	 									</s:if>
	 									<s:else>
	 										<s:property value="trir.hurdle" />
	 									</s:else>
	 									= <s:property value="flagColor" />
	 									<s:if test="trir.hurdleFlag.toString().equals('NAICS')"> (NAICS code:<s:property value="co.contractorAccount.naics.code"/>)</s:if>
										<br />
									</s:if>
								</s:if>
							</s:iterator></td>
						</s:if>
						<td><s:if
							test="!key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG)">
							<a
								href="AuditCat.action?auditID=<s:property value="value.conAudit.id"/>&catID=151">Show</a>
						</s:if></td>
					</tr>
				</s:iterator>
		</s:iterator>
	</table>
</s:if>
</s:if>

<table class="report" style="clear: none">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Answer</td>
			<td>For</td>
			<td>Question</td>
		</tr>
	</thead>
	<s:iterator value="acaListAudits">
		<tr class="<s:property value="resultColor" />">
			<td class="center"><s:property
				value="resultColor.smallIcon" escape="false" /></td>
			<td class="center"><s:property value="answer.answer" /></td>
			<td><s:property value="answer.audit.auditType.auditName" /> <s:property value="answer.audit.auditFor" /></td>
			<td><s:property value="answer.question.question" /></td>
		</tr>
	</s:iterator>
	<pics:permission perm="EditFlagCriteria">
		<tr><td colspan="4" class="center"><a 
			href="OperatorFlagCriteria.action?id=<s:property value="co.operatorAccount.id" />">Edit Flag Criteria</a></td></tr>
	</pics:permission>
</table>


<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
	<s:if test="co.workStatusPending">
		<div id="alert">The operator has not approved this contractor yet.</div>
	</s:if>
	<s:if test="co.workStatusRejected">
		<div id="alert">The operator did not approve this contractor.</div>
	</s:if>
</s:if>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

<div id="caldiv1" style="position:absolute; visibility:hidden; background-color:white; layer-background-color:white;"></div>

</body>
</html>
								
