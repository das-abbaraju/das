<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="!hasVerifiableAudits">
	<div class="alert">No Audits found to verify</div>
</s:if>
<s:else>
	<s:form id="verify">
		<table class="summary">
			<s:if test="pqfQuestions.size > 0">
				<s:iterator value="pqfQuestions.values">
					<thead>
						<tr>
							<td>PQF</td>
							<td>Answer</td>
						</tr>
					</thead>
					<tr>
						<td><s:property value="question.category.name" />/<s:text name="question.name" /></td>
						<s:if test="verified">
							<td class="center" style="color: #006400; font-weight: bold;"><s:property
								value="answer" /></td>
						</s:if>
						<s:else>
							<td class="center"><s:property value="answer" /></td>
						</s:else>
					</tr>
				</s:iterator>
			</s:if>
			<s:iterator value="verificationAudits">
				<s:if test="auditType.Pqf && (hasCaoStatus('Submitted') || hasCaoStatus('Resubmitted'))">
					<s:if test="pqfQuestions.size == 0">
						<thead>
							<tr>
								<th class="center" colspan="2">PQF</th>
							</tr>
						</thead>
					</s:if>
					<tr>
						<td class="center" colspan="2">
							<input type="button" id="verifyaudit" value="Verify <s:property value="auditType.name"/>" onclick="showAudit(<s:property value="id"/>); return false;"/>
						</td>
					</tr>	
				</s:if>
			</s:iterator>
		</table>
			
		<br/>
		<table class="summary">
			<s:if test="oshasUS.size > 0">
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
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:set name="fileId" value="getFileUploadId('OSHA')" />	
									
									<s:if test="#fileId != null" >
										<a href="#" onClick="openOsha(<s:property value="#audit.id" />,<s:property value="#fileId" />)">
											Show File
										</a>
									</s:if>
									<s:else>
										No File
									</s:else>
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>				
				<tr>
					<td>Man Hours Worked:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'Hours')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Fatalities:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'Fatalities')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Work Cases:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'DaysAwayCases')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Number of Lost Workdays:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'DaysAway')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>Restricted Work Cases:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'JobTransfersCases')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
				<td>Number of On Job Transfer OR Restricted Days:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'JobTransferDays')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
				<td>Other Recordable Cases:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'OtherRecordables')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>				
				<tr>
					<td>TRIR:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'TrirAbsolute')" />
								</s:if>
							</s:iterator>
						</td>
					</s:iterator>
				</tr>
				<tr>
					<td>LWCR:</td>
					<s:iterator value="annualUpdates" id="audit">
						<td class="center">
							<s:iterator value="oshasUS">
								<s:if test="auditFor.equals(#audit.auditFor)">
									<s:property value="getSpecificRate('OSHA', 'LwcrAbsolute')" />
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
									<s:set name="answer" value="value.answer" />
									<s:if test="value.answer.equals('Audit.missingParameter')" >
										<s:set name="answer" value="0" />
									</s:if>
									<s:if test="value.audit.equals(#year)">
										<s:if test="value.verified">
											<span style="color: #006400; font-weight: bold;">
												<s:property value="#answer" />
											</span>
										</s:if>
										<s:else>
											<s:property value="#answer" />
										</s:else>
									</s:if>
								</s:iterator>
							</td>
						</s:iterator>
					</tr>
				</s:iterator>
			</s:if>
			
			<s:if test="oshasUS.size > 0 || emrs.size > 0">
				<tr>
					<td></td>
					<s:iterator value="annualUpdates">
						<td class="center">
							<s:if test="hasCaoStatus('Submitted') || hasCaoStatus('Resubmitted')">
                                <input type="button" value="Verify" class="verifyAudit" data-id="<s:property value="id" />" />
							</s:if>
						</td>
					</s:iterator>
				</tr>
			</s:if>
		</table>
	</s:form>
</s:else>

