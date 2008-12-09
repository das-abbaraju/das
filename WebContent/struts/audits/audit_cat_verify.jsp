<%@ taglib prefix="s" uri="/struts-tags"%>
<tr id="status_<s:property value="id"/>" class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number" />.<s:property
		value="subCategory.number" />.<s:property value="number" />&nbsp;&nbsp;
		<br/><s:div id="thinking_%{id}"></s:div>
		</td>
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
		<s:set name="verifyText" value="'Unverify'"/>
		<s:set name="verifyDetailDisplay" value="'inline'"/>
	</s:if>
	<s:else>
		<s:set name="verifyText" value="'Verify'"/>
		<s:set name="verifyDetailDisplay" value="'none'"/>
	</s:else>

	&nbsp;&nbsp;<input type="submit" id="verifyButton_<s:property value="id"/>" onclick="return changeAnswer(<s:property value="id"/>, '<s:property value="questionType"/>');" value="<s:property value="#attr.verifyText"/>"/>

		<span id="verify_details_<s:property value="id"/>" style="display : <s:property value="#attr.verifyDetailDisplay"/>;" class="verified">Verified on 
			<s:date name="answer.dateVerified" format="MMM d, yyyy" /> by 
			<s:property value="answer.auditor.name"/></span>

	<br /><br/>
	Comment: <s:textfield id="comments_%{id}" name="answer.comment" size="50" onblur="javascript:saveComment(%{id}, this);"/>
	</td>
</tr>
