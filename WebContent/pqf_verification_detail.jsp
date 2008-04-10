<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>PQF Verification for <s:property value="conAudit.contractorAccount.name" /></title>
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
	alert("Successfully moved " + type+ year);
}

function copyAnswer(selectedYear) {
	// If the answer is correct and hasn't been filled in yet, then default it
	if ($F('verify_emr'+selectedYear+'_isCorrectYes') == null) return;
	
	var answer = $('verify')['verify_emr'+selectedYear+'_verifiedAnswer'];
	if (answer.present()) return;
	
	var original = $('verify')['verify_emr'+selectedYear+'_answer'];
	if (original.present())
		$(answer).value = $F(original);
}
</script>
</head>
<body>
<h1>PQF Verification for <s:property value="conAudit.contractorAccount.name" /></h1>

<p class="blueMain"><a href="pqf_verification.jsp">Return to
List</a> | <a href="contractor_detail.jsp?id=<s:property value="conAudit.contractorAccount.id" />">Contractor
Details</a></p>

<s:form id="verify">

<table border="0" cellpadding="1" cellspacing="1" align="center">
	<s:if test="oshaID > 0">
		<tr bgcolor="#003366" class="whiteTitle" align="center">
			<td colspan="2" align="left">OSHA</td>
			<td><s:property value="year1" /> </td>
			<td><s:property value="year2" /> </td>
			<td><s:property value="year3" /> </td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Exempt:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:checkbox name="osha.na1" /></td>
			<td><s:checkbox name="osha.na2" /></td>
			<td class="highlight"><s:checkbox name="osha.na3" /></td>
		</tr>
		<tr class="blueMain" align="center" valign="top">
			<td align="right"><a
				href="pqf_OSHA.jsp?action=Edit&catID=29&oID=<s:property value="oshaID" />&id=<s:property value="id" />#upload"
				class="blueMain">Upload New Files</a></td>
			<td>&nbsp;</td>
			<td class="highlight">
			<s:if test="osha.file1yearAgo">
			<a href="#" onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha1','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;" onMouseOver="status='Osha 300 Logs'">Show File</a>
			<br />Switch with: <select id="moveOsha07" onchange="moveFile('osha', 1)"><option></option><option>2006</option><option>2005</option></select>
			</s:if>
			<s:else>No File</s:else>
			<td>
						<s:if test="osha.file2yearAgo">
			<a href="#" onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha2','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;" onMouseOver="status='Osha 300 Logs'">Show File</a>
			</s:if>
			<s:else>No File</s:else>
			</td>
			<td class="highlight">			<s:if test="osha.file3yearAgo">
			<a href="#" onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha3','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;" onMouseOver="status='Osha 300 Logs'">Show File</a>
			</s:if>
			<s:else>No File</s:else>
</td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Man Hours Worked:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:textfield name="osha.manHours1"
				cssClass="oshanum" /></td>
			<td><s:textfield name="osha.manHours2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.manHours3"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Fatalities:</td>
			<td align="center">G</td>
			<td class="highlight"><s:textfield name="osha.fatalities1"
				cssClass="oshanum" /></td>
			<td><s:textfield name="osha.fatalities2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.fatalities3"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Lost Work Cases:</td>
			<td align="center">H</td>
			<td class="highlight"><s:textfield name="osha.lostWorkCases1"
				cssClass="oshanum" /></td>
			<td><s:textfield name="osha.lostWorkCases2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.lostWorkCases3"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Lost Workdays:</td>
			<td>K(L)</td>
			<td class="highlight"><s:textfield name="osha.lostWorkDays1"
				cssClass="oshanum" /></td>
			<td><s:textfield name="osha.lostWorkDays2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.lostWorkDays3"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Injury &amp; Illnesses Medical Cases:</td>
			<td align="center">J</td>
			<td class="highlight"><s:textfield
				name="osha.injuryIllnessCases1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.injuryIllnessCases2"
				cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield
				name="osha.injuryIllnessCases3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Restricted Work Cases:</td>
			<td align="center">I</td>
			<td class="highlight"><s:textfield
				name="osha.restrictedWorkCases1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.restrictedWorkCases2"
				cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield
				name="osha.restrictedWorkCases3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Total Injuries and Illnesses:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:textfield name="osha.recordableTotal1"
				cssClass="oshanum" /></td>
			<td><s:textfield name="osha.recordableTotal2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.recordableTotal3"
				cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Is Correct:</td>
			<td>&nbsp;</td>
			<td class="highlight" style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="osha.verified1" /> </td>
			<td style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="osha.verified2" /></td>
			<td class="highlight" style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="osha.verified3" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Issue:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:select list="oshaProblems" headerValue="" value="osha.comment1" cssClass="blueMain" /></td>
			<td><s:select list="oshaProblems" headerValue="" value="osha.comment2" cssClass="blueMain" /></td>
			<td class="highlight"><s:select list="oshaProblems" headerValue="" value="osha.comment3" cssClass="blueMain" /></td>
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
		<td><s:property value="year1" /> </td>
		<td><s:property value="year2" /> </td>
		<td><s:property value="year3" /> </td>
	</tr>
	<tr class="blueMain" align="center" valign="top">
		<td colspan="2"><a href="pqf_uploadFile.jsp?auditType=PQF&catID=10&id=<s:property value="id" />"
		class="blueMain">Upload New Files</a></td>
		<td class="highlight"><a href="#"
		onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr1.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show File</a>
		</td>
		<td class="highlight"><a href="#"
		onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr2.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show File</a>
		</td>
		<td class="highlight"><a href="#"
		onclick="window.open('servlet/showpdf?id=<s:property value="id" />&file=pqfpdf<s:property value="emr3.question.questionID" />','','scrollbars=yes,resizable=yes,width=700,height=450'); return false;">Show File</a>
		</td>
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
		<td class="highlight" style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="emr1.isCorrect" onchange="copyAnswer(1);" /> </td>
		<td style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="emr2.isCorrect" onchange="copyAnswer(2);" /></td>
		<td class="highlight" style="font-size: 14px; font-weight: bolder;"><s:radio list="yesNos" name="emr3.isCorrect" onchange="copyAnswer(3);" /></td>
	</tr>
	<tr class="blueMain">
		<td align="right" colspan="2">Issue:</td>
		<td class="highlight"><s:select list="emrProblems" headerValue="" value="emr1.comment" cssClass="blueMain" /></td>
		<td><s:select list="emrProblems" headerValue="" value="emr2.comment" cssClass="blueMain" /></td>
		<td class="highlight"><s:select list="emrProblems" headerValue="" value="emr3.comment" cssClass="blueMain" /></td>
	</tr>

	<tr bgcolor="#003366" class="whiteTitle" align="center">
		<td colspan="2" align="left">Summary</td>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr class="blueMain">
		<td colspan="5"></td>
	</tr>
</table>
</s:form>


<s:form action="VerifySaveFollowUp" method="POST">
<table>
<tr valign="top">
<td>
</td>
<td class="blueMain">
<input class="blueMain" type="submit" value="Followup in" />
<select class="blueMain">
	<option value="1">1 day</option>
	<option value="3" selected="selected">3 days</option>
	<option value="7">1 week</option>
	<option value="14">2 weeks</option>
	<option value="30">1 month</option>
</select></td>
</tr>
</table>
</s:form>

<div id="notes" style="width: 600px; text-align: left;">
<h3 class="blueHeader">Notes:</h3>
<p class="blueMain"><s:property value="contractorNotes" escape="false" /><br/>
<a href="add_notes.jsp?id=<s:property value="id" />">...show all notes</a>
</p>
</body>
</html>
