<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="verificationAudits.size() == 0">
	<div id="alert">No Audits found to verify</div>
</s:if>
<s:else>
	<s:form id="verify">
		<table class="summary">
			<s:if test="pqfQuestions.size() > 0 ">
				<thead>
					<tr>
						<td colspan="3">PQF</td>
						<td>Answer</td>
					</tr>
				</thead>
				<s:iterator value="pqfQuestions.values">
					<tr>
						<td colspan="3"><s:property
							value="question.subCategory.subCategory" />/<s:property
							value="question.question" /></td>
						<s:if test="value.verified">
							<td class="center"  style="color: #006400; font-weight: bold;"><s:property
								value="answer" /></td>
						</s:if>
						<s:else>
							<td class="center"><s:property value="answer" /></td>
						</s:else>
					</tr>
				</s:iterator>
				<tr>
					<td colspan="3"></td>
					<s:iterator value="verificationAudits">
						<s:if test="auditType.Pqf">
							<td class="center">
								<input type="button" value="Verify" onclick="showAudit(<s:property value="id"/>); return false;"/>
							</td>
						</s:if>
					</s:iterator>
				</tr>
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
						<td class="center"><s:property value="applicable" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Upload New Files</td>
					<s:iterator value="oshas">
						<td class="center"><s:if test="file && file.name().equals('Yes')">
							<a href="#" onClick="openOsha(<s:property value="id"/>)">Show
							File</a>
						</s:if> <s:else>No File</s:else></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Man Hours Worked:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="manHours" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Fatalities:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="fatalities" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Work Cases:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="lostWorkCases" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Workdays:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="lostWorkDays" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Injury &amp; Illnesses Medical Cases:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="injuryIllnessCases" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Restricted Work Cases:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="restrictedWorkCases" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Total Injuries and Illnesses:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="recordableTotal" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Is Correct:</td>
					<s:iterator value="oshas">
						<s:if test="verified">
							<td class="center" style="color: #006400; font-weight: bold;"><s:property
								value="verified" /></td>
						</s:if>
						<s:else>
							<td class="center"><s:property value="verified" /></td>
						</s:else>

					</s:iterator>
				</tr>
				<tr>
					<td>Verified Date:</td>
					<s:iterator value="oshas">
						<td class="center"><s:date name="verifiedDate" format="MM/dd/yyyy" /></td>
					</s:iterator>
				</tr>
				<tr>
					<td>Issue:</td>
					<s:iterator value="oshas">
						<td class="center"><s:property value="comment" /></td>
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
							<td><s:property /></td>
						</s:iterator>
					</tr>
				</thead>
				<s:iterator value="emrs.entrySet()">
					<tr>
						<td><s:property value="key.question" /></td>
						<s:iterator value="value">
							<s:if test="value.verified">
								<td class="center" style="color: #006400; font-weight: bold;"><s:property
									value="value.answer" /></td>
							</s:if>
							<s:else>
								<td class="center"><s:property value="value.answer" /></td>
							</s:else>
						</s:iterator>
					</tr>
				</s:iterator>
				<tr>
					<td></td>
					<s:iterator value="verificationAudits">
						<s:if test="auditType.annualAddendum">
							<td class="center">
								<s:property value="auditFor"/>
								<input type="button" value="Verify" onclick="showAudit(<s:property value="id"/>); return false;"/>
							</td>	
						</s:if>
					</s:iterator>
				</tr>
			</s:if>
		</table>
	</s:form>
</s:else>

