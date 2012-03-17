<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="showApproveButton" value="true" />
<s:set name="fullyVerified" value="true" />

<s:iterator value="conAudit.operatorsVisible" id="cao">
	<s:if test="!#cao.status.submittedResubmitted">
		<s:set name="showApproveButton" value="false" />
	</s:if>

	<s:if test="#cao.percentVerified < 100" >
		<s:set name="fullyVerified" value="false" />
	</s:if>
</s:iterator>

<s:if test="(#showApproveButton && pqfQuestions.size == 0) || #fullyVerified">
	<s:set name="showApproveButton" value="'inline'"/>
</s:if>
<s:else>
	<s:set name="showApproveButton" value="'none'"/>
</s:else>

<h2><s:property value="conAudit.auditFor" /> <s:property value="conAudit.auditType.name" /></h2>

<br clear="all" />

<s:if test="caos.keySet().size > 1">
	<div>
		<button class="picsbutton negative" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Incomplete','Reject All',<s:property value="allCaoIDs" />);">Reject All</button>
		<button style="display: <s:property value="#showApproveButton" />" class="picsbutton positive approveButton" name="button" onclick="return changeAuditStatus(<s:property value="conAudit.id"/>,'Complete','Complete All',<s:property value="allCaoIDs" />);">Complete All</button>
	</div>
</s:if>

