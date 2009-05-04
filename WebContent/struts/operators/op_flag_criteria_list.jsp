<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th class="center">#</th>
			<th>Question</th>
			<th title="Red Flag Criteria"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.bigIcon" escape="false"/></th>
			<th title="Yellow Flag Criteria"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Amber.bigIcon" escape="false"/></th>
		</tr>
	</thead>
	
	<s:iterator value="oshaList">
	</s:iterator>
	
	<s:iterator value="questionList">
		<tr onclick="showCriteria('<s:property value="question.id" />', '<s:property value="question.subCategory.category.auditType.auditName"/>', <s:property value="question.questionType=='Date'"/>);" 
		class="clickable" title="Click to open">
			<td><s:property value="question.subCategory.category.auditType.auditName" /></td>
			<td class="right">
				<nobr><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></nobr>
			</td>
			<td>
				<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
						<s:property value="question.question" />
				</s:if>
				<s:else><s:property value="question.question" escape="false" /></s:else>
			</td>
			<td><nobr><s:property value="red" escape="false" /></nobr></td>
			<td><nobr><s:property value="amber" escape="false" /></nobr></td>
		</tr>
	</s:iterator>
</table>
