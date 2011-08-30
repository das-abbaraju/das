<%@ page language="java" errorPage="/exception_handler.jsp" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="answer">
	<s:form cssClass="qform" id="qform%{#q.id}" onsubmit="return false;">
		<s:hidden name="categoryID" value="%{#q.category.id}" cssClass="get_request"/>
		
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
				<s:radio theme="audits" list="#q.option.values" listValue="name" listKey="identifier" name="auditData.answer" value="%{#a.answer}"></s:radio>
				
				<input class="resetAnswer" id="clearButton_<s:property value="#q.id"/>" type="submit" value="Clear" />
				
				<s:if test="#q.auditType.policy && #q.option.uniqueCode.equals('YesNo')">
					<s:set name="op" value="%{getOperatorByName(#q.category.name)}" />
					
					<s:if test="#op != null && #op.id > 0">
						<div class="clearfix question shaded">
							If it does NOT comply, please explain below.
							<s:if test="#op.insuranceForms.size > 0">
								<ul style="list-style:none">
									<s:iterator value="#op.insuranceForms">
										<li><a href="forms/<s:property value="file"/>" target="_BLANK" title="Opens in new Window"><s:property value="formName"/></a></li>
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
				<s:select list="#q.option.values" headerValue="- Select -" headerKey="" listValue="name" listKey="identifier" name="auditData.answer" value="%{#a.answer}" />
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
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="text"/>
			<span style="font-style: italic; font-size: 12px;">example: 12/31/1999</span>
		</s:if>
		
		<s:if test="#q.questionType == 'License'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="text"/>
			<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
		
		<s:if test="#q.questionType == 'Money' || #q.questionType == 'Number' || #q.questionType == 'Decimal Number'">
			<s:textfield name="auditData.answer" value="%{#a.answer}" cssClass="number" cssClass="text"/>
		</s:if>
		
		<s:if test="#q.questionType == 'AMBest'">
			<s:hidden name="auditData.comment" value="%{#a.comment}"/>
			<s:textfield id="ambest" name="auditData.answer" value="%{#a.answer}" cssClass="text"/>

			<s:if test="#a.commentLength">
				<s:set name="ambest" value="@com.picsauditing.dao.AmBestDAO@getAmBest(#a.comment)" />
				<br />
				
				NAIC#: <s:property value="#a.comment" />
				<s:if test="#ambest.amBestId > 0">
					AM Best Rating: <s:property value="#ambest.ratingAlpha" /> /
					Class: <s:property value="#ambest.financialAlpha" />
				</s:if>
				
				<br />
			</s:if>
		</s:if>
		
		<s:if test="#q.questionType == 'File'">
			<nobr>
				<s:if test="#a.id > 0 && #a.answer.length() > 0">
					<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="#q.id"/>" target="_BLANK">View File</a>
				</s:if>
				<s:else>
					File Not Uploaded
				</s:else>
				<input id="show_button_<s:property value="#q.id"/>" type="button" value="<s:if test="#a.id > 0 && #a.answer.length() > 0">Edit</s:if><s:else>Add</s:else> File" class="fileUpload" title="Opens in new window (please disable your popup blocker)" />
			</nobr>
		</s:if>
		
		<s:if test="#q.questionType == 'FileCertificate'">
			<s:include value="audit_question_cert_load.jsp" />
		</s:if>
		
		<s:if test="#q.questionType == 'Calculation'">
			<s:property value="#a.answer"/>
		</s:if>
		
		<s:if test="#q.questionType == 'ESignature'">
			<s:if test="#a == null || #this.hasChanged(#q.id)"> 
				<label>Please enter your full name</label>
				<br />
				
				<s:hidden name="auditData.comment" value="%{#this.IP}"/>
				<s:textfield name="auditData.answer" value=""/>
				<s:submit type="button" value="Sign" cssClass="question-save" />
			</s:if>
			<s:else>
				${a.answer} ${a.updateDate}
			</s:else>
		</s:if>

		<s:if test="#a.verified && !#q.hasRequirement">
			<div class="verified">
				Answer verified on <s:date name="#a.dateVerified" format="MMM d, yyyy" />
			</div>
		</s:if>
		
		<s:if test="#a.hasRequirements">
			<br />
			<span class="requirement">
				<label>Requirement:</label>
				<s:if test="#a.requirementOpen">
					<div class="unverified">Open</div>
				</s:if>
				<s:elseif test="#a.wasChangedB">
					<div class="verified">Closed on <s:date name="#a.dateVerified" format="MMM d, yyyy" /></div>
				</s:elseif>
			</span>
		</s:if>
		
		<s:if test="#q.showComment || mode == 'Verify'">
			<br />
			<label>Comments:</label>
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
			Verified on <s:date name="#a.dateVerified" format="MMM d, yyyy" /> by <s:property value="#a.auditor.name" />
		</div>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div class="error"><s:property value="#q.requirement" escape="false"/></div>
</s:if>

<s:include value="../actionMessages.jsp" />

<div class="dependentFunction hide"><s:iterator value="#q.functionWatchers" status="s"><s:property value="function.question.id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
<div class="dependentRequired hide"><s:iterator value="#q.dependentRequired" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
<div class="dependentVisible hide"><s:iterator value="#q.getDependentVisible(#a.answer)" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
<div class="dependentVisibleHide hide"><s:iterator value="#q.getDependentVisibleHide(#a.answer)" status="s"><s:property value="id"/><s:if test="!#s.last">,</s:if></s:iterator></div>
