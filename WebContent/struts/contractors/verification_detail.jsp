<%@ taglib prefix="s" uri="/struts-tags"%>

<s:form id="verify">

	<table class="summary">
		<s:if test="pqfQuestions.size() > 0 ">
			<thead>
				<tr><td colspan="3">PQF</td>
				<td>Answer</td>
				</tr>
			</thead>
			<s:iterator value="pqfQuestions.values">
				<tr>
					<td colspan="3" ><s:property value="question.subCategory.subCategory" />/<s:property
						value="question.question" />
					</td>
					<s:if test="value.verified">
						<td style="color : #006400;font-weight: bold;"><s:property value="verifiedAnswerOrAnswer" />
						</td>	
					</s:if>	
					<s:else>
						<td><s:property value="verifiedAnswerOrAnswer" />
						</td>
					</s:else>
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
					<td><s:property value="applicable" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Upload New Files</td>
				<s:iterator value="oshas">	
				<td><s:if
					test="file && file.name().equals('Yes')">
					<a href="#"
						onClick="openOsha(<s:property value="id"/>)">Show File</a>
				</s:if> <s:else>No File</s:else></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Man Hours Worked:</td>
				<s:iterator value="oshas">
				<td><s:property value="manHours"/></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Fatalities:</td>
				<s:iterator value="oshas">
				<td><s:property value="fatalities"/></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Lost Work Cases:</td>
				<s:iterator value="oshas">
				<td><s:property value="lostWorkCases" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Number of Lost Workdays:</td>
				<s:iterator value="oshas">
				<td><s:property value="lostWorkDays" /></td>
				</s:iterator>	
			</tr>
			<tr>
				<td>Injury &amp; Illnesses Medical Cases:</td>
				<s:iterator value="oshas">
				<td><s:property value="injuryIllnessCases"/></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Restricted Work Cases:</td>
				<s:iterator value="oshas">
				<td><s:property value="restrictedWorkCases" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Total Injuries and Illnesses:</td>
				<s:iterator value="oshas">
				<td><s:property value="recordableTotal" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Is Correct:</td>
				<s:iterator value="oshas">
					<s:if test="verified">
						<td style="color : #006400;font-weight: bold;"><s:property value="verified" /></td>
					</s:if>
					<s:else>
						<td><s:property value="verified" /></td>
					</s:else>
				
				</s:iterator>
			</tr>
			<tr>
				<td>Verified Date:</td>
				<s:iterator value="oshas">
				<td><s:date name="verifiedDate"
					format="MM/dd/yyyy" /></td>
				</s:iterator>
			</tr>
			<tr>
				<td>Issue:</td>
				<s:iterator value="oshas">
				<td><s:property value="comment" /></td>
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
		
		<s:if test="emrs.size() > 0">
			<thead>
			<tr>
				<td>EMR</td>
				<s:iterator value="years">
					<td><s:property/></td>
				</s:iterator>
			</tr>
			</thead>
			<s:iterator value="emrs.entrySet()">
				<tr>
					<td><s:property value="key.question" /></td>
					<s:iterator value="value">
						<s:if test="value.verified">
							<td style="color : #006400;font-weight: bold;"><s:property value="value.verifiedAnswerOrAnswer" /></td>
						</s:if>
						<s:else>
						<td><s:property value="value.verifiedAnswerOrAnswer" /></td>
						</s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</s:if>
		
	</table>

</s:form>

<div>
<s:iterator value="contractor.audits">
	<s:if test="auditStatus.pendingSubmitted && (auditType.Pqf || auditType.annualAddendum)">
		[<a href="#" onclick="showAudit(<s:property value="id"/>); return false;"><s:property value="auditFor"/> <s:property value="auditType.auditName"/></a>]
	</s:if>
</s:iterator>
</div>

<div id="verification_audit"></div>
