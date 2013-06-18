<%@ page language="java" errorPage="/exception_handler.jsp" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="answer">
	<s:form cssClass="qform" id="qform%{#q.id}" onsubmit="return false;">
		<s:if test="auditData.audit != null">
			<s:hidden name="auditData.audit.id" cssClass="get_request"/>
		</s:if>
		<s:else>
			<s:hidden name="auditData.audit.id" value="%{conAudit.id}" cssClass="get_request"/>
		</s:else>
		
		<s:hidden name="auditData.question.id" value="%{#q.id}" cssClass="get_request"/>
		<s:hidden name="mode" cssClass="get_request"/>

		<%-- Option types [START] --%>

		<%-- radio // select --%>
		<s:if test="#q.questionType.equals('MultipleChoice') && #q.option != null">
			<s:if test="#q.option.radio">
                <s:if test="#q.isInlineRadioGroup()">
                    <s:set var="display_inline" value="'inline'" />
                </s:if>
                <s:else>
                    <s:set var="display_inline" value="" />
                </s:else>
                
				<s:radio 
					list="#q.option.values" 
					listValue="name" 
					listKey="identifier" 
					name="auditData.answer" 
					value="%{#a.answer}"
					theme="pics"
                    cssClass="%{#display_inline}"
				/>

				<s:if test="!isStringEmpty(#a.answer)">
					<a href="#" class="reset-answer"><s:text name="global.Reset" /></a>
				</s:if>

				<s:if test="#q.auditType.policy && #q.option.uniqueCode.equals('YesNo')">
					<s:set name="op" value="%{getOperatorByName(#q.category.name)}" />
					
					<s:if test="#op != null && #op.id > 0">
						<div class="clearfix question shaded">
							<s:text name="AuditQuestion.DoesntComply"></s:text>
							
							<s:if test="#op.insuranceForms.size > 0">
								<ul style="list-style:none">
									<s:iterator value="#op.insuranceForms">
										<li>
											<a href="forms/<s:property value="file"/>" target="_BLANK" title="Opens in new Window">
												<s:property value="formName"/>
											</a>
										</li>
									</s:iterator>
								</ul>
							</s:if>
							
							<br clear="all"/>
							<div class="clear"></div>
						</div>
					</s:if>
				</s:if>
			</s:if>
			<s:else>
				<s:select list="#q.option.values" headerValue="%{getText('global.list.select.header')}" headerKey="" listValue="name" listKey="identifier" name="auditData.answer" value="%{#a.answer}" />
			</s:else>
		</s:if>

		
		<s:if test="#q.questionType == 'Check Box'">
			<s:checkbox fieldValue="X" name="auditData.answer" value="#a.answer == \"X\"" cssClass="checkbox" />
		</s:if>
		
		<s:if test="#q.questionType == 'Text Area'">
			<s:textarea name="auditData.answer" value="%{#a.answer}"></s:textarea>
		</s:if>
		
		<s:if test="#q.questionType == 'Text'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="text"/>
		</s:if>
		
		<s:if test="#q.questionType == 'Additional Insured'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="text"/>
		</s:if>
		
		<s:if test="#q.questionType == 'Date'">
			<s:textfield name="auditData.answer" value="%{#a.getAnswerInDate(@com.picsauditing.util.PicsDateFormat@Iso)}" cssClass="date text"/>
			<span style="font-style: italic; font-size: 12px;">
                <s:label value="%{@com.picsauditing.util.PicsDateFormat@Iso}"></s:label>
            </span>
		</s:if>
		
		<s:if test="#q.questionType == 'License'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="text"/>
			<s:property value="@com.picsauditing.util.Constants@displayCountrySubdivisionLink(#q.question, #a.answer)" escape="false" />
		</s:if>
		
		<s:if test="#q.questionType == 'Money' || #q.questionType == 'Number' || #q.questionType == 'Decimal Number' || #q.questionType == 'Percent'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="number text"/>
            <s:if test="#q.category.uniqueCode == 'limits' && #a.isAnswered()">
               <s:set var="insuranceCriteria" value = "getInsuranceCriteriaMap(#q)" />
                <s:if test="#insuranceCriteria.size() > 0">
                    <div class="insurance-criteria">
                        <p><s:text name="Audit.InsuranceLimit.RequiredLimits" /></p>
                        <ul>
                            <s:iterator value="#insuranceCriteria.keySet()" var="criteriaLimit" status="status">
                                <s:set var="excess_criteria" ></s:set>
                                <s:if test="#status.count > 3">
                                    <s:set var="excess_criteria">excess-criteria hide</s:set>
                                </s:if>

                                <li class="criteria-list ${excess_criteria}">
                                    <div class="icon">
                                        <s:if test = "#insuranceCriteria.get(#criteriaLimit).get(0).meetsCriteria(#a)">
                                            <i class="icon-ok icon-large"></i>
                                        </s:if>
                                        <s:else>
                                            <i class="icon-warning-sign icon-large"></i>
                                        </s:else>
                                    </div>
                                    <div class="operators">
                                        <s:set value="#insuranceCriteria.get(#criteriaLimit)" var="criteriaList" />
                                        <s:iterator value="criteriaList" var="criteria" status="anotherStatus">
                                            ${criteria.operatorAccount.name} <s:if test="#anotherStatus.count < #criteriaList.size()">,</s:if>
                                        </s:iterator>
                                    </div>
                                    <div class="limit">
                                            <s:property value="@com.picsauditing.util.Strings@formatInternationalNumber(#criteriaLimit, getPermissions().getLocale())" />
                                    </div>
                                </li>
                            </s:iterator>
                        </ul>
                        <s:if test="#insuranceCriteria.size() > 3">
                            <button class="btn toggle-excess-criteria"><s:text name="Audit.InsuranceLimit.ShowAll" /></button>
                        </s:if>
                    </div>
                </s:if>
            </s:if>
	</s:if>

		<s:if test="#q.questionType == 'AMBest'">
			<s:hidden name="auditData.comment" value="%{#a.comment}"/>
			<s:textfield id="ambest" name="auditData.answer" value="%{#a.answer}" cssClass="text"/>

			<s:if test="#a.commentLength">
				<s:set name="ambest" value="@com.picsauditing.dao.AmBestDAO@getAmBest(#a.comment)" />
				<br />
				
				<s:text name="AmBest.NAIC" /><s:property value="#a.comment" />
				<s:if test="#ambest.amBestId > 0">
					<s:text name="AuditQuestionEdit.label.AMBestRating" />: <s:property value="#ambest.ratingAlpha" /> /
					<s:text name="AmBest.financialCode" /> <s:property value="#ambest.financialAlpha" />
				</s:if>
				
				<br />
			</s:if>
		</s:if>
		
		<s:if test="#q.questionType == 'File'">
			<nobr>
				<s:if test="#a.id > 0 && #a.answer.length() > 0">
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="#q.id"/>" target="_BLANK"><s:text name="Audit.link.ViewFile" /></a>
					<input id="show_button_<s:property value="#q.id"/>" type="button" value="<s:text name="Audit.EditFile" />" class="fileUpload" title="Opens in new window (please disable your popup blocker)" />
				</s:if>
				<s:else>
					<input id="show_button_<s:property value="#q.id"/>" type="button" value="<s:text name="Audit.UploadFile" />" class="fileUpload" title="Opens in new window (please disable your popup blocker)" />
				</s:else>
			</nobr>
		</s:if>
		
		<s:if test="#q.questionType == 'FileCertificate'">
			<s:include value="audit_question_cert_load.jsp" />
		</s:if>

		<s:if test="#q.questionType == 'ESignature'">
			<div class="esignature">
				<s:if test="!isStringEmpty(#a.answer)">
					<s:set name="esignature_edit" value="%{'display: none'}" />
				</s:if>
				<s:else>
					<s:set name="esignature_edit" value="" />
				</s:else>
				
				<div class="edit" style="${esignature_edit}">
					<div class="name">
						<label><s:text name="QuestionType.ESignature.fullName" />:</label>
						<s:textfield name="ESignatureName" />
					</div>
					
					<div class="title">
						<label><s:text name="QuestionType.ESignature.title" />:</label>
						<s:textfield name="ESignatureTitle" />
					</div>
					
					<s:submit type="button" value="%{getText('QuestionType.ESignature.sign')}" cssClass="question-save" />
				</div>
				
				<s:if test="!isStringEmpty(#a.answer)">
					<div class="view">
						<div class="name">
							<span><s:text name="QuestionType.ESignature.label.NameTitle" />:</span> ${a.answer}
						</div>

                        <s:if test="!isStringEmpty(#a.answer)">
						    <div class="date">
							    <span><s:text name="global.Date" />:</span> <s:date name="#a.updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
						    </div>
                        </s:if>
						
						<div class="ip">
							<span><s:text name="Login.IPAddress" />:</span> ${a.comment}
						</div>
						
						<a href="javascript:;" class="edit-esignature"><s:text name="global.Edit" /></a>

                        <s:if test="!isStringEmpty(#a.answer)">
                            <br/><a href="#" class="reset-answer"><s:text name="global.Reset" /></a>
                        </s:if>

                    </div>
				</s:if>

            </div>
		</s:if>
		
		<s:if test="#q.questionType == 'Tagit'">
			<input type="hidden"
				name="auditData.answer"
				data-option-group-id="${q.option.id}"
                value="${a.answer}"
                data-toggle="select2"
			/>
            <s:submit type="button" value="Submit" cssClass="question-save" />
		</s:if>

		<s:if test="#a.verified && !#q.hasRequirement">
			<s:if test="permissions.picsEmployee">
    			<div id="verify_details_<s:property value="#q.id"/>" style='display: <s:property value ="#attr.verifyDetailDisplay"/>;' class="verified">
    				Verified on <s:date name="#a.dateVerified" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /> by <s:property value="#a.auditor.name" />
    			</div>
            </s:if>
			<s:else>
				<span class="verified">
					<s:text name="Audit.message.AnswerVerifiedOn">
						<s:param><s:date name="#a.dateVerified" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /></s:param>
					</s:text>
				</span>	
			</s:else>
		</s:if>
		
		<s:if test="#a.hasRequirements">
			<br />
			<span class="requirement">
				<label><s:text name="Audit.message.RequirementStatus" />:</label>
				
				<s:if test="#a.requirementOpen">
					<span class="unverified-answer">
						<img src="images/notOkCheck.gif" />
						<s:text name="Audit.message.Open" />
					</span>
				</s:if>
				<s:elseif test="#a.wasChangedB">
					<span class="verified-answer">
						<img src="images/okCheck.gif" />
						<s:text name="Audit.message.ClosedOn">
							<s:param><s:date name="#a.dateVerified" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /></s:param>
						</s:text>
					</span>
				</s:elseif>
			</span>
		</s:if>
		
		<s:if test="#q.showComment || mode == 'Verify'">
			<br />
			<label><s:text name="AuditQuestionEdit.label.Comments" /></label>
			<br />
			<s:textarea name="auditData.comment" value="%{#a.comment}" />
		</s:if>
		
	</s:form>
	
	<s:if test="mode == 'Verify'">
		<s:if test="#a.verified == true">
			<s:set name="verifyText" value="'Unverify'" />
			<s:set name="verifyDetailDisplay" value="'inline'" />
		</s:if>
		<s:else>
			<s:set name="verifyText" value="'Verify'" />
			<s:set name="verifyDetailDisplay" value="'none'" />
		</s:else>
	
		<input class="verify" id="verifyButton_<s:property value="#q.id"/>" type="submit" value="<s:property value="#attr.verifyText"/>" />
	
		<div id="verify_details_<s:property value="#q.id"/>" style='display: <s:property value ="#attr.verifyDetailDisplay"/>;' class="verified">
			Verified on <s:date name="#a.dateVerified" format="%{@com.picsauditing.util.PicsDateFormat@IsoLongMonth}" /> by <s:property value="#a.auditor.name" />
		</div>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div class="error"><s:property value="#q.requirement" escape="false"/></div>
</s:if>

<s:include value="../actionMessages.jsp" />

<div class="dependent-questions hide"><s:iterator value="#q.dependentQuestions" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
