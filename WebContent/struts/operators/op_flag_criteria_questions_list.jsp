<%@ taglib prefix="s" uri="/struts-tags"%>

<table class="report" id="questionTable">
	<thead>
		<tr>
			<th> Type </th>
			<th> # </th>
			<th> Question </th>
			<th></th>
		</tr>
	</thead>
	<s:iterator value="questions">
		<tr id="addRow<s:property value="id"/>">
			<td>
				<s:property value="subCategory.category.auditType.auditName"/>
			</td>
			<td>
				<s:property value="expandedNumber"/>
			</td>
			<td>
				<s:property value="question" escape="false"/>
			</td>
			<td><a href="#" class="add" onclick="showCriteria(<s:property value="id"/>,'<s:property value="subCategory.category.auditType.auditName"/>'); return false;">Add</a></td>
		</tr>
	</s:iterator>
</table>