<%@page language="java" errorPage="exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>PQF Verification for <s:property
	value="conAudit.contractorAccount.name" /></title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects"
	type="text/javascript"></script>
<style>
.oshanum {
	width: 100px;
	text-align: right;
	font-size: 14px;
	font-family: sans-serif;
	color: #003366;
}

.highlight {
	background-color: #BBB;
}
</style>
<script src="js/prototype.js" type="text/javascript"></script>
<script type="text/javascript">

function moveFile(type, year) {
	var newyear = $F('move'+type+year);
	if (newyear=="") return;
	status.innerHTML = "Switching files...";
	pars = 'oshaID=<s:property value="osha.id" />&type='+type+'&oldYear='+year+'&newYear='+newyear;
	var myAjax = new Ajax.Updater('status', 'VerifySwitchFilesAjax.action', {method: 'post', parameters: pars});
}

function copyAnswer(selectedYear) {
	// If the answer is correct and hasn't been filled in yet, then default it
	if ($F('verify_emr'+selectedYear+'_isCorrectBoolean') == null) return;
	
	var answer = $('verify')['verify_emr'+selectedYear+'_verifiedAnswer'];
	if (answer.present()) return;
	
	var original = $('verify')['verify_emr'+selectedYear+'_answer'];
	if (original.present())
		$(answer).value = $F(original);
}

