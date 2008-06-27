<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<script type="text/javascript" src="js/prototype.js"></script>
<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
<SCRIPT LANGUAGE="JavaScript">var cal1 = new CalendarPopup();</SCRIPT>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
</head>
<body>
<s:push value="#subHeading='Flag Status'" />
<s:include value="conHeader.jsp" />

<div style="text-align: center; width: 100%">
<table border="1">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property
			value="co.flag.flagColor.bigIcon" escape="false" /></td>
		<td style="vertical-align: middle;"><b>Overall Flag Status at <s:property value="co.operatorAccount.name"/></b></td>
	</tr>
	<tr>
		<td style="font-style: italic">
		<s:if test="opID == permissions.getAccountId() || permissions.corporate">
			<s:if test="co.forcedFlag">
			<s:form cssStyle="border: 2px solid #A84D10; background-color: #FFC; font-style: italic; padding: 2px;">
				Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
				<s:hidden name="id" />
				<s:hidden name="opID" 	/>
				<s:hidden name="action" value="deleteOverride" />
				<pics:permission perm="EditForcedFlags">
				<s:submit value="Cancel Override" /><br/>
				<s:if test="permissions.corporate">
					<s:checkbox name="deleteAll"/><label>Check to Cancel the Force the Flag Color at all your Facilities in your database</label>
				</s:if>
				</pics:permission>
			</s:form>
			</s:if>
			<s:else>
				<pics:permission perm="EditForcedFlags">
				<div id="override" style="display: none">
				<s:form id="form_override">
					<s:hidden name="id" />
					<s:hidden name="opID" />
					<s:submit value="Force Flag" />
					<s:select list="flagList" name="forceFlag" />
					until <input id="forceEnd" name="forceEnd" size="8" type="text"
						onClick="cal1.select(this,'forceEnd','M/d/yy'); return false;" /><br/>
					<s:hidden name="action" value="Override" />
				<s:if test="permissions.corporate">
					<s:checkbox name="overrideAll"/><label>Check to Force the Flag Color for all your Facilities in your database</label>
				</s:if>
				</s:form>
				<a href="#" onclick="$('override_link').show(); $('override').hide(); return false;">Nevermind</a>
				</div>
				<a id="override_link" href="#" onclick="$('override').show(); this.hide(); return false;">Manually Force Flag Color</a>
				</pics:permission>
			</s:else>
		</s:if>
		</td>
	</tr>
</table>
</div>
<s:if test="permissions.contractor">
<div class="helpBox">
		The minimum requirements set by <s:property value="co.operatorAccount.name"/> is listed in this page. 
		If any requirement exceeds the acceptable threshold or answer, that requirement will be flagged. The overall flag color is set to Red if any requirement is flagged Red. 
		It is set to Amber if any requirement is flagged Amber. If no requirement is Red or Amber, then the overall flag color will be Green.
</div>	
</s:if>
<table class="report">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Required Audits</td>
		</tr>
	</thead>
	<s:iterator value="co.operatorAccount.audits">
		<s:if test="requiredForFlag && requiredForFlag.name() in {'Red', 'Amber'}">  
			<tr class="<s:property value="contractorFlag" />">
				<td class="center"><s:property value="contractorFlag.smallIcon"
					escape="false" /></td>
				<td><s:property value="auditType.auditName" /></td>
			</tr>
		</s:if>
	</s:iterator>
</table>

<pics:permission perm="ManageOperators">
	<div><a
		href="AuditOperator.action?oID=<s:property value="opID" />">Flag
	Criteria for Audits</a></div>
</pics:permission>

<s:if test="oshaFatalitiesUsed || oshaLwcrUsed || oshaTrirUsed">
<s:if test="contractor.oshas.size > 0">
	<table class="report">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Year</td>
				<s:if test="oshaFatalitiesUsed">
					<td>Fatalities</td>
					<td>Required Fatalities</td>
				</s:if>
				<s:if test="oshaLwcrUsed">
					<td>LWCR</td>
					<td>Required LWCR</td>
				</s:if>
				<s:if test="oshaTrirUsed">
					<td>TRIR</td>
					<td>Required TRIR</td>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="contractor.oshas">
			<s:if test="corporate">
				<tr class="<s:property value="year1.flagColor" />">
					<td class="center"><s:property
						value="year1.flagColor.smallIcon" escape="false" /></td>
					<td>2007</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year1.fatalities" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year1.flagColor == flagColor}">
								<s:property value="fatalities.hurdle"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year1.lostWorkCasesRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year1.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(lwcr.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year1.recordableTotalRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year1.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(trir.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
				</tr>
				<tr class="<s:property value="year2.flagColor" />">
					<td class="center"><s:property
						value="year2.flagColor.smallIcon" escape="false" /></td>
					<td>2006</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year2.fatalities" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year2.flagColor == flagColor}">
								<s:property value="fatalities.hurdle"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year2.lostWorkCasesRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year2.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(lwcr.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year2.recordableTotalRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year2.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(trir.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
				</tr>
				<tr class="<s:property value="year3.flagColor" />">
					<td class="center"><s:property
						value="year3.flagColor.smallIcon" escape="false" /></td>
					<td>2005</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year3.fatalities" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year3.flagColor == flagColor}">
								<s:property value="fatalities.hurdle"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year3.lostWorkCasesRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year3.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(lwcr.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year3.recordableTotalRate)}" /></td>
						<td class="right"><s:iterator value="co.operatorAccount.flagOshaCriteria">
							<s:if test="%{year3.flagColor == flagColor}">
								<s:property value="%{new java.text.DecimalFormat('#,##0.0').format(trir.hurdle)}"/>
							</s:if>
						</s:iterator>
						</td>
					</s:if>
				</tr>
				<s:if test="oshaAveragesUsed">
					<tr class="<s:property value="flagColor" />">
						<td class="center"><s:property value="flagColor.smallIcon"
							escape="false" /></td>
						<td>Avg</td>
						<s:if test="oshaFatalitiesUsed">
							<td></td>
							<td></td>
						</s:if>
						<s:if test="oshaLwcrUsed">
							<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(averageLwcr)}" /></td>
							<td></td>
						</s:if>
						<s:if test="oshaTrirUsed">
							<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(averageTrir)}" /></td>
							<td></td>
						</s:if>
					</tr>
				</s:if>
			</s:if>
		</s:iterator>
	</table>
</s:if>
</s:if>

<s:if test="auditData.size > 0">
	<table class="report">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Answer</td>
				<td>Audit Question</td>
			</tr>
		</thead>
		<s:iterator value="auditData">
			<tr class="<s:property value="value.flagColor" />">
				<td class="center"><s:property
					value="value.flagColor.smallIcon" escape="false" /></td>
				<td class="center"><s:property value="value.verifiedAnswerOrAnswer" /></td>
				<td><s:property value="value.question.question" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>


<pics:permission perm="EditFlagCriteria">
<div><a href="op_editFlagCriteria.jsp?opID=<s:property value="opID" />">Edit Flag Criteria</a></div>
</pics:permission>


<s:if test="co.operatorAccount.canSeeInsurance">
	<table class="report">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Insurance</td>
				<td>Status</td>
			</tr>
		</thead>
		<s:iterator value="contractor.certificates">		
		<s:if test="opID == operatorAccount.id">
			<tr class="<s:property value="flagColor" />">
				<td class="center"><s:property
					value="flagColor.smallIcon" escape="false" /></td>
				<td><s:property value="type" /></td>
				<td><s:property value="status" /></td>
			</tr>
		</s:if>
		</s:iterator>
	</table>
</s:if>

</body>
</html>
								
