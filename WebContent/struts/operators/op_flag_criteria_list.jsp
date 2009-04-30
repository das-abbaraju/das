<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Type</th>
			<th class="center">#</th>
			<th>Question</th>
			<th><s:property value="@com.picsauditing.jpa.entities.FlagColor@Red.smallIcon" escape="false"/> Red if...</th>
			<th title="and not Red"><s:property value="@com.picsauditing.jpa.entities.FlagColor@Amber.smallIcon" escape="false"/> Amber if...</th>
		</tr>
	</thead>
	
	<s:iterator value="questionList">
		<tr>
			<td><s:property value="question.subCategory.category.auditType.auditName" /></td>
			<td class="right">
				<nobr><s:property value="question.subCategory.category.number" />.<s:property value="question.subCategory.number" />.<s:property value="question.number" /></nobr>
			</td>
			<td>
				<s:if test="(operator == operator.inheritFlagCriteria && !question.subCategory.category.auditType.classType.policy) 
						|| (operator == operator.inheritInsuranceCriteria && question.subCategory.category.auditType.classType.policy)">
						<a href="#criteriaEdit" onclick="showCriteria('<s:property value="question.id" />'); return false;"><s:property value="question.question" /></a>
				</s:if>
				<s:else><s:property value="question.question" escape="false" /></s:else>
			</td>
			<td><nobr><s:property value="red" escape="false" /></nobr></td>
			<td><nobr><s:property value="amber" escape="false" /></nobr></td>
		</tr>
	</s:iterator>
</table>
