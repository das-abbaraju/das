<%@ taglib prefix="s" uri="/struts-tags"%>
<span class="question">
	<a name="q<s:property value="#q.id"/>"></a>
	<s:property value="#q.subCategory.category.number"/>.<s:property value="#q.subCategory.number"/>.<s:property value="#q.number"/>&nbsp;&nbsp;
	
	<s:property value="#q.question" escape="false"/>
	<br />
	<s:if test="#q.linkUrl1.length() > 0"><a href="http://<s:property value="#q.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
	<s:if test="#q.linkUrl2.length() > 0"><a href="http://<s:property value="#q.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
	<s:if test="#q.linkUrl3.length() > 0"><a href="http://<s:property value="#q.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
	<s:if test="#q.linkUrl4.length() > 0"><a href="http://<s:property value="#q.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
	<s:if test="#q.linkUrl5.length() > 0"><a href="http://<s:property value="#q.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
	<s:if test="#q.linkUrl6.length() > 0"><a href="http://<s:property value="#q.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
</span>

<span class="answerDisplay">
	<s:if test="#q.questionType == 'File'">
		<s:if test="#a.id > 0 && #a.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&answer.id=<s:property value="#a.id"/>" 
				target="_BLANK">View File</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
	</s:if>
	<s:else>
		<s:property value="#a.answer" />
		<s:if test="#q.questionType == 'License'">
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(#q.question, #a.answer)" escape="false" />
		</s:if>
		<s:if test="#q.showComment && #a.comment.length() > 0">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comment: <s:property value="#a.comment"/>
		</s:if>
	</s:else>

	<span id="showText_<s:property value="#a.id"/>" style="display: none" class="verified">
		Answer changed on <s:date name="dateVerified" format="MMM d, yyyy" />
	</span>
</span>

<s:if test="#a.hasRequirements && conAudit.auditType.hasRequirements">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
		<td class="center">Requirement</td>
		<td>Status:
		<s:if test="requirementOpen">
			<span class="unverified">Open</span>
		</s:if>
		<s:else>
			<span class="verified">Closed on <s:date name="dateVerified" format="MMM d, yyyy" /></span>
		</s:else>
		
		<br>
		<s:if test="requirement.length() > 0">
			<s:if test="requirementOpen">
				<div id="alert"><s:property value="requirement"/></div>
			</s:if>
			<s:else>
				<span class="answer"><s:property value="requirement"/></span>
			</s:else>
		</s:if>
		</td>
	</tr>
</s:if>
<br clear="all"/>
