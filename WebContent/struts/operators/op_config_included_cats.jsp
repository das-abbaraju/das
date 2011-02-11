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
								<a href="CategoryRuleEditor.action?button=New&ruleAuditTypeId=1&ruleCategoryId=<s:property value="#cat.id" />&ruleOperatorAccountId=<s:property value="operator.id" />"
									target="_blank" class="hide add">Add Rule</a>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>				
	</li>
	<s:if test="permissions.canEditAuditRules">
		<li>
			<div id="includeNewCategory">
				<s:hidden value="%{operator.id}" name="id" />
				<s:hidden value="Add Cat" name="button" />
				<s:select list="otherCategories" headerKey="0" headerValue="- Include Another Category -" 
					listKey="id" listValue="name" name="catID" />
				<s:submit cssClass="add_rule" value="Add Category" />
			</div>
		</li>
	</s:if>
	<li><a href="OperatorCategoryRules.action?id=<s:property value="id"/>">Show all Category Rules specific to this operator</a></li>
</ol>