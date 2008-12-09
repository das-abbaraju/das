<%@ taglib prefix="s" uri="/struts-tags"%>
<tr id="status_<s:property value="id"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number" />.<s:property
		value="subCategory.number" />.<s:property value="number" />&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question" />
	<s:hidden name="%{'answer_' + id}" value="%{answer.answer}"></s:hidden>
	<br clear="all"/><br/>
	Answer: 
	<s:if test="questionType == 'Radio'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="options"  listKey="optionName" listValue="optionName" value="answer.answer"/>
	</s:if>
	<s:elseif test="questionType == 'Check Box'">
		<s:checkbox id="verifiedBox_%{id}" name="answer.answer" fieldValue="X" value="answer.answer.length() == 1"/>
	</s:elseif>
	<s:elseif test="questionType == 'Yes/No'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="#{'Yes':'Yes','No':'No'}" value="answer.answer"></s:radio>
	</s:elseif>
	<s:elseif test="questionType == 'Yes/No/NA'">
		<s:radio name="verifiedAnswer_%{id}" theme="pics" list="#{'Yes':'Yes','No':'No','NA':'NA'}" value="answer.answer"></s:radio>
	</s:elseif>
	<s:else>
		<s:textfield id="verifiedBox_%{id}" name="verifiedAnswer_%{id}" value="%{answer.answer}" size="40"/>
	</s:else>

	<s:if test="answer.verified == true">
		&nbsp;&nbsp;<input type="submit" id="verifyButton_<s:property value="id"/>" onclick="return changeAnswer(<s:property value="id"/>, '<s:property value="questionType"/>');" value="Unverify"/>
		<span id="verify_details_<s:property value="id"/>" class="verified">Verified on 
			<s:date name="answer.dateVerified" format="MMM d, yyyy" /> by 
			<s:property value="answer.auditor.name"/></span>
	</s:if>
	<s:else>
		&nbsp;&nbsp;<input type="submit" id="verifyButton_<s:property value="id"/>" onclick="return changeAnswer(<s:property value="id"/>, '<s:property value="questionType"/>');" value="Verify"/>
		<span id="verify_details_<s:property value="id"/>" class="verified" style="display: none;"></span>
	</s:else>
	<br /><br/>
	Comment: <s:textfield id="comments_%{id}" name="answer.comment" size="50" onblur="javascript:saveComment(%{id}, this);"/>
	</td>
</tr>
