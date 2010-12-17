<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="rules.size() > 0">
	<table class="report">
		<thead>
			<tr>
				<s:if test="columnMap.get('include')">
					<th>Include</th>
				</s:if>
				<s:if test="columnMap.get('priority')">
					<th>Priority</th>
				</s:if>
				<s:if test="columnMap.get('auditType')">
					<th>Audit Type</th>
				</s:if>
				<s:if test="columnMap.get('auditCategory')">
					<th>Category</th>
				</s:if>
				<s:if test="columnMap.get('rootCategory')">
					<th>Root Category</th>
				</s:if>
				<s:if test="columnMap.get('contractorType')">
					<th>Contractor Type</th>
				</s:if>
				<s:if test="columnMap.get('operatorAccount')">
					<th>Operator</th>
				</s:if>
				<s:if test="columnMap.get('risk')">
					<th>Risk</th>
				</s:if>
				<s:if test="columnMap.get('tag')">
					<th>Tag</th>
				</s:if>
				<s:if test="columnMap.get('bidOnly')">
					<th>Bid-Only</th>
				</s:if>
				<s:if test="columnMap.get('question')">
					<th colspan="3">Question</th>
				</s:if>
				<s:if test="columnMap.get('dependentAuditType')">
					<th colspan="2">Dependent Audit</th>
				</s:if>
				<s:if test="columnMap.get('createdBy')">
					<th colspan="2">Created By</th>
				</s:if>
				<s:if test="columnMap.get('updatedBy')">
					<th colspan="2">Updated By</th>
				</s:if>
				<th>View</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="rules">
				<tr>
					<s:if test="columnMap.get('include')">
						<td><s:if test="include">Yes</s:if><s:else>No</s:else></td>
					</s:if>
					<s:if test="columnMap.get('priority')">
						<td><s:property value="priority"/></td>
					</s:if>
					<s:if test="columnMap.get('auditType')">
						<td><s:property value="auditTypeLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('auditCategory')">
						<td><s:property value="auditCategoryLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('rootCategory')">
						<td><s:property value="rootCategoryLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('contractorType')">
						<td><s:property value="contractorTypeLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('operatorAccount')">
						<td><s:property value="operatorAccountLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('risk')">
						<td><s:property value="riskLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('tag')">
						<td><s:property value="tagLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('bidOnly')">
						<td><s:property value="acceptsBidsLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('question')">
						<td><s:property value="questionLabel"/></td>
						<td><s:property value="questionComparatorLabel"/></td>
						<td><s:property value="questionAnswerLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('dependentAuditType')">
						<td><s:property value="dependentAuditTypeLabel"/></td>
						<td><s:property value="dependentAuditStatusLabel"/></td>
					</s:if>
					<s:if test="columnMap.get('createdBy')">
						<td><s:property value="createdBy.name"/></td>
						<td><s:date name="creationDate" format="mm/dd/yyy"/></td>
					</s:if>
					<s:if test="columnMap.get('updatedBy')">
						<td><s:property value="updatedBy.name"/></td>
						<td><s:date name="updateDate" format="mm/dd/yyy"/></td>
					</s:if>
					<td><a href="<s:property value="ruleType.replaceAll(' ','')"/>RuleEditor.action?id=<s:property value="id"/>" class="preview"></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="info">
		No rules found for this <s:property value="ruleType" />
	</div>
</s:else>