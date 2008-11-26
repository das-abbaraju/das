<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="verify">

	<table class="report">
		<s:if test="pqfQuestions.size() > 0 ">
			<tr bgcolor="#003366" class="whiteTitle" align="center">
				PQF
			</tr>
			<tr>
				<td>Question</td>
				<td></td>
			</tr>
			<s:iterator value="pqfQuestions.values">
				<tr class="blueMain">
					<td><s:property value="question.subCategory.subCategory" />/<s:property
						value="question.question" /> <s:if
						test="question.questionType == 'License'">
						<s:set name="checkLicenseQuestion" value="question.question" />
						<s:set name="checkLicenseAnswer" value="answer" />
						<input type="image" name="CheckLicense"
							src="images/checklicense.gif" alt='Check License'
							onclick="javascript: document.getElementById('form1').submit(); return false;" />
					</s:if></td>
				</tr>
			</s:iterator>
		</s:if>

		<s:if test="oshas.size() > 0">
			<thead>
			<tr>
				<td>OSHA</td>
				<s:iterator value="oshas">
					<td><s:property value="conAudit.auditFor" /></td>
				</s:iterator>
			</tr>
			</thead>

			<tr>
				<td>Applicable:</td>
				<s:iterator value="oshas">
					<td><s:checkbox name="applicable" value="applicable" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td><a
					href="AuditCat.action?auditID=<s:property value="#oshaemr.id" />&catDataID=151&mode=Edit"
					class="blueMain">Upload New Files</a></td>
				<s:iterator value="oshas">	
				<td><s:if
					test="file && file.name().equals('Yes')">
					<a href="#"
						onClick="window.open('servlet/showpdf?id=<s:property value="id" />&OID=<s:property value="oshaID" />&file=osha','Osha300Logs','scrollbars=yes,resizable=yes,width=700,height=450'); return false;"
						onMouseOver="status='Osha 300 Logs'">Show File</a>
				</s:if> <s:else>No File</s:else></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Man Hours Worked:</td>
				<s:iterator value="oshas">
				<td><s:textfield name="manHours"
					cssClass="oshanum" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Fatalities:</td>
				<s:iterator value="oshas">
				<td><s:textfield name="fatalities"
					cssClass="oshanum" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Lost Work Cases:</td>
				<s:iterator value="oshas">
				<td><s:textfield name="lostWorkCases"
					cssClass="oshanum" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Lost Workdays:</td>
				<s:iterator value="oshas">
				<td><s:textfield name="lostWorkDays"
					cssClass="oshanum" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Injury &amp; Illnesses Medical Cases:</td>
				<s:iterator value="oshas">
				<td><s:textfield
					name="injuryIllnessCases" cssClass="oshanum" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Restricted Work Cases:</td>
				<s:iterator value="oshas">
				<td><s:textfield
					name="restrictedWorkCases" cssClass="oshanum" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Total Injuries and Illnesses:</td>
				<s:iterator value="oshas">
				<td class="highlight"><s:textfield name="recordableTotal"
					cssClass="oshanum" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td align="right">Is Correct:</td>
				<s:iterator value="oshas">
				<td class="highlight" style="font-size: 14px; font-weight: bolder;"><input
					type="radio" name="verified" value="true"
					<s:if test="verified">checked</s:if> />Yes <input
					type="radio" name="verified" value="false"
					<s:if test="!verified">checked</s:if> />No</td>
				</s:iterator>
			</tr>
			<tr>
				<td align="right">Verified Date:</td>
				<s:iterator value="oshas">
				<td class="highlight"><s:date name="verifiedDate"
					format="MM/dd/yyyy" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td align="right">Issue:</td>
				<s:iterator value="oshas">
				<td class="highlight"><s:select list="oshaProblems"
					name="comment" cssClass="blueMain" /></td>
				</s:iterator>
			</tr>
		</s:if>
		<s:else>
			<tr>
				<td>
				<div class="error">Your OSHA is missing.Your PQF has no
				Corporate record or has more than one Corporate record for OSHA. <br />
				Please go to your PQF and resolve this Issue.</div>
				</td>
			</tr>
		</s:else>
		
		<s:if test="emrQuestions.size() > 0">
			<thead>
			<tr>
				<td>EMR</td>
				<s:iterator value="emrQuestions">
					<td><s:property value="audit.auditFor" /></td>
				</s:iterator>
			</tr>
			</thead>
			<s:iterator value="emrQuestions">
				<tr>
					<td><s:property value="question.question" /></td>
					<td><s:property value="answer" /></td>
				</tr>
			</s:iterator>

			
		
		</s:if>
		
	</table>

</s:form>
