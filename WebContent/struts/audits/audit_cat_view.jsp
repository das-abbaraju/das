<%@ taglib prefix="s" uri="/struts-tags"%>
<span class="question">
	<a name="q<s:property value="#q.id"/>"></a>
	<s:property value="#q.subCategory.category.number"/>.<s:property value="#q.subCategory.number"/>.<s:property value="#q.number"/>&nbsp;&nbsp;
	
	<s:property value="#q.question" escape="false"/>
	<br />
	<s:if test="#q.linkUrl1 != null && #q.linkUrl1.length() > 0"><a href="http://<s:property value="#q.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText1"/></a></s:if>
	<s:if test="#q.linkUrl2 != null && #q.linkUrl2.length() > 0"><a href="http://<s:property value="#q.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText2"/></a></s:if>
	<s:if test="#q.linkUrl3 != null && #q.linkUrl3.length() > 0"><a href="http://<s:property value="#q.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText3"/></a></s:if>
	<s:if test="#q.linkUrl4 != null && #q.linkUrl4.length() > 0"><a href="http://<s:property value="#q.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText4"/></a></s:if>
	<s:if test="#q.linkUrl5 != null && #q.linkUrl5.length() > 0"><a href="http://<s:property value="#q.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText5"/></a></s:if>
	<s:if test="#q.linkUrl6 != null && #q.linkUrl6.length() > 0"><a href="http://<s:property value="#q.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="#q.linkText6"/></a></s:if>
</span>

<div class="answer">
	<s:if test="#q.questionType.startsWith('File')">
		<s:if test="#a.id > 0 && #a.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="#a.id"/>" 
				target="_BLANK">View File</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
	</s:if>
	<s:elseif test="#q.questionType == 'Check Box'">
		<s:if test='#a.answer.equals("X")'>
			<span class="checked"></span>
		</s:if>
	</s:elseif>
	<s:else>
		<s:property value="#a.answer" />
		<s:if test="#q.questionType == 'License'">
			<s:property value="@com.picsauditing.util.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
	</s:else>
	
	<s:if test="#a.verified && !#q.hasRequirementB">
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
	<s:if test="#a.commentLength">
		<br/>
		<label>Comment:</label> <s:property value="#a.comment"/>
	</s:if>
</div>

<s:if test="#a.hasRequirements && #a.requirementOpen">
	<br clear="all"/>
	<div id="error"><s:property value="#q.requirement" escape="false"/></div>
</s:if>

<br clear="all"/>
