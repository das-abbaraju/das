<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Question</th>
			<th>Amber Flag Criteria</th>
			<th>Red Flag Criteria</th>
		</tr>
	</thead>
	<s:iterator value="questionList">
		<tr>
			<td><s:property value="question.subCategory.number" />.<s:property value="question.number" /></td>
			<td><s:property value="question.question" /></td>
		</tr>
	</s:iterator>
</table>
