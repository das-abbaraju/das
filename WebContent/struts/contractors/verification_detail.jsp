<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="verificationAudits.size() == 0">
	<div class="alert">No Audits found to verify</div>
</s:if>
<s:else>
	<s:form id="verify">
		<table class="summary">
			<s:if test="pqfQuestions.size() > 0 ">
				<s:iterator value="pqfQuestions.values">
					<thead>
						<tr>
							<td>PQF</td>
							<td>Answer</td>
						</tr>
					</thead>
					<tr>
						<td><s:property value="question.category.name" />/<s:property
							value="question.name" /></td>
						<s:if test="verified">
							<td class="center" style="color: #006400; font-weight: bold;"><s:property
								value="answer" /></td>
						</s:if>
						<s:else>
							<td class="center"><s:property value="answer" /></td>
						</s:else>
					</tr>
				</s:iterator>
				<s:iterator value="verificationAudits">
					<s:if test="auditType.Pqf && (hasCaoStatus('Submitted') || hasCaoStatus('Resubmitted'))">
						<tr>
							<td class="center" colspan="2">
								<input type="button" id="verifyaudit" value="Verify <s:property value="auditType.auditName"/>" onclick="showAudit(<s:property value="id"/>); return false;"/>
							</td>
						</tr>	
					</s:if>
				</s:iterator>
			</s:if>
		</table>
			
		<br/>
		<table class="summary">
			<s:if test="oshas.size() > 0">
				<thead>
					<tr>
						<td>OSHA</td>
						<s:iterator value="annualUpdates">
							<td><s:property value="auditFor" /></td>
						</s:iterator>
					</tr>
				</thead>

				<tr>
					<td>Upload New Files</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:if test="fileUploaded"><a href="#" onClick="openOsha(<s:property value="id"/>)">Show File</a></s:if>
									<s:else>No File</s:else>
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Man Hours Worked:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="manHours" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Fatalities:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="fatalities" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Work Cases:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="lostWorkCases" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Workdays:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="lostWorkDays" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Injury &amp; Illnesses Medical Cases:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="injuryIllnessCases" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Restricted Work Cases:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="restrictedWorkCases" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of On Job Transfer OR Restricted Days</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="modifiedWorkDay" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Total Injuries and Illnesses:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="recordableTotal" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Is Correct:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:if test="verified">
										<span style="color: #006400; font-weight: bold;">
											<s:property value="verified" />
										</span>
									</s:if>
									<s:else>
										<s:property value="verified" />
									</s:else>
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Verified Date:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:date name="verifiedDate" format="MM/dd/yyyy" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Issue:</td>
					<s:iterator value="annualUpdates" id="year">
						<td class="center">
							<s:iterator value="oshas">
								<s:if test="conAudit.equals(#year)">
									<s:property value="comment" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
			</s:if>

			<s:if test="emrs.size() > 0">
				<thead>
					<tr>
						<td>EMR</td>
						<s:iterator value="annualUpdates">
							<td><s:property value="auditFor" /></td>
						</s:iterator>
					</tr>
				</thead>
				<s:iterator value="emrs.entrySet()">
					<tr>
						<td><s:property value="key.name" escape="false" /></td>
						<s:iterator value="annualUpdates" id="year">
							<td class="center">
								<s:iterator value="value">
									<s:if test="value.audit.equals(#year)">
										<s:if test="value.verified">
											<span style="color: #006400; font-weight: bold;">
												<s:property value="value.answer" />
											</span>
										</s:if>
										<s:else>
											<s:property value="value.answer" />
										</s:else>
									</s:if>
								</s:iterator>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
				<tr>
					<td></td>
					<s:iterator value="annualUpdates">
						<td class="center">
							<s:if test="hasCaoStatus('Submitted') || hasCaoStatus('Resubmitted')">
								<input type="button" value="Verify" onclick="showAudit(<s:property value="id"/>); return false;"/>
							</s:if>
						</td>	
					</s:iterator>
				</tr>
			</s:if>
		</table>
	</s:form>
</s:else>

