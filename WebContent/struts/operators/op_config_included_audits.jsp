<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ol>			
	<li>			
		<table class="report">
			<thead>
				<tr>
					<th>Category</th>
					<th>Audit</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="typeList" id="type">
					<tr id="type<s:property value="#type.id" />">
						<td class="classType"><s:property value="#type.classType" /></td>
						<td><a href="ManageAuditType.action?id=<s:property value="#type.id" />"><s:property value="#type.name" /></a></td>
						<td>
							<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="normal preview">Show Rules</a>
							<a href="#" onclick="return hideType(<s:property value="#type.id" />);" class="hide remove">Hide Rules</a>
							<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="hide refresh">Refresh</a>
							<div id="typeTable_<s:property value="#type.id" />"></div>
							<s:if test="permissions.isCanAddRuleForOperator(operator)">
								<a href="AuditTypeRuleEditor.action?button=New&ruleAuditTypeId=<s:property value="#type.id" />&ruleOperatorAccountId=<s:property value="operator.id" />"
									target="_blank" class="hide add">Add Rule</a>
							</s:if>
							<div id="build_<s:property value="#type.id" />" class="hide"></div>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</li>
	<s:if test="permissions.isCanAddRuleForOperator(operator)">
		<li>
			<div id="includeNewAudit">
				<s:hidden value="%{operator.id}" name="id" />
				<s:hidden value="Add Audit" name="button" /> 
				<s:select list="otherAudits" 
					listKey="id" listValue="name" name="auditTypeID" />
				<s:submit cssClass="add_rule picsbutton" value="Add Audit" />
			</div>
		</li>
	</s:if>
	<pics:permission perm="ManageAuditTypeRules">
		<li><a href="AuditTypeRuleSearch.action?filter.operator=<s:property value="operator.name"/>">Search for all Audit Type Rules specific to this operator</a></li>
	</pics:permission>
</ol>