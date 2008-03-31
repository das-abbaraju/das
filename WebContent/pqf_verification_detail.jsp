<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>PQF Verification for <s:property value="contractor.name" /></title>
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
.upload {
	display: none;
}

</style>
</head>
<body>
<h1>PQF Verification for <s:property value="contractor.name" /></h1>

<p class="blueMain">
<a href="pqf_verification.jsp">Return to List</a> |
<a href="contractor_detail.jsp?id=<s:property value="id" />">Contractor Details</a> |
<a href="pqf_editMain.jsp?auditType=PQF&id=<s:property value="id" />">Edit OSHA</a>
</p>

<table border="0" cellpadding="1" cellspacing="1" align="center">
	<s:if test="oshaID > 0">
		<tr bgcolor="#003366" class="whiteTitle" align="center">
			<td colspan="2" align="left">OSHA</td>
			<td>2007</td>
			<td>2006</td>
			<td>2005</td>
		</tr>
		<tr class="blueMain" align="center">
			<td colspan="2"></td>
			<td class="highlight"><a href="" target="_BLANK">View File</a> | 
			<a href="#" onclick="">Change File</a>
			<div id="" class="upload"></td>
			<td                  ><a href="" target="_BLANK">View File</a> | 
			<a href="#" onclick="">Change File</a></td>
			<td class="highlight"><a href="" target="_BLANK">View File</a> | 
			<a href="#" onclick="">Change File</a></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Man Hours Worked:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:textfield name="osha.manHours1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.manHours2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.manHours3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Fatalities:</td>
			<td align="center">G</td>
			<td class="highlight"><s:textfield name="osha.fatalities1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.fatalities2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.fatalities3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Lost Work Cases:</td>
			<td align="center">H</td>
			<td class="highlight"><s:textfield name="osha.lostWorkCases1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.lostWorkCases2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.lostWorkCases3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Number of Lost Workdays:</td>
			<td>K(L)</td>
			<td class="highlight"><s:textfield name="osha.lostWorkDays1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.lostWorkDays2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.lostWorkDays3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Injury &amp; Illnesses Medical Cases:</td>
			<td align="center">J</td>
			<td class="highlight"><s:textfield name="osha.injuryIllnessCases1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.injuryIllnessCases2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.injuryIllnessCases3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Restricted Work Cases:</td>
			<td align="center">I</td>
			<td class="highlight"><s:textfield name="osha.restrictedWorkCases1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.restrictedWorkCases2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.restrictedWorkCases3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="right">
			<td>Total Injuries and Illnesses:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:textfield name="osha.recordableTotal1" cssClass="oshanum" /></td>
			<td><s:textfield name="osha.recordableTotal2" cssClass="oshanum" /></td>
			<td class="highlight"><s:textfield name="osha.recordableTotal3" cssClass="oshanum" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Verified:</td>
			<td>&nbsp;</td>
			<td class="highlight"><input type="radio" name="osha.verified1" value="V" /></td>
			<td                  ><input type="radio" name="osha.verified2" value="Yes" /></td>
			<td class="highlight"><input type="radio" name="osha.verified3" value="Yes" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Exempt:</td>
			<td>&nbsp;</td>
			<td class="highlight"><input type="radio" name="osha.verified1" value="E" /></td>
			<td                  ><input type="radio" name="osha.verified2" value="E" /></td>
			<td class="highlight"><input type="radio" name="osha.verified3" value="E" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Problem:</td>
			<td>&nbsp;</td>
			<td class="highlight"><input type="radio" name="osha.verified1" value="P" /></td>
			<td                  ><input type="radio" name="osha.verified2" value="P" /></td>
			<td class="highlight"><input type="radio" name="osha.verified3" value="P" /></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right"></td>
			<td>&nbsp;</td>
			<td class="highlight"></td>
			<td                  ></td>
			<td class="highlight"></td>
		</tr>
		<tr class="blueMain" align="center">
			<td align="right">Additional Comments:</td>
			<td>&nbsp;</td>
			<td class="highlight"><s:textfield name="osha.comment1" cssClass="blueMain" /></td>
			<td                  ><s:textfield name="osha.comment1" cssClass="blueMain" /></td>
			<td class="highlight"><s:textfield name="osha.comment1" cssClass="blueMain" /></td>
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
		<td>2007</td>
		<td>2006</td>
		<td>2005</td>
	</tr>
	<tr class="blueMain" align="right">
		<td colspan="2">Original Answer:</td>
		<td class="highlight"><s:textfield name="emr07.answer" cssClass="oshanum" disabled="true" /></td>
		<td><s:textfield name="emr06.answer" cssClass="oshanum" disabled="true" /></td>
		<td class="highlight"><s:textfield name="emr05.answer" cssClass="oshanum" disabled="true" /></td>
	</tr>
	<tr class="blueMain" align="right">
		<td colspan="2">Verified Answer:</td>
		<td class="highlight"><s:textfield name="emr07.verifiedAnswer" cssClass="oshanum" /></td>
		<td><s:textfield name="emr06.verifiedAnswer" cssClass="oshanum" /></td>
		<td class="highlight"><s:textfield name="emr05.verifiedAnswer" cssClass="oshanum" /></td>
	</tr>
	<tr class="blueMain" align="center">
		<td align="right">Verified:</td>
		<td>&nbsp;</td>
		<td class="highlight"><input type="radio" name="emr07.verified1" value="V" /></td>
		<td                  ><input type="radio" name="emr06.verified2" value="Yes" /></td>
		<td class="highlight"><input type="radio" name="emr05.verified3" value="Yes" /></td>
	</tr>
	<tr class="blueMain" align="center">
		<td align="right">Problem:</td>
		<td>&nbsp;</td>
		<td class="highlight"><input type="radio" name="emr07.verified1" value="P" /></td>
		<td                  ><input type="radio" name="emr06.verified2" value="P" /></td>
		<td class="highlight"><input type="radio" name="emr05.verified3" value="P" /></td>
	</tr>
	<tr class="blueMain" align="center">
		<td align="right"></td>
		<td>&nbsp;</td>
		<td class="highlight"></td>
		<td                  ></td>
		<td class="highlight"></td>
	</tr>
	<tr class="blueMain">
		<td align="right" colspan="2">Additional Comments:</td>
		<td class="highlight"><s:textfield name="emr07.comment" cssClass="blueMain" /></td>
		<td                  ><s:textfield name="emr06.comment" cssClass="blueMain" /></td>
		<td class="highlight"><s:textfield name="emr05.comment" cssClass="blueMain" /></td>
	</tr>
	
	<tr bgcolor="#003366" class="whiteTitle" align="center">
		<td colspan="2" align="left">Summary</td>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr class="blueMain">
		<td colspan="5"><s:textarea name="new_note" value="" rows="3" cols="50" cssClass="blueMain" /></td>
	</tr>
</table>
</body>
</html>
