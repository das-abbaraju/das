<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question"/>
	
	<span class="answer">
		<s:if test="questionType == 'Date'">
			<s:property value="com.picsauditing.PICS.DateBean.toShowFormat(answer.answer)"/>
		</s:if>
		<s:elseif test="questionType == 'File'">
			<s:if test="answer.answer.length() > 0">
				<a href="#" onClick="window.open('servlet/showpdf?id=<s:property value="contractor.id"/>&file=pqf','','scrollbars=yes,resizable=yes,width=700,height=450')">Uploaded</a>
			</s:if>
			<s:else>File Not Uploaded</s:else>
		</s:elseif>
		<s:else>
		<s:property value="answer.answer"/>
		</s:else>
		<s:if test="answer.verified">
			<span class="verified">Verified on <s:date name="answer.dateVerified" format="MMM d, yyyy" /></span>
		</s:if>
		<s:if test="answer.unverified">
			<span class="unverified">Inaccurate Data</span>
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
		<s:property value="requirement"/>
		</td>
		<td></td>
	</tr>
</s:if>
