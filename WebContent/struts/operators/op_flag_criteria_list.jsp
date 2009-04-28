<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Question</th>
			<th>Red if...</th>
			<th title="and not Red">Amber if...</th>
		</tr>
	</thead>
	<s:iterator value="questionList">
		<tr>
			<td>
				<s:property value="question.subCategory.category.auditType.auditName" /><br />
				<nobr><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></nobr>
			</td>
			<td>
				<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
						<a href="#" onclick="showCriteria('<s:property value="operator.id" />', '<s:property value="question.id" />');"><s:property value="question.question" /></a>
				</s:if>
				<s:else><s:property value="question.question" /></s:else>
			</td>
			<td><s:property value="red" escape="false" /></td>
			<td><s:property value="amber" escape="false" /></td>
		</tr>
	</s:iterator>
</table>

<s:property value="operator == operator.inheritFlagCriteria"/>