<form>
	<s:if test="conAudit.auditType.pqf">
		<s:if test="pqfQuestions.size > 0">
			<fieldset class="form" style="clear: none; float: left; width: 50%; margin: 0.5em;">
				<h2 class="formLegend">PQF Questions</h2>
				
				<s:iterator value="pqfQuestions">
					<ol>
						<li>
							<s:property value="question.category.name"/>
							<br />
							<s:property value="question.expandedNumber"/>
							<s:property value="question.name"/>
						</li>
					 
						<s:if test="question.questionType != 'File'">
							<li>
								<label>Answer:</label>			
								<s:textfield id="answer_%{question.id}" name="answer"/>
							</li>
						</s:if>
						<s:else>
							<li>
								<label>File:</label> 
								<s:if test="answer.length() > 0">
									<a 
										href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&auditData.question.id=<s:property value="question.id"/>" 
										target="_BLANK"
									>View File</a>
									<a 
										href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=33&mode=Edit" 
										target="_BLANK"
									>Change File</a>
								</s:if>
								<s:else>
									None.
									<a
										href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=33&mode=Edit"
										target="_BLANK"
									>Upload New Files</a>
								</s:else>
							</li>
						</s:else>
						
						<s:if test="verified == false">
							<s:set name="verifyText" value="'Verify'"/>
						</s:if>
						<s:else>
							<s:set name="verifyText" value="'Unverify'"/>
						</s:else>
			
						<li>
							<input 
								id="verify_<s:property value="question.id"/>" 
								type="button" 
								onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="id"/>, <s:property value="question.category.id" />);"	
								value="<s:property value="#attr.verifyText"/>"
							/>
							
							<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div>
						</li>
						
						<s:if test="verified">
							<s:set name="displayVerified" value="'block'"/>
						</s:if>
						<s:else>
							<s:set name="displayVerified" value="'none'"/>
						</s:else>
						
						<li id="verified_<s:property value="question.id"/>" style="display: <s:property value="#attr.displayVerified"/>;">
							<label>Verified:</label>
							<s:div cssStyle="display:inline;" id="verify_details_%{question.id}">
								<s:date name="dateVerified" format="MM/dd/yyyy" /> by <s:property value="auditor.name"/>
							</s:div>
						</li>
						<li>
							<label>Comment:</label>
							<s:textfield 
								onblur="return setComment( %{conAudit.id}, %{question.id}, %{id}, %{question.category.id} );" 
								id="comment_%{question.id}" 
								name="comment" 
							/>
						</li>
						
						<s:if test="question.questionType == 'License'">
							<li>
								<s:property value="@com.picsauditing.util.Constants@displayStateLink(question.question, answer)" escape="false" />
							</li>
						</s:if>
						
						<li>
							<hr>
						</li>
					</ol>
					
					<s:div id="qid_%{question.id}"></s:div>
				</s:iterator>
			</fieldset>
		</s:if>
	</s:if>
	<s:else>
		<fieldset class="form">
			<h2 class="formLegend">Audit Questions</h2>
			
			<s:iterator value="applicableAuditData">
				<s:if test="isShowQuestionToVerify(question, answered)">
					<div id="qid_${question.id}">
						<ol>
							<li>
								<strong><s:property value="question.category.name"/></strong>
								<br />
								<s:property value="question.expandedNumber"/>
								<s:property value="question.name" escape="false" />
								<br/>
								
								<s:if test="question.id == 3563 || question.id == 3565 || question.id == 3566">
									<a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="answer"/>" target="_BLANK" title="opens in new window">OSHA Citations</a>
								</s:if>
							</li>
				
							<li>
								<s:if test="question.questionType != 'File'">
									<label>Answer:</label>			
									<s:textfield id="answer_%{question.id}" name="answer"/>
								</s:if>
								<s:else>
									<label>File:</label> 
									
									<s:if test="answer.length() > 0">
										<a 
											href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&auditData.question.id=<s:property value="question.id"/>" 
											target="_BLANK"
										>View File</a>
										<a 
											href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=152&mode=Edit" 
											target="_BLANK"
										>Change File</a>
									</s:if>
									<s:else>
										None.
										<a
											href="Audit.action?auditID=<s:property value="conAudit.id" />&catID=152&mode=Edit"
											target="_BLANK"
										>Upload New Files</a>
									</s:else>
								</s:else>
							</li>
							
							<s:if test="verified == false">
								<s:set name="verifyText" value="'Verify'"/>
							</s:if>
							<s:else>
								<s:set name="verifyText" value="'Unverify'"/>
							</s:else>
							
							<li>
								<input 
									id="verify_<s:property value="question.id"/>" 
									type="button" 
									onclick="return toggleVerify(<s:property value="conAudit.id"/>, <s:property value="question.id"/>, <s:property value="id"/>, <s:property value="question.category.id" />);"	
									value="<s:property value="#attr.verifyText"/>"
								/>
								
								<s:div cssStyle="display : inline;" id="status_%{question.id}"></s:div>
							</li>
							
							<s:if test="verified">
								<s:set name="displayVerified" value="'block'"/>
							</s:if>
							<s:else>
								<s:set name="displayVerified" value="'none'"/>
							</s:else>
							
							<li id="verified_<s:property value="question.id"/>" style="display: <s:property value="#attr.displayVerified"/>;">
								<label>Verified:</label>
								<s:div cssStyle="display:inline;" id="verify_details_%{question.id}">
									<s:date name="dateVerified" format="MM/dd/yyyy" /> by <s:property value="auditor.name"/>
								</s:div>
							</li>
							<li>
								<label>Comment:</label> 
								
								<s:if test="question.category.parent.id == 152">
									<s:select 
										onchange="return setComment(%{conAudit.id}, %{question.id}, %{id}, %{question.category.id} );" 
										id="comment_%{question.id}" 
										list="emrProblems" 
										name="comment" 
									/>
								</s:if>
								<s:else>
									<nobr>
										<s:if test="question.id == 2033">
											<s:select id="emrExempt" list="emrExemptReason" headerKey="" headerValue="- Issues -" onchange="copyComment('emrExempt','comment_%{question.id}');"/>
										</s:if>
										<s:elseif test="question.id == 2064">
											<s:select id="oshaExempt" list="oshaExemptReason" headerKey="" headerValue="- Issues -" onchange="copyComment('oshaExempt','comment_%{question.id}');"/>
										</s:elseif>
										<s:elseif test="question.id == 2037">
											<s:select id="emrFileExempt" list="emrProblems" headerKey="" headerValue="- Issues -" onchange="copyComment('emrFileExempt','comment_%{question.id}');"/>
										</s:elseif>
										
										<s:textfield 
											onblur="return setComment( %{conAudit.id}, %{question.id}, %{id}, %{question.category.id} );" 
											id="comment_%{question.id}" 
											name="comment" 
										/>
									</nobr>
								</s:else>
							</li>
							<li>
							<hr>
							</li>
						</ol>
					</div>
				</s:if>
			</s:iterator>
		</fieldset>
		
		<s:if test="osha != null">
			<fieldset class="form">
				<h2 class="formLegend">OSHA</h2>
					<s:if test="!osha.verified">
						<s:set name="verifyText" value="'Verify'"/>
					</s:if>
					<s:else>
						<s:set name="verifyText" value="'Unverify'"/>
					</s:else>
					
					<input 
						id="verify_<s:property value="osha.id"/>" 
						name="verify" 
						type="button" 
						onclick="return toggleOSHAVerify(<s:property value="osha.id"/>,<s:property value="osha.conAudit.id"/>);" 
						value="<s:property value="#attr.verifyText"/>"
					/>				
				
				<s:div id="oid_%{osha.id}">
					<ol>
					<s:iterator value="osha.getQuestionsToVerify('OSHA')" id="auditData">
						<li>
							<label><s:property value="#auditData.question.name" escape="false"/></label>
							<s:textfield name="#auditData.answer" cssClass="oshanum" />
						</li>	
					</s:iterator>	
					</ol>
				</s:div>
			</fieldset>
		</s:if>
	</s:else>
</form>

<br clear="all" />

<div id="caoActionArea">
	<s:include value="verification_audit_caos.jsp"/>
</div>