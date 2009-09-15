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
						<input id="forceEnd" name="forceEnd" size="8" type="text" />
						<a onclick="cal1.select($('forceEnd'),'anchor_forceEnd','M/d/yy'); return false;"
							name="anchor_forceEnd" id="anchor_forceEnd"
							href="#"><img src="images/icon_calendar.gif" width="18" height="15" border="0" /></a>
						<br/>
						<s:if test="permissions.corporate">
							<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label><br/>
						</s:if>
						Reason: <s:textarea name="forceNote" value="" rows="4" cols="15"></s:textarea><br />
						<span class="redMain">* All Fields are required</span>
						
						<div>
							<button class="picsbutton positive" type="submit" name="button" value="Force Flag" onclick="return checkForce();">Force Flag</button>
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

<s:if test="oshaFatalitiesUsed || oshaLwcrUsed || oshaTrirUsed || oshaCad7Used || oshaNeerUsed">

	<s:iterator value="co.operatorAccount.flagOshaCriteria">
		<s:if test="trir.required && trir.hurdleFlag.naics">
			<div id="info">
			The operator flags up to
			<s:if test="trir.hurdle > 100">
				<s:property value="trir.hurdle - 100" />% above
			</s:if>
			<s:elseif test="trir.hurdle < 100 ">
				<s:property value="100 - trir.hurdle" />% below
			</s:elseif>
			<s:else>
				100% 
			</s:else>
			of your NAICS industry average code <s:property value="co.contractorAccount.naics.code"/><br/>
			Your industry average TRIR is <s:property value="co.contractorAccount.naics.trir"/>.				
			</div>
		</s:if>	
	</s:iterator>

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
				<s:if test="oshaCad7Used">
					<td>Cad7</td>
					<td>Criteria</td>
				</s:if>
				<s:if test="oshaNeerUsed">
					<td>Neer</td>
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
							<td class="right">
								<s:property
									value="%{new java.text.DecimalFormat('#,##0.000').format(value.recordableTotalRate)}" />
							</td>	
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if test="trir.required">
									<s:if
										test="(key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && trir.timeAverage) || (!key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG) && !trir.timeAverage)">
	 									&gt; 
	 									<s:if test="trir.hurdleFlag.naics">
	 										<s:property value="%{new java.text.DecimalFormat('#,##0.000').format((co.contractorAccount.naics.trir * trir.hurdle) / 100)}" />(<s:property value="co.contractorAccount.naics.trir" /> * <s:property value="format(trir.hurdle,'#')" />)/100
	 									</s:if>
	 									<s:else>
	 										<s:property value="trir.hurdle" />
	 									</s:else>
	 									= <s:property value="flagColor" />
										<br />
									</s:if>
								</s:if>
							</s:iterator></td>
						</s:if>
						<s:if test="oshaCad7Used">
							<td class="right"><s:property value="value.cad7" /></td>
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if
									test="cad7.required && !key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG)">
								&gt; <s:property value="cad7.hurdle" /> = <s:property
										value="flagColor" />
									<br />
								</s:if>
							</s:iterator></td>
						</s:if>
						<s:if test="oshaNeerUsed">
							<td class="right"><s:property value="value.neer" /></td>
							<td style="vertical-align: middle;"><s:iterator
								value="co.operatorAccount.flagOshaCriteria">
								<s:if
									test="neer.required && !key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG)">
								&gt; <s:property value="neer.hurdle" /> = <s:property
										value="flagColor" />
									<br />
								</s:if>
							</s:iterator></td>
						</s:if>
						<td><s:if
							test="!key.equals(@com.picsauditing.jpa.entities.OshaAudit@AVG)">
							<a
								href="AuditCat.action?auditID=<s:property value="value.conAudit.id"/>&catID=<s:property value="shaTypeID"/>">Show</a>
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
			<td>
				<s:set name="auditCatData" value="@com.picsauditing.actions.contractors.ContractorFlagAction@getAuditCatData(answer.audit.id,answer.question.id)"/>
				<s:if test="#auditCatData != null">
					<a href="AuditCat.action?auditID=<s:property value="answer.audit.id"/>&catDataID=<s:property value="#auditCatData.id"/>&mode=View#node_<s:property value="answer.question.id"/>"><s:property value="answer.question.question" /></a>
				</s:if>
				<s:else>
					<s:property value="answer.question.question" />
				</s:else>
			</td>
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
								
