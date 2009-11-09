<%@ taglib prefix="s" uri="/struts-tags"%>
<s:if test="questions.size > 0">
	<table class="report">
		<thead>
			<tr>
				<th>Type</th>
				<th class="center">#</th>
				<th>Question</th>
				<th></th>
			</tr>
		</thead>

		<s:iterator value="questions">
			<tr>
				<td><s:property value="subCategory.category.auditType.auditName" /></td>
				<td class="right"><nobr><s:property value="subCategory.category.number" />.<s:property
					value="subCategory.number" />.<s:property value="number" /></nobr></td>
				<td><s:property value="question" escape="false" /></td>
				<td><a href="#" onclick="showCriteria(<s:property value="id" />)">Add</a></td>
			</tr>
		</s:iterator>
	</table>
	<s:if test="questions.size >= 100">
		<div class="alert">Only displaying first 100 results</div>
	</s:if>
</s:if>
<s:else>
	<div class="alert">No questions matching this text</div>
</s:else>