function saveFollowup() {
	var x = $F('followUpInterval');
	if (x==0) return;
	pars = 'auditID=<s:property value="auditID" />&followUp='+x;
	var myAjax = new Ajax.Updater('scheduleDate', 'VerifySaveFollowUpAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($('scheduleDate'), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}

function sendEmail() {
	pars = 'auditID=<s:property value="auditID" />';
	var myAjax = new Ajax.Updater('emailStatus', 'VerifySendEmailAjax.action', {method: 'post', parameters: pars});
	new Effect.Highlight($('emailStatus'), {duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
}
</script>
</head>
<body>
<h1><s:property value="contractor.name" />
<span class="sub">PQF Verification</span></h1>
<s:push value="#request.current='Audits'"/>
<s:include value="con_nav.jsp" />

<p class="blueMain"><a href="pqf_verification.jsp">PQF Verification Report</a></p>

<s:form id="verify">
	<s:hidden name="auditID" />
	<s:hidden name="osha.id" />

	<div id="status" style="text-align: center;"></div>
	<table border="0" cellpadding="1" cellspacing="1" align="center">
		<s:if test="oshaID > 0">
			<tr bgcolor="#003366" class="whiteTitle" align="center">
				<td colspan="2" align="left">OSHA</td>
				<td><s:property value="year1" /></td>
				<td><s:property value="year2" /></td>
				<td><s:property value="year3" /></td>
			</tr>
			<tr class="blueMain" align="center">
				<td align="right">Applicable:</td>
				<td>&nbsp;</td>
				
				<td class="highlight"><s:checkbox name="osha.year1.applicable" value="osha.year1.applicable"/></td>
				<td><s:checkbox name="osha.year2.applicable" value="osha.year2.applicable"/></td>
				<td class="highlight"><s:checkbox name="osha.year3.applicable" value="osha.year3.applicable"/></td>
			</tr>
			<tr class="blueMain" align="center" valign="top">
				<td align="right"><a
					href="pqf_OSHA.jsp?action=Edit&catID=29&oID=<s:property value="oshaID" />&id=<s:property value="id" />#upload"
					class="blueMain">Upload New Files</a></td>
				<td>&nbsp;</td>
				<td class="highlight"><s:if test="osha.year1.file && osha.year1.file.name().equals('Yes')">
					<a href="#"
						onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha1','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;"
						onMouseOver="status='Osha 300 Logs'">Show File</a>
					<br />Switch with: <select id="moveosha1"
						onchange="moveFile('osha', 1)" class="blueMain">
						<option></option>
						<option>2006</option>
						<option>2005</option>
					</select>
				</s:if> <s:else>No File</s:else>
				<td><s:if test="osha.year2.file && osha.year2.file.name().equals('Yes')">
					<a href="#"
						onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha2','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;"
						onMouseOver="status='Osha 300 Logs'">Show File</a>
				</s:if> <s:else>No File</s:else></td>
				<td class="highlight"><s:if test="osha.year3.file && osha.year3.file.name().equals('Yes')">
					<a href="#"
						onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha3','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;"
						onMouseOver="status='Osha 300 Logs'">Show File</a>
				</s:if> <s:else>No File</s:else></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Man Hours Worked:</td>
				<td>&nbsp;</td>
				<td class="highlight"><s:textfield name="osha.year1.manHours"
					cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.manHours" cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield name="osha.year3.manHours"
					cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Number of Fatalities:</td>
				<td align="center">G</td>
				<td class="highlight"><s:textfield name="osha.year1.fatalities"
					cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.fatalities" cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield name="osha.year3.fatalities"
					cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Number of Lost Work Cases:</td>
				<td align="center">H</td>
				<td class="highlight"><s:textfield name="osha.year1.lostWorkCases"
					cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.lostWorkCases" cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield name="osha.year3.lostWorkCases"
					cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Number of Lost Workdays:</td>
				<td>K(L)</td>
				<td class="highlight"><s:textfield name="osha.year1.lostWorkDays"
					cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year1.lostWorkDays" cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield name="osha.year3.lostWorkDays"
					cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Injury &amp; Illnesses Medical Cases:</td>
				<td align="center">J</td>
				<td class="highlight"><s:textfield
					name="osha.year1.injuryIllnessCases" cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.injuryIllnessCases"
					cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield
					name="osha.year3.injuryIllnessCases" cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Restricted Work Cases:</td>
				<td align="center">I</td>
				<td class="highlight"><s:textfield
					name="osha.year1.restrictedWorkCases" cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.restrictedWorkCases"
					cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield
					name="osha.year3.restrictedWorkCases" cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="right">
				<td>Total Injuries and Illnesses:</td>
				<td>&nbsp;</td>
				<td class="highlight"><s:textfield name="osha.year1.recordableTotal"
					cssClass="oshanum" /></td>
				<td><s:textfield name="osha.year2.recordableTotal"
					cssClass="oshanum" /></td>
				<td class="highlight"><s:textfield name="osha.year3.recordableTotal"
					cssClass="oshanum" /></td>
			</tr>
			<tr class="blueMain" align="center">
				<td align="right">Is Correct:</td>
				<td>&nbsp;</td>
				<td class="highlight" style="font-size: 14px; font-weight: bolder;">
					<input type="radio" name="osha.year1.verified" value="true"  <s:if test="osha.year1.verified">checked</s:if>/>Yes
					<input type="radio" name="osha.year1.verified" value="false" <s:if test="!osha.year1.verified">checked</s:if>/>No
					</td>
				<td style="font-size: 14px; font-weight: bolder;">
					<input type="radio" name="osha.year2.verified" value="true"  <s:if test="osha.year2.verified">checked</s:if>/>Yes
					<input type="radio" name="osha.year2.verified"  value="false" <s:if test="!osha.year2.verified">checked</s:if>/>No
					</td>
				<td class="highlight" style="font-size: 14px; font-weight: bolder;">
					<input type="radio" name="osha.year3.verified" value="true" <s:if test="osha.year3.verified">checked</s:if>/>Yes
					<input type="radio" name="osha.year3.verified" value="false" <s:if test="!osha.year3.verified">checked</s:if>/>No
					</td>
			</tr>
			<tr class="blueMain" align="center">
				<td align="right">Verified Date:</td>
				<td>&nbsp;</td>
				<td class="highlight"><s:date name="osha.year1.verifiedDate" format="MM/dd/yyyy"/></td>
				<td><s:date name="osha.year2.verifiedDate" format="MM/dd/yyyy"/></td>
				<td class="highlight"><s:date name="osha.year3.verifiedDate" format="MM/dd/yyyy"/></td>
			</tr>
			<tr class="blueMain" align="center">
				<td align="right">Issue:</td>
				<td>&nbsp;</td>
				<td class="highlight"><s:select list="oshaProblems" name="osha.year1.comment" cssClass="blueMain" /></td>
				<td><s:select list="oshaProblems" name="osha.year2.comment" cssClass="blueMain" /></td>
				<td class="highlight"><s:select list="oshaProblems" name="osha.year3.comment" cssClass="blueMain" /></td>
			</tr>
		</s:if>
		<s:else>
			<tr>
				<td>Select the Primary OSHA Log to verify</td>
			</tr>
			<tr>
				<td>TODO: List all of the OSHA Logs here to select from</td>
			</tr>
		</s:else>

		<tr bgcolor="#003366" class="whiteTitle" align="center">
			<td colspan="2" align="left">EMR / Insurance</td>
			<td><s:property value="year1" /></td>
			<td><s:property value="year2" /></td>
			<td><s:property value="year3" /></td>
		</tr>
		<tr class="blueMain" align="center" valign="top">
			<td colspan="2"><a
				href="pqf_uploadFile.jsp?catID=10&auditID=<s:property value="auditID" />"
				class="blueMain">Upload New Files</a></td>
			<td class="highlight"><a href="#"
				onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr1.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show
			File</a></td>
			<td class="highlight"><a href="#"
				onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr2.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show
			File</a></td>
			<td class="highlight"><a href="#"
				onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr3.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show
			File</a></td>
		</tr>
		<tr class="blueMain" align="right">
			<td colspan="2">Original Answer:</td>
			<td class="highlight"><s:textfield name="emr1.answer"
				cssClass="oshanum" disabled="true" /></td>
			<td><s:textfield name="emr2.answer" cssClass="oshanum"
				disabled="true" /></td>
			<td class="highlight"><s:textfield name="emr3.answer"
				cssClass="oshanum" disabled="true" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td colspan="2">Verified Answer:</td>
			<td class="highlight"><s:textfield name="emr1.verifiedAnswer"
				cssClass="oshanum" /></td>
			<td><s:textfield name="emr2.verifiedAnswer" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="emr3.verifiedAnswer"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right" colspan="2">Is Correct:</td>
			<td class="highlight" style="font-size: 14px; font-weight: bolder;">
				<input id="verify_emr1_isCorrectBoolean" type="radio" name="emr1.isCorrectBoolean" value="true" <s:if test="emr1.isCorrectBoolean">checked</s:if> onchange="copyAnswer(1);"/>Yes
				<input type="radio" name="emr1.isCorrectBoolean" value="false" <s:if test="emr1.isCorrectBoolean == null || ! emr1.isCorrectBoolean">checked</s:if>/>No
			</td>
			<td style="font-size: 14px; font-weight: bolder;">
				<input id="verify_emr2_isCorrectBoolean" type="radio" name="emr2.isCorrectBoolean" value="true" <s:if test="emr2.isCorrectBoolean">checked</s:if> onchange="copyAnswer(2);"/>Yes
				<input type="radio" name="emr2.isCorrectBoolean" value="false" <s:if test="emr2.isCorrectBoolean == null || ! emr2.isCorrectBoolean">checked</s:if>/>No
			</td>
			<td class="highlight" style="font-size: 14px; font-weight: bolder;">
				<input id="verify_emr3_isCorrectBoolean" type="radio" name="emr3.isCorrectBoolean" value="true" <s:if test="emr3.isCorrectBoolean">checked</s:if> onchange="copyAnswer(3);"/>Yes
				<input type="radio" name="emr3.isCorrectBoolean" value="false" <s:if test="emr3.isCorrectBoolean == null || ! emr3.isCorrectBoolean">checked</s:if>/>No
			</td>
		</tr>
		<tr class="blueMain">
			<td align="right" colspan="2">Issue:</td>
			<td class="highlight"><s:select list="emrProblems" name="emr1.comment" cssClass="blueMain" /></td>
			<td><s:select list="emrProblems" name="emr2.comment" cssClass="blueMain" /></td>
			<td class="highlight"><s:select list="emrProblems" name="emr3.comment" cssClass="blueMain" /></td>
		</tr>
		<tr class="blueMain">
			<td colspan="5" align="center"><input class="blueMain"
				type="submit" value="Save" /></td>
		</tr>
	</table>
</s:form>

<br /><br /><br />
<s:form>
	<table width="600" border="0">
		<tr bgcolor="#003366" class="whiteTitle">
			<td colspan="3">Summary:</td>
		</tr>
		<tr valign="top" class="blueMain">
			<td><input type="button" value="Send Reminder Email"
				onclick="sendEmail();" class="blueMain" /> <br />
			to: <s:property value="conAudit.contractorAccount.email" /> <s:property
				value="conAudit.contractorAccount.secondEmail" />

			<div id="emailStatus" style="font-style: italic; color: red;"></div>
			</td>
			<td>Followup<br />
			<select class="blueMain" name="followUp"
				id="followUpInterval" onchange="saveFollowup();">
				<option value="0" selected="selected">- Follow up -</option>
				<option value="1">1 day</option>
				<option value="3">3 days</option>
				<option value="7">1 week</option>
				<option value="14">2 weeks</option>
				<option value="30">1 month</option>
			</select>
			<td>Date
			<div id="scheduleDate"><s:date name="conAudit.scheduledDate"
				format="MM/dd" /></div>
			</td>
		</tr>
		<tr bgcolor="#003366" class="whiteTitle">
			<td colspan="3">Contractor Notes:</td>
		</tr>
		<tr class="blueMain">
			<td colspan="3"><s:property value="contractorNotes"
				escape="false" /><br />
			<a href="add_notes.jsp?id=<s:property value="id" />">...show all
			notes</a></td>
		</tr>
	</table>
</s:form>

</body>
</html>
