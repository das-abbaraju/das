<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number" />.<s:property
		value="subCategory.number" />.<s:property value="number" />&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question" />
	<span class="answer">
	<s:if test="questionType == 'File'">
		<s:if test="answer.answer.length() > 0">
			<a href="#"
				onClick="window.open('servlet/showpdf?id=<s:property value="contractor.id"/>&file=pqf','','scrollbars=yes,resizable=yes,width=700,height=450')">Uploaded</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
	</s:if>
	<s:else>
		<s:property value="answer.answer" />
	</s:else>
	</span>
	
	<s:if test="answer.verified">
		<br />
		<span class="verified">Verified on 
			<s:date name="answer.dateVerified" format="MMM d, yyyy" /> by 
			<s:property value="auditor.name"/></span>
	</s:if>
	<div class="verifyBox">Is Original Answer Correct?
	<s:radio list="#{'Yes':'Yes','No':'No'}" name="isCorrect_%{questionID}" value="isCorrect"></s:radio><br />
	Verified Answer: <s:textfield name="verifiedAnswer_%{questionID}" value="%{verifiedAnswer}" size="50"></s:textfield><br />
	Comment: <s:textfield name="comment_%{questionID}" value="%[comment}" size="50"></s:textfield>
	</div>
	</td>
</tr>
