<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Manage Category</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/rules.css?v=<s:property value="version"/>" />
<style type="text/css">
.subCategories {
	position: relative;
	padding-left: 2em;
}
</style>
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
$(function(){
	var sortList = $('#list').sortable({
		update: function() {
			$('#list-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategory', 
				sortList.sortable('serialize').replace(/\[|\]/g,''), 
				function() {sortList.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		},
		axis: 'y'
	});

	var sortListQ = $('#listQ').sortable({
		update: function() {
			$('#listQ-info').load('OrderAuditChildrenAjax.action?id=<s:property value="category.id"/>&type=AuditCategoryQuestions', 
				sortListQ.sortable('serialize').replace(/\[|\]/g,''), 
				function() {sortListQ.effect('highlight', {color: '#FFFF11'}, 1000);}
			);
		},
		axis: 'y'
	});
});

</script>
</head>
<body>
<s:include value="manage_audit_type_breadcrumbs.jsp" />


<s:form id="save">
	<s:hidden name="id" />
	<s:if test="category.auditType != null">
		<s:hidden name="category.auditType.id" />
		<s:hidden name="parentID" value="%{category.auditType.id}" />
	</s:if>
	<s:if test="categoryParent != null">
		<s:hidden name="categoryParent.id" />
		<s:hidden name="parentID" value="%{categoryParent.ancestors.get(0).auditType.id}" />
	</s:if>
	<fieldset class="form">
	<h2 class="formLegend">Category</h2>
		<ol>
			<li><label>ID:</label>
				<s:if test="category.id > 0">
					<s:property value="category.id" />
				</s:if>
					<s:else>NEW</s:else>
			</li>
			<li><label>Category Name:</label>
				<s:textfield name="category.name" size="30" />
			</li>
			<li><label># of Questions:</label>
				<s:property value="category.numQuestions"/>
			</li>
			<li><label># Required:</label>
				<s:property value="category.numRequired"/>
			</li>
			<li><label>Page Break:</label>
				<s:checkbox name="category.pageBreak"/>
			</li>
			<li><label>Help Text:</label>
				<s:textarea name="category.helpText" cssStyle="width: 480px;" rows="5" />
			</li>
			<s:if test="category.auditType.dynamicCategories">
				<li><label>Apply on Question:</label>
					<s:textfield name="applyOnQuestionID" />
					<s:if test="applyOnQuestionID > 0"><a href="ManageQuestion.action?id=<s:property value="applyOnQuestionID" />">Show</a></s:if>
					<div class="fieldhelp">
					<h3>Apply on Question</h3>
					<p>This field is only available on audits with dynamic categories</p>
					</div>
				</li>
				<li><label>When Answer is:</label>
					<s:textfield name="category.applyOnAnswer" />
				</li>
			</s:if>
		</ol>
	</fieldset>
	<fieldset class="form submit">
		<div>
			<input type="submit" class="picsbutton positive" name="button" value="Save"/>
			<s:if test="category.subCategories.size == 0 && category.questions.size == 0">
				<input type="submit" class="picsbutton negative" name="button" value="Delete" 
					onclick="return confirm('Are you sure you want to delete this category?');" />
			</s:if>
			<input type="submit" class="picsbutton" name="button" value="UpdateAllAuditsCategories"/>	
		</div>
	</fieldset>
</s:form>

<s:if test="id != 0">
	<div>
		<h3>Subcategories</h3>
		<ul class="list" id="list" title="Drag and drop to change order">
			<s:iterator value="category.subCategories">
				<li id="item_<s:property value="id"/>" title="Drag and drop to change order">
					<s:property value="number" />.
					<a href="ManageCategory.action?id=<s:property value="id"/>">
						<s:property value="name" />
					</a>
				</li>
			</s:iterator>
		</ul>
		<s:if test="category.subCategories.size() > 0">
			<a class="preview" href="AuditCatPreview.action?categoryID=<s:property value="category.id" />&button=PreviewCategory">Preview Category</a>&nbsp;&nbsp;
		</s:if>
		<a class="add" href="ManageCategory.action?button=AddNew&parentID=<s:property value="category.parentAuditType.id"/>&categoryParent.id=<s:property value="category.id" />">Add New Sub Category</a>
		<div id="list-info"></div>
	</div>
	<br clear="all" />
	<div>
		<h3>Questions</h3>
		<ul class="list" id="listQ" title="Drag and drop to change order">
		<s:iterator value="category.questions" id="aq">
			<s:if test="#aq.current">
			    <li id="item_<s:property value="id"/>"><s:property value="number"/>.
			    <a href="ManageQuestion.action?id=<s:property value="id"/>"><s:if test="name != null"><s:property value="name.length()>100 ? name.substring(0,97) + '...' : name"/></s:if><s:else>Question has no text</s:else></a></li>
		    </s:if>
		</s:iterator>
		</ul>
		
		<a class="add" href="ManageQuestion.action?button=AddNew&parentID=<s:property value="category.id"/>&categoryParent.id=<s:property value="category.id"/>&question.category.id=<s:property value="category.id"/>">Add New Question</a>
		<div id="listQ-info"></div>
	</div>
	<pics:permission perm="ManageCategoryRules">
		<div>
			<h3>Related Rules</h3>
			<s:if test="relatedRules.size() == 0">
				<div class="alert">
					There are no rules for this category. <a href="CategoryRuleEditor.action?button=edit&rule.auditCategory.id=<s:property value="category.id"/>&rule.auditCategory.name=<s:property value="category.name"/>">Click here to create a rule for <s:property value="category.name"/></a>
				</div>
			</s:if>
			<s:else>
				<s:if test="relatedRules.size() >= 250">
					<div class="alert">
						There are too many rules to display here. <a href="CategoryRuleSearch.action?filter.category=<s:property value="category.name"/>&filter.catID=<s:property value="category.id"/>">Click here to view all rules for <s:property value="category.name"/>.</a>
					</div>
				</s:if>
				<table class="report">
					<s:set name="ruleURL" value="'CategoryRuleEditor.action'"/>
					<s:set name="categoryRule" value="true"/>
					<s:include value="rules/audit_rule_header.jsp"/>
					<s:iterator value="relatedRules" id="r">
						<s:include value="rules/audit_rule_view.jsp"/>
					</s:iterator>
				</table>
			</s:else>
			<a href="CategoryRuleEditor.action?button=New&ruleAuditTypeId=<s:property value="category.auditType.id" />&ruleAuditCategoryId=<s:property value="category.id" />" class="add">Add New Category Rule</a>
		</div>
	</pics:permission>
</s:if>

</body>
</html>