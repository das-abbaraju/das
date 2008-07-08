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
		<s:if test="questionType == 'Date'">
			<s:property value="answer.answer" />
		</s:if>
		<s:elseif test="questionType == 'File'">
			<s:if test="answer.answer.length() > 0">
				<a href="#" onClick="window.open('servlet/showpdf?id=<s:property value="contractor.id"/>&file=pqf<s:property value="answer.answer"/><s:property value="questionID"/>','','scrollbars=yes,resizable=yes,width=700,height=450')">Uploaded</a>
			</s:if>
			<s:else>File Not Uploaded</s:else>
		</s:elseif>
		<s:elseif test="questionType == 'License'">
			<s:property value="answer.answer" />
			<s:property value="@com.picsauditing.PICS.pqf.Constants@displayStateLink(question, answer.answer)" escape="false" />
		</s:elseif>
		
		<s:else>
			<s:property value="answer.answer" />
			<s:if test="questionType == 'Manual'">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Comment: <s:property value="answer.comment"/>
			</s:if>
		</s:else>
		
		<s:if test="answer.verified">
			<br/><span class="verified">Verified on <s:date name="answer.dateVerified" format="MMM d, yyyy" /></span>
		</s:if>
		
		<s:if test="answer.unverified">
			<br/><span class="unverified">Inaccurate Data</span>
		</s:if>
	</span></td>
</tr>
<s:if test="answer.hasRequirements && conAudit.auditType.hasRequirements">
	<tr class="group<s:if test="#shaded">Shaded</s:if>">
		<td class="center">Req</td>
		<td>Status:
		<s:if test="answer.requirementOpen">
			<span class="unverified">Open</span>
		</s:if>
		<s:else>
			<span class="verified">Closed on <s:date name="answer.dateVerified" format="MMM d, yyyy" /></span>
		</s:else>
		
		<br>
		<s:if test="requirement.length() > 0">
			<div id="alert"><s:property value="requirement"/></div>
		</s:if>
		</td>
		<td></td>
	</tr>
</s:if>
