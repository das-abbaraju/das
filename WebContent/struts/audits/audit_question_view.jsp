<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="answer">
	<s:if test="#q.questionType == 'File'">
		<s:if test="#a.id > 0 && #a.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&auditData.question.id=<s:property value="#q.id"/>" target="_BLANK"><s:text name="Audit.link.ViewFile" /></a>
		</s:if>
		<s:else>
			<s:text name="Audit.message.FileNotUploaded" />
		</s:else>
	</s:if>
	<s:elseif test="#q.questionType == 'FileCertificate'">
		<s:include value="audit_question_cert_load.jsp" />
	</s:elseif>
	<s:elseif test="#q.questionType == 'Check Box'">
		<s:if test='#a.answer.equals("X")'>
			<span class="checked"></span>
			<span class="printchecked"><img src="images/checkBoxTrue.gif"></span>
		</s:if>
	</s:elseif>
	<s:elseif test="#q.questionType == 'Country'">
		<s:iterator value="countryList">
			<s:if test="isoCode == #a.answer">
				<s:property value="name"/>
			</s:if>
		</s:iterator>
	</s:elseif>
	<s:elseif test="#q.questionType == 'State'">
		<s:iterator value="stateList">
			<s:if test="isoCode == #a.answer">
				<s:property value="name"/>
			</s:if>
		</s:iterator>
	</s:elseif>
	<s:elseif test="#q.questionType == 'AMBest'">
		<s:property value="#a.answer" />
		
		<s:if test="#a.commentLength">
			<s:set name="ambest" value="@com.picsauditing.dao.AmBestDAO@getAmBest(#a.comment)" />
			
			<br />
			<s:text name="AmBest.NAIC" /><s:property value="#a.comment" />
			
			<s:if test="#ambest.amBestId > 0">
				<s:text name="AuditQuestionEdit.label.AMBestRating" /><s:property value="#ambest.ratingAlpha" /> /
				<s:text name="AmBest.financialCode" /><s:property value="#ambest.financialAlpha" />
			</s:if>
			
			<br />
		</s:if>
	</s:elseif>
	<s:elseif test="#q.questionType == 'Text Area'">
		<s:property value="#a.getHtmlDisplay(#a.answer)" escape="false"/>
	</s:elseif>	
	<s:else>
		<s:if test="#q.questionType == 'MultipleChoice' && #q.option != null && !isStringsEmpty(#a.answer)">
			<s:text name="%{#q.option.i18nKey + '.' + #a.answer}" />
		</s:if>
		<s:elseif test="#q.questionType == 'Calculation'">
			<s:if test="hasKey(#a.answer)">
				<s:text name="%{#a.answer}" />			
			</s:if>
			<s:else>
				<s:property value="#a.answer" />
			</s:else>
		</s:elseif>
		<s:else>
			<s:property value="#a.answer" />
		</s:else>
		
		<s:if test="#q.questionType == 'License'">
			<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
	</s:else>
	
	<s:if test="#a.verified && !#q.hasRequirement">
		<span class="verified">
			<s:text name="Audit.message.AnswerVerifiedOn">
				<s:param><s:date name="#a.dateVerified" format="MMM d, yyyy" /></s:param>
			</s:text>
		</span>
	</s:if>
	
	<s:if test="#a.hasRequirements">
		<br/><br/>
		
		<s:if test="#a.requirementOpen">
			<s:set name="extraClass" value="'boxed'"/>
		</s:if>
		
		<span class="requirement <s:property value="#extraClass" default=""/>">
			<label><s:text name="Audit.message.RequirementStatus" />:</label>
			
			<s:if test="#a.requirementOpen">
				<div class="unverified-answer">
					<img src="images/notOkCheck.gif" />
					<s:text name="Audit.message.Open" />
				</div>
			</s:if>
			<s:elseif test="#a.wasChangedB">
				<div class="verified-answer">
					<img src="images/okCheck.gif" />
					<s:text name="Audit.message.ClosedOn">
						<s:param><s:date name="#a.dateVerified" format="MMM d, yyyy" /></s:param>
					</s:text>
				</div>
			</s:elseif>
		</span>
	</s:if>
	
	<s:if test="#a.commentLength && #q.questionType != 'AMBest'">
		<br />
		
		<div class="info">
			<label><s:text name="Audit.message.Comment" />:</label>
			<s:property value="#a.getHtmlDisplay(#a.comment)" escape="false"/>
		</div>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	
	<div class="error">
		<s:property value="#q.requirement" escape="false"/>
	</div>
</s:if>
