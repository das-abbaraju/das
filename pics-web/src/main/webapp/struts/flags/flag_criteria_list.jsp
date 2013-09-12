<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="criteriaList.size() > 0">
	<s:if test="auditTypeID > 0">
		<table class="report">
			<thead>
				<tr>
					<th>View</th>
					<th>Category</th>
					<th>Label</th>
					<th>Description</th>
					<th>Required Status</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="criteriaList">
					<tr>
						<td class="center"><a href="ManageFlagCriteria!edit.action?criteria=<s:property value="id"/>" class="preview"></a></td>
						<td><s:property value="category" /></td>
						<td><s:property value="label" /></td>
						<td><s:property value="description" /></td>
						<td class="center"><s:property value="requiredStatus" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
	<s:elseif test="questionID > 0">
			<table class="report">
			<thead>
				<tr>
					<th>View</th>
					<th>Category</th>
					<th>Label</th>
					<th>DataType</th>
					<th>Comparison</th>
					<th>Default Hurdle</th>
					<th>Allow Custom Hurdle</th>
					<th>Required Status</th>
					<th>Flaggable When Missing</th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="criteriaList">
					<tr>
						<td class="center"><a href="ManageFlagCriteria!edit.action?criteria=<s:property value="id"/>" class="preview"></a></td>
						<td><s:property value="category" /></td>
						<td><s:property value="label" /></td>
						<td><s:property value="dataType" /></td>
						<td><s:property value="comparison" /></td>
						<td><s:property value="defaultValue" /></td>
						<td><s:property value="allowCustomValue" /></td>
						<td><s:property value="requiredStatus" /></td>
						<td><s:property value="flaggableWhenMissing" /></td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:elseif>
</s:if>
<s:else>
	<div class="info">
		No Related Flagging Criteria found.
	</div>
</s:else>