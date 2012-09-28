<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<script>
function deleteRule(element,ruleID) {
	var deleteMe = confirm('You are sure you want to archive (delete) this rule?');
	
	if (!deleteMe) {
		return;
	}
	
	$.ajax({
		url: '<s:property value="urlPrefix"/>RuleEditor.action', 
		data: {button: 'Delete', id: ruleID}, 
		success: function() {
			$(element).remove();
		}
	});
}
</script>

<s:if test="rules.size() > 0">
	<table class="report">
		<thead>
			<tr>
				<th>View</th>
				
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
					<th title="Category">Root</th>
				</s:if>
				
				<s:if test="columnMap.get('operatorAccount')">
					<th>Operator</th>
				</s:if>
				
				<s:if test="columnMap.get('contractorType')">
					<th title="Onsite/Offsite/Supplier">Type</th>
				</s:if>
				
				<s:if test="columnMap.get('safetyRisk')">
					<th title="Safety Critical">Safety</th>
				</s:if>
				
				<s:if test="columnMap.get('productRisk')">
					<th title="Product Critical">Product</th>
				</s:if>
				
				<s:if test="columnMap.get('tag')">
					<th>Tag</th>
				</s:if>
				
				<s:if test="columnMap.get('trade')">
					<th><s:text name="Trade" /></th>
				</s:if>
				
				<s:if test="columnMap.get('accountLevel')">
					<th title="Bid Only, List Only, or Full Account">List</th>
				</s:if>
				
				<s:if test="columnMap.get('soleProprietor')">
					<th title="Sole Proprietor or Not a Sole Proprietor">Sole</th>
				</s:if>
				
				<s:if test="columnMap.get('question')">
					<th colspan="3">Question</th>
				</s:if>
				
				<s:if test="columnMap.get('manuallyAdded')">
					<th>Man. Added</th>
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
				
				<s:if test="columnMap.get('delete')">
					<th colspan="2">Delete</th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="rules" var="r">
				<tr id="<s:property value="urlPrefix"/>Rule<s:property value="id"/>" class="<s:if test="include">on</s:if><s:else>off</s:else>">
					<td class="center">
						<a href="<s:property value="urlPrefix"/>RuleEditor.action?id=<s:property value="id"/>" class="preview"></a>
					</td>
					
					<s:if test="columnMap.get('include')">
						<td class="center">
							<s:if test="include">
								Yes
							</s:if>
							<s:else>
								No
							</s:else>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('priority')">
						<td>
							<s:property value="level"/>.<s:property value="priority"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('auditType')">
						<td>
							<s:if test="auditType != null && permissions.hasPermission('ManageAudits')">
								<a href="ManageAuditType.action?id=<s:property value="auditType.id"/>"><s:property value="auditTypeLabel"/></a>
							</s:if>
							<s:else>
								<s:property value="auditTypeLabel"/>
							</s:else>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('auditCategory')">
						<td>
							<s:if test="auditCategory != null && permissions.hasPermission('ManageAudits')">
								<a href="ManageCategory.action?id=<s:property value="auditCategory.id"/>"><s:property value="auditCategoryLabel"/></a>
							</s:if>
							<s:else>
								<s:property value="auditCategoryLabel"/>
							</s:else>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('rootCategory')">
						<td>
							<s:property value="rootCategoryLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('operatorAccount')">
						<td class="limitWidth">
							<s:if test="operatorAccount != null">
								<a href="OperatorConfiguration.action?id=<s:property value="operatorAccount.id"/>" class="account<s:property value="operatorAccount.status"/>"><s:property value="operatorAccountLabel"/></a>
							</s:if>
							<s:else>
								<s:property value="operatorAccountLabel"/>
							</s:else>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('contractorType')">
						<td>
							<s:property value="contractorTypeLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('safetyRisk')">
						<td>
							<s:property value="safetyRiskLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('productRisk')">
						<td>
							<s:property value="productRiskLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('tag')">
						<td>
							<s:property value="tagLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('trade')">
						<td class="limitWidth">
							<s:property value="tradeLabel" />
						</td>
					</s:if>
					
					<s:if test="columnMap.get('bidOnly')">
						<td>
							<s:property value="acceptsBidsLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('accountLevel')">
						<td>
							<s:property value="accountLevelLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('soleProprietor')">
						<td>
							<s:property value="soleProprietorLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('question')">
						<td class="limitWidth">
							<s:if test="question != null && permissions.hasPermission('ManageAudits')">
								<a href="ManageQuestion.action?id=<s:property value="question.id"/>"><s:property value="questionLabel"/></a>
							</s:if>
							<s:else>
								<s:property value="questionLabel"/>
							</s:else>
						</td>
						<td>
							<s:property value="questionComparatorLabel"/>
						</td>
						<td>
							<s:property value="questionAnswerLabel"/>
						</td>
					</s:if>
										
					<s:if test="columnMap.get('manuallyAdded')">
						<s:if test="manuallyAdded">
							<td>Yes</td>
						</s:if>
						<s:else>
							<td>No</td>
						</s:else>
					</s:if>
					
					<s:if test="columnMap.get('dependentAuditType')">
						<td>
							<s:if test="dependentAuditType != null && permissions.hasPermission('ManageAudits')">
								<a href="ManageAuditType.action?id=<s:property value="dependentAuditType.id"/>"><s:property value="dependentAuditTypeLabel"/></a>
							</s:if>
							<s:else>
								<s:property value="dependentAuditTypeLabel"/>
							</s:else>
						</td>
						<td>
							<s:property value="dependentAuditStatusLabel"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('createdBy')">
						<td>
							<s:property value="createdBy.name"/>
						</td>
						<td>
							<s:date name="creationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('updatedBy')">
						<td>
							<s:property value="updatedBy.name"/>
						</td>
						<td>
							<s:date name="updateDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
						</td>
					</s:if>
					
					<s:if test="columnMap.get('delete')">
						<s:if test="isCanEditRule(#r)">
							<td class="center"><a class="remove" href="#" onclick="deleteRule('#<s:property value="urlPrefix"/>Rule<s:property value="id"/>',<s:property value="id"/>); return false;"></a></td>
						</s:if>
						<s:else>
							<td>&nbsp;</td>
						</s:else>
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	<div class="info">
		No <s:property value="ruleType" /> Rules found.
	</div>
</s:else>