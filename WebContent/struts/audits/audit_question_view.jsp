<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set name="questionStillRequired" value="false" />
<s:if test="(#a == null || #a.answer == null || #a.answer.length() < 1)">
	<s:if test="#q.isRequired == 'Yes'">
		<s:set name="questionStillRequired" value="true" />
	</s:if>
	<s:if test="#q.isRequired == 'Depends' && #q.dependsOnQuestion.id > 0">
		<s:set name="dependsAnswer" value="answerMap.get(#q.dependsOnQuestion.id)" />
		<s:if test="#q.dependsOnAnswer == 'NULL' && (#dependsAnswer == null || #dependsAnswer.answer == '')">
        	<% // Policies must have either Policy Expiration Date OR In Good Standing %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
        <s:if test="#q.dependsOnAnswer == 'NOTNULL' && #dependsAnswer != null">
        	<% // If dependsOnQuestion is a textfield, textbox or a select box etc where the dependsOnAnswer is not null %>
           	<s:set name="questionStillRequired" value="true" />
        </s:if>
		<s:if test="#dependsAnswer != null && #q.dependsOnAnswer == #dependsAnswer.answer">
			<s:set name="questionStillRequired" value="true" />
		</s:if>
	</s:if>
</s:if>
<% //TODO check stuff here %>
<s:if test="questionStillRequired">
	<span class="printrequired"><img src="images/yellow_star.gif"></span>
</s:if>
<span class="question<s:if test="questionStillRequired"> required</s:if>">
	<a name="q<s:property value="#q.id"/>"></a>
	<span class="questionNumber"><s:property value="#q.expandedNumber"/></span>
	
	<s:property value="#q.name" escape="false"/>
	<br />
	<s:if test="#q.helpPage.length() > 0"><a href="http://help.picsauditing.com/wiki/<s:property value="#q.helpPage"/>" class="help" target="_BLANK" title="opens in new window">Help Center</a></s:if>
	<s:if test="(#q.id == 3563 || #q.id == 3565 || #q.id == 3566) && #a.answer.length() > 0"><a href="http://www.osha.gov/pls/imis/establishment.inspection_detail?id=<s:property value="#a.answer"/>" target="_BLANK" title="opens in new window">OSHA Citations</a></s:if>
</span>

<div class="answer">
	<s:if test="#q.questionType == 'File'">
		<s:if test="#a.id > 0 && #a.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="#a.id"/>" 
				target="_BLANK">View File</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
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
			<br>
			NAIC#: <s:property value="#a.comment" />
			<s:if test="#ambest.amBestId > 0">
				AM Best Rating: <s:property value="#ambest.ratingAlpha" /> /
				Class: <s:property value="#ambest.financialAlpha" />
			</s:if>
			<br>
		</s:if>
	</s:elseif>
	<s:elseif test="#q.questionType == 'Text Area'">
		<s:property value="#a.getHtmlDisplay(#a.answer)" escape="false"/>
	</s:elseif>	
	<s:else>
		<s:property value="#a.answer" />
		<s:if test="#q.questionType == 'License'">
			<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
	</s:else>
	
	<s:if test="#a.verified && !#q.hasRequirement">
		<span class="verified">
			Answer verified on <s:date name="#a.dateVerified" format="MMM d, yyyy" />
		</span>
	</s:if>
	<s:if test="#a.hasRequirements">
		<br/><br/>
		<s:if test="#a.requirementOpen">
			<s:set name="extraClass" value="'boxed'"/>
		</s:if>
		<span class="requirement <s:property value="#extraClass" default=""/>">
			<label>Requirement Status:</label>
			<s:if test="#a.requirementOpen">
				<span class="unverified">Open</span>
			</s:if>
			<s:elseif test="#a.wasChangedB">
				<span class="verified">Closed on <s:date name="#a.dateVerified" format="MMM d, yyyy" /></span>
			</s:elseif>
		</span>
	</s:if>
	<s:if test="#a.commentLength && #q.questionType != 'AMBest'">
		<br/>
		<label>Comment:</label> <s:property value="#a.getHtmlDisplay(#a.comment)" escape="false"/>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div class="error"><s:property value="#q.requirement" escape="false"/></div>
</s:if>
