<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question" escape="false"/>
		<s:if test="linkUrl1 > ''"><a href="http://<s:property value="linkUrl1"/>" target="_BLANK" title="opens in new window"><s:property value="linkText1"/></a></s:if>
		<s:if test="linkUrl2 > ''"><a href="http://<s:property value="linkUrl2"/>" target="_BLANK" title="opens in new window"><s:property value="linkText2"/></a></s:if>
		<s:if test="linkUrl3 > ''"><a href="http://<s:property value="linkUrl3"/>" target="_BLANK" title="opens in new window"><s:property value="linkText3"/></a></s:if>
		<s:if test="linkUrl4 > ''"><a href="http://<s:property value="linkUrl4"/>" target="_BLANK" title="opens in new window"><s:property value="linkText4"/></a></s:if>
		<s:if test="linkUrl5 > ''"><a href="http://<s:property value="linkUrl5"/>" target="_BLANK" title="opens in new window"><s:property value="linkText5"/></a></s:if>
		<s:if test="linkUrl6 > ''"><a href="http://<s:property value="linkUrl6"/>" target="_BLANK" title="opens in new window"><s:property value="linkText6"/></a></s:if>
	
	<span class="answer">
		<s:if test="questionType == 'File'">
			<s:if test="answer.answer.length() > 0">
				<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&question.questionID=<s:property value="questionID"/>" target="_BLANK">Uploaded</a>
			</s:if>
			<s:else>File Not Uploaded</s:else>
		</s:if>
		
		<s:elseif test="questionType == 'License'">
			<s:property value="answer.answer" />
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer.answer)" escape="false" />
		</s:elseif>
		
		<s:else>
			<s:if test="answer.verified && answer.answer.length() > 0">
				 <s:property value="answer.answer" />&nbsp;&nbsp;<a href="javascript:showAnswer(<s:property value="questionID"/>);" style="color : rgb(168, 77, 16);font-weight : normal;">Changed by PICS</a> 
			</s:if>
			<s:else>
				<s:property value="answer.answer" />
			</s:else>
			<s:if test="questionType == 'Manual' && answer.answer.length() > 0">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comment: <s:property value="answer.comment"/>
			</s:if>
		</s:else>
		
	</span>
	<br/>
	<span id="showText_<s:property value="questionID"/>" style="display: none" class="verified">
		Previous Answer changed from "<s:property value="answer.answer"/>" on <s:date name="answer.dateVerified" format="MMM d, yyyy" />
	</span></td>
</tr>
<s:if test="answer.hasRequirements && conAudit.auditType.hasRequirements">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
		<td class="center">Requirement</td>
		<td>Status:
		<s:if test="answer.requirementOpen">
			<span class="unverified">Open</span>
		</s:if>
		<s:else>
			<span class="verified">Closed on <s:date name="answer.dateVerified" format="MMM d, yyyy" /></span>
		</s:else>
		
		<br>
		<s:if test="requirement.length() > 0">
			<s:if test="answer.requirementOpen">
				<div id="alert"><s:property value="requirement"/></div>
			</s:if>
			<s:else>
				<span class="answer"><s:property value="requirement"/></span>
			</s:else>
		</s:if>
		</td>
	</tr>
</s:if>
