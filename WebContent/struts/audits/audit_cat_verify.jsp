<%@ taglib prefix="s" uri="/struts-tags"%>
<tr id="status_<s:property value="id"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number" />.<s:property
		value="subCategory.number" />.<s:property value="number" />&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question" />
	<s:hidden name="%{'answer_' + id}" value="%{answer.answer}"></s:hidden>
	<span class="answer">
	<s:if test="questionType == 'File'">
		<s:if test="answer.answer.length() > 0">
			<a href="DownloadAuditData.action?auditID=<s:property value="auditID"/>&question.id=<s:property value="id"/>" target="_BLANK">Uploaded</a>
		</s:if>
		<s:else>File Not Uploaded</s:else>
	</s:if>
	<s:else>
		<s:property value="answer.answer" />
	</s:else>
	</span>
	<s:if test="answer.verified">
		<span class="verified">Verified on 
			<s:date name="answer.dateVerified" format="MMM d, yyyy" /> by 
			<s:property value="answer.auditor.name"/></span>
	</s:if>
	<br clear="all"/><br/>
	Verified Answer: 
	<s:if test="questionType == 'Radio'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="options"  listKey="optionName" listValue="optionName" value="answer.answer" onclick="saveVerifiedAnswer(%{id}, this);"/>
	</s:if>
	<s:elseif test="questionType == 'Check Box'">
		<s:checkbox id="verifiedBox_%{id}" name="answer.answer" fieldValue="X" value="answer.answer.length() == 1" onclick="saveVerifiedAnswer(%{id}, this);" />
	</s:elseif>
	<s:elseif test="questionType == 'Yes/No'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="#{'Yes':'Yes','No':'No'}" value="answer.answer" onclick="saveVerifiedAnswer(%{id}, this);"></s:radio>
	</s:elseif>
	<s:elseif test="questionType == 'Yes/No/NA'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer" onclick="saveVerifiedAnswer(%{id}, this);"></s:radio>
	</s:elseif>
	<s:else>
		<s:textfield id="verifiedBox_%{id}" name="verifiedAnswer_%{id}" value="%{answer.answer}" size="40" onchange="saveVerifiedAnswer(%{id},this);"/>
	</s:else>
	&nbsp;&nbsp;<button onclick="changeAnswer(<s:property value="id"/>, '<s:property value="questionType"/>');">Copy Answer</button>
	<br /><br/>
	Comment: <s:textfield id="comments_%{id}" name="answer.comment" size="50" onblur="javascript:saveComment(%{id}, this);"/>
	</td>
</tr>
