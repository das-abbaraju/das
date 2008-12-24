<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number"/>.<s:property value="question.subCategory.number"/>.<s:property value="question.number"/>&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question.question" escape="false"/>
		<s:if test="question.linkUrl1 > ''"><a href="http://<s:property value="question.linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText1"/></a></s:if>
		<s:if test="question.linkUrl2 > ''"><a href="http://<s:property value="question.linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText2"/></a></s:if>
		<s:if test="question.linkUrl3 > ''"><a href="http://<s:property value="question.linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText3"/></a></s:if>
		<s:if test="question.linkUrl4 > ''"><a href="http://<s:property value="question.linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText4"/></a></s:if>
		<s:if test="question.linkUrl5 > ''"><a href="http://<s:property value="question.linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText5"/></a></s:if>
		<s:if test="question.linkUrl6 > ''"><a href="http://<s:property value="question.linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="question.linkText6"/></a></s:if>
	
	<span class="answer">
		<s:if test="questionType == 'File'">
			<s:if test="id > 0 && answer.length() > 0">
				<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&question.id=<s:property value="id"/>" target="_BLANK">Uploaded</a>
			</s:if>
			<s:else>File Not Uploaded</s:else>
		</s:if>
		
		<s:elseif test="questionType == 'License'">
			<s:property value="answer" />
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer)" escape="false" />
		</s:elseif>
		
		<s:else>
			<s:property value="answer" />
			<s:if test="questionType == 'Manual' && comment.length() > 0">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comment: <s:property value="comment"/>
			</s:if>
		</s:else>
		
	</span>
	<br/>
	<span id="showText_<s:property value="id"/>" style="display: none" class="verified">
		Answer changed on <s:date name="dateVerified" format="MMM d, yyyy" />
	</span></td>
</tr>
<s:if test="hasRequirements && conAudit.auditType.hasRequirements">
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
