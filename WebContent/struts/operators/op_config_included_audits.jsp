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
						<td><a href="ManageAuditType.action?id=<s:property value="#type.id" />"><s:property value="#type.auditName" /></a></td>
						<td>
							<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="normal preview">Show Rules</a>
							<a href="#" onclick="return hideType(<s:property value="#type.id" />);" class="hide remove">Hide Rules</a>
							<a href="#" onclick="return showType(<s:property value="#type.id" />);" class="hide refresh">Refresh</a>
							<div id="typeTable_<s:property value="#type.id" />"></div>
								<a href="AuditTypeRuleEditor.action?button=New&ruleAuditTypeId=<s:property value="#type.id" />&ruleOperatorAccountId=<s:property value="operator.id" />"
									target="_blank" class="hide add">Add Rule</a>
							<div id="build_<s:property value="#type.id" />" class="hide"></div>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</li>
	<li>
		<div id="includeNewAudit">
			<s:hidden value="%{operator.id}" name="id" />
			<s:hidden value="Add Audit" name="button" /> 
			<s:select list="otherAudits" 
				listKey="id" listValue="auditName" name="auditTypeID" />
			<s:submit cssClass="add_audits" value="Add Audit" />
		</div>
	</li>
	<li><a href="OperatorAuditTypeRules.action?id=<s:property value="id"/>">Show all Audit Type Rules specific to this operator</a></li>
</ol>