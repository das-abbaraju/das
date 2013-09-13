<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<ol>
	<li>
		<table class="report">
			<thead>
				<tr>
					<th>Category</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="categoryList" id="cat">
					<tr id="cat<s:property value="#cat.id" />">
						<td><a href="ManageCategory.action?id=<s:property value="#cat.id" />"><s:property value="#cat.name" /></a></td>
						<td>
							<a href="#" onclick="return showCat(<s:property value="#cat.id" />);" class="normal preview">Show Rules</a>
							<a href="#" onclick="return hideCat(<s:property value="#cat.id" />);" class="hide remove">Hide Rules</a>
							<a href="#" onclick="return showCat(<s:property value="#cat.id" />);" class="hide refresh">Refresh</a>
							<div id="catTable_<s:property value="#cat.id" />"></div>
							<s:if test="permissions.isCanAddRuleForOperator(operator)">
								<a href="CategoryRuleEditor.action?button=New&ruleAuditTypeId=1&ruleAuditCategoryId=<s:property value="#cat.id" />&ruleOperatorAccountId=<s:property value="operator.id" />"
									target="_blank" class="hide add">Add Rule</a>
							</s:if>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>				
	</li>
	<s:if test="permissions.isCanAddRuleForOperator(operator)">
		<li>
			<div id="includeNewCategory">
				<s:hidden value="%{operator.id}" name="id" />
				<s:hidden value="Add Cat" name="button" />
				<s:select list="otherCategories" headerKey="0" headerValue="- Include Another Category -" 
					listKey="id" listValue="name" name="catID" />
				<s:submit cssClass="add_rule picsbutton" value="Add Category" />
			</div>
		</li>
	</s:if>
	<pics:permission perm="ManageCategoryRules">
		<li><a href="CategoryRuleSearch.action?filter.operator=<s:property value="operator.name"/>">Search for all Category Rules specific to this operator</a></li>
	</pics:permission>
</ol>